package project;

public class FingerPrintingNN {
	
	private static final int NB_ITER = 100;
	
	public static void main(String[] args) {

		System.out.println("Starting the "+NB_ITER+" computations for fingerPrintingNN...");
		FingerPrintingkNN fingerPrintingkNN = new FingerPrintingkNN(1);
		
		for(int i = 1; i <= NB_ITER; i++) {
			fingerPrintingkNN.computeTrace();
			fingerPrintingkNN.computeFingerPrintkNN();
			System.out.println("Accuracy experiment #"+i+" done");
		}
	}
}
