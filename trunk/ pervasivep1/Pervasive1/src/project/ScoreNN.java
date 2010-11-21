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

public class ScoreNN {
	
	public static void computeDistance(File fileInput){
		
		double realX, realY, realZ;
		double estX, estY, estZ;
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(fileInput);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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
				System.out.println(Neighbour.euclidianDist(realX, estX, realY, estY, realZ, estZ));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		String nameInput = null;
		//nameInput = "fingerPrinting1NNresults.txt";
		
		if(args.length > 0 || nameInput != null )
		{
			if (nameInput == null)
				nameInput = args[0];
			
			File fileInput = new File(nameInput);
			
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream("scoreNN.txt", false);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PrintStream stdOut = System.out;
			PrintStream fileOut = new PrintStream(fOut);
			
			System.setOut(fileOut);	
	
			computeDistance(fileInput);
			
			System.setOut(stdOut);	
		}
		else
		{
			System.out.println("Usage : need the name of the file in argument or define it in the main l.67");
		}
	}
}


