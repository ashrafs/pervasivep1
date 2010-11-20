package project;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.TraceEntry;

public class FingerPrintingkNN {

	private int k;
	private int offlineSize;
	private int onlineSize;
	private GenerateTrace genTrace;
	private TraceGenerator trace;
	private static PrintStream fileOut;
	private static PrintStream stdOut;
	private List<TraceEntry> onlineTrace;
	private List<TraceEntry> offlineTrace;
	public Vector<Double> errorsTrueEsti = new Vector<Double>();
	private static int NB_ITER = 100;
	
	public FingerPrintingkNN(int kParam) {
		
		offlineSize = 25;
		onlineSize = 5;
		
		k = kParam;
		FileOutputStream fOut = null;
		stdOut = System.out;

		genTrace = new GenerateTrace(offlineSize, onlineSize);
		trace = genTrace.getTrace();

		try {
			fOut = new FileOutputStream("fingerPrinting" + k + "NNresults.txt", true);
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
	
	void computeFingerPrint()
	{
		errorsTrueEsti.clear();
		System.setOut(fileOut);
		ArrayList<TrueAndEstimatedPos<GeoPosition,GeoPosition>> tePosVector = Neighbour.getEstimatedPositions(k, onlineTrace, offlineTrace);

		for(TrueAndEstimatedPos<GeoPosition,GeoPosition> tePos : tePosVector)
		{
			//Print true and estimated position on the same line
			System.out.println(tePos.getTruePos().getX() + ", " + tePos.getTruePos().getY() + ", " + tePos.getTruePos().getZ() + " ; "+
							   tePos.getEstimatedPos().getX() + ", " + tePos.getEstimatedPos().getY() + ", " + tePos.getEstimatedPos().getZ());

			//Keep errors between true and estimated positions for later plotting
			errorsTrueEsti.add(tePos.getTruePos().distance(tePos.getEstimatedPos()));
		}
		System.setOut(stdOut);
	}

	public static void main(String[] args) {

		if(args.length > 0)
		{
			int k = Integer.parseInt(args[0]);
			FingerPrintingkNN fingerPrintingkNN = new FingerPrintingkNN(k);
			
			System.setOut(stdOut);
			System.out.println("Starting the "+NB_ITER+" computations for fingerPrinting"+k+"NN...");
			System.setOut(fileOut);

			for(int i = 1; i <= NB_ITER; i++) {

				fingerPrintingkNN.computeTrace();
				fingerPrintingkNN.computeFingerPrint();
				System.out.println("Accuracy experiment #"+i+" done");
			}
		}
		else
		{
			System.out.println("Usage : FingerPrintkNN k");
		}
	}
}