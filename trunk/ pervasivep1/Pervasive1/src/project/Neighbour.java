package project;

import java.util.*;

import org.pi4.locutil.MACAddress;
import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.trace.TraceEntry;

public class Neighbour {
	
	public static double euclidianDist(double x1, double x2, double y1, double y2, double z1, double z2) 
    {
            return Math.sqrt(Math.pow(x1 - x2,2) + Math.pow(y1 - y2,2) + Math.pow(z1 - z2, 2));
    }
	
	/** 
	 * Compute and return a list of true and estimated positions
	 * based on k nearest neighbors
	 * 
	 * @param k
	 * @param onlineTrace
	 * @param offlineTrace
	 * @return
	 */
	public static ArrayList<TrueAndEstimatedPos<GeoPosition,GeoPosition>> computeTrueAndEstimPos(int k, List<TraceEntry> onlineTrace, List<TraceEntry> offlineTrace)
	{	
		ArrayList<TrueAndEstimatedPos<GeoPosition,GeoPosition>> teList = new ArrayList<TrueAndEstimatedPos<GeoPosition,GeoPosition>>();
		
		for(TraceEntry entry: onlineTrace) {

			double ss1, ss2, ss3;			
			LinkedList<EntryWithDist> bestEntries = new LinkedList<EntryWithDist>();
			
			if(entry.getSignalStrengthSamples().getSortedAccessPoints().size() > 2)
			{
				List<MACAddress> arr = entry.getSignalStrengthSamples().getSortedAccessPoints().subList(0, 3);
				// Compute Nearest Neighbour as specified in slides 39-40 from Week2SS.pdf
				ss1 = entry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(0)); 
				ss2 = entry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(1));
				ss3 = entry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(2));

				for(TraceEntry offEntry: offlineTrace) {

					if(offEntry.getSignalStrengthSamples().keySet().containsAll(arr))
					{
						double m1 = offEntry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(0));
						double m2 = offEntry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(1));
						double m3 = offEntry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(2));
						double euclSignStrSpaceDist = euclidianDist(ss1, m1, ss2, m2, ss3, m3);
						if(bestEntries.size() < k)
						{
							bestEntries.add(new EntryWithDist(offEntry, euclSignStrSpaceDist));
						} else {
							if(bestEntries.getFirst().distance > euclSignStrSpaceDist)
							{
								bestEntries.set(0, new EntryWithDist(offEntry, euclSignStrSpaceDist));
							}
						}
						// Sort available AP by signal strength
						DistComparator comparator = new DistComparator();
						Collections.sort(bestEntries,comparator);
					}
				}
				
				//Compute true position
				GeoPosition tPos = new GeoPosition(entry.getGeoPosition().getX() , entry.getGeoPosition().getY(), entry.getGeoPosition().getZ());

				//Compute estimated position using the first k best entries
				double estimX = 0, estimY = 0, estimZ = 0;
				int i;
				for(i = 0; i < k; i++)
				{
					estimX = estimX + bestEntries.get(i).traceEntry.getGeoPosition().getX();
					estimY = estimY + bestEntries.get(i).traceEntry.getGeoPosition().getY();
					estimZ = estimZ + bestEntries.get(i).traceEntry.getGeoPosition().getZ();
				}
				estimX = estimX/k; estimY = estimY/k; estimZ = estimZ/k;  // create average position
				GeoPosition ePos = new GeoPosition(estimX, estimY, estimZ);				
				teList.add(new TrueAndEstimatedPos<GeoPosition,GeoPosition>(tPos,ePos));
			}
		}
		return teList;
	}
}

class EntryWithDist
{
    public TraceEntry traceEntry;
    public double distance;
    
    public EntryWithDist(TraceEntry traceEntry, double distance)
    {
        this.traceEntry = traceEntry; 
        this.distance = distance;
    }
    
    public int compareTo(EntryWithDist entry)
    {
        return Double.compare(entry.distance, this.distance);
    }
    
    public String toString()
    {
        return String.valueOf(distance);
    }
}

class DistComparator implements Comparator<EntryWithDist> {
	public int compare(EntryWithDist ewd1, EntryWithDist ewd2) {
		return ewd1.compareTo(ewd2);   
	}
}