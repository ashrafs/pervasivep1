package project;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.TraceEntry;

/**
 * Finds the true and estimate position based on the empirical data
 * available in the online and offline datasets
 * @author Michaël Ludmann & Guillaume Depoyant
 *
 */
public class FingerPrintingkNN {

	private int k;
	private int offlineSize;
	private int onlineSize;
	private GenerateTrace genTrace;
	private TraceGenerator trace;
	private static PrintStream fileOut;
	private static PrintStream stdOut;
	private ArrayList<TraceEntry> onlineTrace;
	private ArrayList<TraceEntry> offlineTrace;
	public ArrayList<Double> errorsTrueEsti = new ArrayList<Double>();
	private static int NB_ITER = 100;
	
	/**
     * Constructor
	 * @param kParam
	 */
	public FingerPrintingkNN(int kParam) {
		
		offlineSize = 25;
		onlineSize = 5;
		
		k = kParam;
		FileOutputStream fOut = null;
		stdOut = System.out;

		genTrace = new GenerateTrace(offlineSize, onlineSize);
		trace = genTrace.getTrace();

		try {
			fOut = new FileOutputStream("fingerPrinting" + k + "NN_results.txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		fileOut = new PrintStream(fOut);
		System.setOut(fileOut);	
	}
	
	public void computeTrace()
	{
		//Generate traces from parsed files
		trace.generate();
		//Set trace computed from the offline and online files
		offlineTrace = trace.getOffline();	
		onlineTrace = trace.getOnline();	
	}
	
	public void computeFingerPrintkNN()
	{
		errorsTrueEsti.clear();
		System.setOut(fileOut);
		ArrayList<TrueAndEstimatedPos<GeoPosition,GeoPosition>> tePosList = Neighbour.computeTrueAndEstimPos(k, onlineTrace, offlineTrace);

		for(TrueAndEstimatedPos<GeoPosition,GeoPosition> tePos : tePosList)
		{
			//Keep errors between true and estimated positions for later plotting
//			errorsTrueEsti.add(tePos.getTruePos().distance(tePos.getEstimatedPos()));
			//Print true and estimated position on the same line
			System.out.println(tePos.getTruePos().getX() + ", " + tePos.getTruePos().getY() + ", " + tePos.getTruePos().getZ() + " ; "+
							   tePos.getEstimatedPos().getX() + ", " + tePos.getEstimatedPos().getY() + ", " + tePos.getEstimatedPos().getZ());
		}
		System.setOut(stdOut);
	}

	/**
	 * Main to test the class
	 * @param args : input an integer k
	 */
	public static void main(String[] args) {

		if(args.length > 0)
		{
			int paramK = Integer.parseInt(args[0]);
			FingerPrintingkNN fingerPrintingkNN = new FingerPrintingkNN(paramK);
			
			System.setOut(stdOut);
			System.out.println("Starting the "+NB_ITER+" computations for fingerPrinting"+paramK+"NN...");
			System.setOut(fileOut);

			for(int i = 1; i <= NB_ITER; i++) {

				fingerPrintingkNN.computeTrace();
				fingerPrintingkNN.computeFingerPrintkNN();
				System.out.println("Accuracy experiment #"+i+" done");
			}
		}
		else
		{
			System.out.println("Usage : FingerPrintkNN k");
		}
	}
}