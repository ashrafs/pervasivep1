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
	
	//Returns a list of true and estimated positions based on k nearest neighbors
	public static ArrayList<TrueAndEstimatedPos<GeoPosition,GeoPosition>> getEstimatedPositions(int k, List<TraceEntry> onlineTrace, List<TraceEntry> offlineTrace)
	{
		ArrayList<TrueAndEstimatedPos<GeoPosition,GeoPosition>> teList = new ArrayList<TrueAndEstimatedPos<GeoPosition,GeoPosition>>();
		
		for(TraceEntry entry: onlineTrace) {

			double ss1, ss2, ss3;			
			LinkedList<EntryWithDist> bestEntries = new LinkedList<EntryWithDist>();
			
			if(entry.getSignalStrengthSamples().getSortedAccessPoints().size() > 2)
			{
				List<MACAddress> arr = entry.getSignalStrengthSamples().getSortedAccessPoints().subList(0, 3);
				
				ss1 = entry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(0));
				ss2 = entry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(1));
				ss3 = entry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(2));

				for(TraceEntry offEntry: offlineTrace) {

					if(offEntry.getSignalStrengthSamples().keySet().containsAll(arr))
					{
						double m1, m2, m3;
						m1 = offEntry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(0));
						m2 = offEntry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(1));
						m3 = offEntry.getSignalStrengthSamples().getAverageSignalStrength(arr.get(2));

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
				GeoPosition truePos = new GeoPosition(entry.getGeoPosition().getX() , entry.getGeoPosition().getY(), entry.getGeoPosition().getZ());

				//Compute estimated position using the first k best entries
				double estimX = 0, estimY = 0, estimZ = 0;
				int i;
				for(i = 0; i < k; i++)
				{
					estimX += bestEntries.get(i).traceEntry.getGeoPosition().getX();
					estimY += bestEntries.get(i).traceEntry.getGeoPosition().getY();
					estimZ += bestEntries.get(i).traceEntry.getGeoPosition().getZ();
				}
				estimX /= k; //mean position X
				estimY /= k; //mean position Y
				estimZ /= k; //mean position Z
				GeoPosition estimatedPos = new GeoPosition(estimX, estimY, estimZ);
				
				teList.add(new TrueAndEstimatedPos<GeoPosition,GeoPosition>(truePos,estimatedPos));
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
	public int compare(EntryWithDist o1, EntryWithDist o2) {
		return o2.compareTo(o1);	
	}
}