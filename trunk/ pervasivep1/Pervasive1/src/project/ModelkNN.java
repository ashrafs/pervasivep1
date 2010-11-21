package project;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.MACAddress;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.TraceEntry;

/**
 * Finds the true and estimate position based on the model computed data
 * usinf Model-based method of RADAR
 * @author Michaël Ludmann & Guillaume Depoyant
 *
 */
public class ModelkNN {

	private double pD0;
	private double n;
	private double d0;

	private GenerateTrace genTrace;
	private TraceGenerator trace;
	public ArrayList<Double> errorsTrueEsti = new ArrayList<Double>();
	private int k;
	private int offlineSize;
	private int onlineSize;
	private static PrintStream stdOut;
	private static PrintStream fileOut;
	private List<TraceEntry> onlineTrace;
	private List<TraceEntry> offlineTrace;
	private static int NB_ITER = 100;

	/**
	 * Constructor
	 * @param kParam
	 * @param pD0
	 * @param n
	 * @param d0
	 */
	public ModelkNN(int kParam, double pD0, double n, double d0)
	{
		offlineSize = 25;
		onlineSize = 5;
		FileOutputStream fOut = null;
		stdOut = System.out;
		k = kParam;
		this.pD0 = pD0;
		this.n = n;
		this.d0 = d0;
	
		genTrace = new GenerateTrace(offlineSize, onlineSize);
		trace = genTrace.getTrace();
		
		try {
			fOut = new FileOutputStream("model"+kParam+"NNresults.txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileOut = new PrintStream(fOut);
		System.setOut(fileOut);	
	}
	
	/**
     * Constructor
	 * @param kParam
	 */
	public ModelkNN(int kParam)
	{
		// Loading ModelkNN with default values for the model (cf. slide 34 from Week2SS.pdf)
		this(kParam,-33.77, 3.415, 1.0);
	}

	public void computeTrace()
	{
		//Generate traces from parsed files
		trace.generate();
		//Set trace computed from the offline and online files
		offlineTrace = trace.getOffline();	
		onlineTrace = trace.getOnline();	
	}

	/**
	 * Model-based method of RADAR
	 */
	public void computeModelkNN()
	{
		errorsTrueEsti.clear();
		System.setOut(fileOut);

		//replace each entry off the offline set by a new one computed with the model (less accurate)
		HashMap<MACAddress, Double> macHashMap = new HashMap<MACAddress, Double>();
		for(TraceEntry entry : offlineTrace) 
		{
			for(MACAddress macAddress : entry.getSignalStrengthSamples().keySet())
			{
				// Compute signal strength using a propagation model for a point af the offline set
				double pDdBm =	pD0 - 10*n*Math.log10(entry.getGeoPosition().distance(getAPPos(macAddress))/d0);
				macHashMap.put(macAddress, pDdBm);
			}
			for(MACAddress macAddress : macHashMap.keySet())
			{
				//Here we replace each signal strength in the offline set with the corresponding one from the model
				entry.getSignalStrengthSamples().remove(macAddress);
				entry.getSignalStrengthSamples().put(macAddress,macHashMap.get(macAddress));
			}
			macHashMap.clear();
		}

		// From now on, the computation is similar with the one in FingerPrintingNN.java
		ArrayList<TrueAndEstimatedPos<GeoPosition, GeoPosition>> tePosList = Neighbour.computeTrueAndEstimPos(k, onlineTrace, offlineTrace);

		for(TrueAndEstimatedPos<GeoPosition,GeoPosition> tePos : tePosList)
		{
			//Keep errors between true and estimated positions for later plotting
			errorsTrueEsti.add(tePos.getTruePos().distance(tePos.getEstimatedPos()));
			//Print true and estimated position on the same line
			System.out.println(tePos.getTruePos().getX() + ", " + tePos.getTruePos().getY() + ", " + tePos.getTruePos().getZ() + " ; "+
							   tePos.getEstimatedPos().getX() + ", " + tePos.getEstimatedPos().getY() + ", " + tePos.getEstimatedPos().getZ());
		}
		System.setOut(stdOut);
	}
	
	/**
	 * Get the GeoPosition of a given AP defined by its MAC address
	 * @param macAddress
	 * @return Position of the AP
	 */
    public static GeoPosition getAPPos(MACAddress macAddress)
    {
        if(macAddress.toString().equals("00:14:BF:B1:7C:54"))
    	{
        	return new GeoPosition(-23.626, -18.596);
    	}
        else if(macAddress.toString().equals("00:16:B6:B7:5D:8F"))
    	{
        	return new GeoPosition(-10.702, -18.596);
    	}
        else if(macAddress.toString().equals("00:14:BF:B1:7C:57"))
		{
        	return new GeoPosition(8.596, -14.62);
		}
        else if(macAddress.toString().equals("00:14:BF:B1:97:8D"))     
    	{
        	return new GeoPosition(8.538, -9.298);
    	}
        else if(macAddress.toString().equals("00:16:B6:B7:5D:9B"))     
    	{
        	return new GeoPosition(-1.93, -2.749);
    	}
        else if(macAddress.toString().equals("00:14:6C:62:CA:A4"))     
    	{
        	return new GeoPosition(4.035, -0.468);
    	}
        else if(macAddress.toString().equals("00:14:BF:3B:C7:C6"))     
    	{
        	return new GeoPosition(13.333, -2.69);
    	}
        else if(macAddress.toString().equals("00:14:BF:B1:97:8A")) 
    	{
        	return new GeoPosition(21.17, -2.69);
    	}
        else if(macAddress.toString().equals("00:14:BF:B1:97:81"))     
    	{
        	return new GeoPosition(32.398, -2.69);
    	}
        else if(macAddress.toString().equals("00:16:B6:B7:5D:8C"))     
    	{
        	return new GeoPosition(32.573, 13.86);
    	}
        else if(macAddress.toString().equals("00:11:88:28:5E:E0"))     
    	{
        	return new GeoPosition(7.135, 6.023);
    	}
        return null;
    }

    /**
	 * Main to test the class
	 * @param args : input an integer k [optionally, input also parameters for the model in this order : p(d0) n d0] 
	 */
	public static void main(String[] args) {

		if(args.length == 1)
		{
			int k = Integer.parseInt(args[0]);
			ModelkNN modelkNN = new ModelkNN(k);
			System.setOut(stdOut);
			System.out.println("Starting the "+NB_ITER+" computations for Model"+k+"NN with default values : p(d0) = -33.77 ; n = 3.415 ; d0 = 1.0 ...");
			System.setOut(fileOut);

			int i;
			for(i = 1; i<=NB_ITER; i++)
			{
				modelkNN.computeTrace();
				modelkNN.computeModelkNN();
				System.out.println("Accuracy experiment #"+i+" done");
			}
		} else if(args.length == 4)
		{
			int k = Integer.parseInt(args[0]);
			double pD0 = Double.parseDouble(args[1]);
			double n = Double.parseDouble(args[2]);
			double d0 = Double.parseDouble(args[3]);
			ModelkNN modelkNN = new ModelkNN(k, pD0, n, d0);
			
			System.setOut(stdOut);
			System.out.println("Starting the "+NB_ITER+" computations for Model"+k+"NN with p(d0) = "+pD0+" ; n = "+n+" ; d0 = "+d0+ "...");
			System.setOut(fileOut);
			
			int i;
			for(i = 1; i<=NB_ITER; i++)
			{
				modelkNN.computeTrace();
				modelkNN.computeModelkNN();
				System.out.println("Accuracy experiment #"+i+" done");
			}
		} else {
			System.out.println("Usage : ModelkNN k [p(d0) n d0]");
		}
	}
}