package project;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class ScoreNN {
	
	private static ArrayList<Double> listError;
	private static double interval = 0.5;
	
	public static void computeDistance(File fileInput){
		
		double realX, realY, realZ;
		double estX, estY, estZ;
		double tmp = 0;
		
		listError = new ArrayList<Double>();
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(fileInput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

        // Here BufferedInputStream is added for fast reading.
    	BufferedInputStream bis = new BufferedInputStream(fis);
    	BufferedReader d = new BufferedReader(new InputStreamReader(bis));

        String line = null;
        String[] lineSplit ;
        
        try {
			while ((line = d.readLine()) != null) {
				
				lineSplit = line.split("[ ,;]");
				
				realX = Double.parseDouble(lineSplit[0]);
				realY = Double.parseDouble(lineSplit[2]);
				realZ = Double.parseDouble(lineSplit[4]);
				
				estX = Double.parseDouble(lineSplit[7]);
				estY = Double.parseDouble(lineSplit[9]);
				estZ = Double.parseDouble(lineSplit[11]);

				//System.out.println(realX + " " + realY + " " + realZ + " " + estX + " " + estY + " " + estZ);
				tmp = Neighbour.euclidianDist(realX, estX, realY, estY, realZ, estZ);
				//System.out.println(tmp);
				
				listError.add(tmp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void errorDistribution(){
		
		int tmp, lenRepartitionError, lenListError;
		
		ArrayList<Integer> repartitionError = new ArrayList<Integer>();
		
		for ( Double d : listError ){
			tmp = (int) (d/interval);
	
			while ( repartitionError.size() <= tmp ){
				repartitionError.add(0);
			}

			repartitionError.set(tmp, repartitionError.get(tmp)+1);
		}
		
		lenRepartitionError = repartitionError.size();
		lenListError = listError.size();
		
		for ( int i = 1; i<lenRepartitionError; i++ ){
			repartitionError.set(i, repartitionError.get(i) + repartitionError.get(i-1) );
		}		
		
		tmp = 1;

		for ( int i : repartitionError ){
			System.out.println( tmp*interval + " " + (double) i / lenListError );
			tmp++;
		}
		
	}
	
	
	public static void main(String[] args)
	{
		String nameInput = null;
		//nameInput = "model1NN_results.txt";

		if(args.length > 0 || nameInput != null )
		{
			if (nameInput == null)
				nameInput = args[0];
			
			File fileInput = new File(nameInput);
			
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream("scoreNN_"+nameInput, false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			PrintStream stdOut = System.out;
			PrintStream fileOut = new PrintStream(fOut);
			
			System.setOut(fileOut);	
	
			computeDistance(fileInput);
			errorDistribution();

			System.setOut(stdOut);
		}
		else
		{
			System.out.println("Usage : need the name of the file in argument or define it in the main l.103");
		}
	}
}


