package project;

public class ModelNN {

	private static final int NB_ITER = 100;
	
	public static void main(String[] args) {

		System.out.println("Starting the "+NB_ITER+" computations for modelNN...");
		ModelkNN modelkNN = new ModelkNN(1);
		
		for(int i = 1; i <= NB_ITER; i++) {
			modelkNN.computeTrace();
			modelkNN.computeModelkNN();
			System.out.println("Accuracy experiment #"+i+" done");
		}
	}
}
