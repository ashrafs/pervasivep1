package experiments;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.MACAddress;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.TraceEntry;

import project.GenerateTrace;

public class ComputeDataPlot1 {

	private GenerateTrace genTrace;
	private TraceGenerator trace;
	private PrintStream stdOut;
	private PrintStream fileOut;
	private ArrayList<TraceEntry> offlineTrace;
	private MACAddress macAddress;
	
	public ComputeDataPlot1(MACAddress macAddress, int offlineSize, int onlineSize)
	{
		genTrace = new GenerateTrace(offlineSize, onlineSize);
		trace = genTrace.getTrace();
		FileOutputStream fOut = null;
		
		try {
			fOut = new FileOutputStream("dataPlot1" + macAddress.toString().replaceAll(":", ".") + ".txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.macAddress = macAddress;
		stdOut = System.out;
		System.setOut(fileOut = new PrintStream(fOut));
	}
	
	public void computeTrace()
	{
		//Generate traces from parsed files
		trace.generate();
		//Set trace computed from the offline file
		offlineTrace = trace.getOffline();	
	}

	void computeDist()
	{
		System.setOut(fileOut);
		for(TraceEntry entry: offlineTrace)
		{
			if(entry.getSignalStrengthSamples().containsKey(macAddress))
			{
				// Print the distance of each AP with the associated signal strength
				System.out.println((getAPPos(macAddress).distance(entry.getGeoPosition())) + " " + entry.getSignalStrengthSamples().getAverageSignalStrength(macAddress));
			}
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
	
	public static void main(String[] args) {

		ArrayList<MACAddress> certifiedMac = new ArrayList<MACAddress>();
		certifiedMac.add(MACAddress.parse("00:14:BF:B1:7C:54"));
		certifiedMac.add(MACAddress.parse("00:16:B6:B7:5D:8F"));
		certifiedMac.add(MACAddress.parse("00:14:BF:B1:7C:57"));
		certifiedMac.add(MACAddress.parse("00:14:BF:B1:97:8D"));
		certifiedMac.add(MACAddress.parse("00:16:B6:B7:5D:9B"));
		certifiedMac.add(MACAddress.parse("00:14:6C:62:CA:A4"));
		certifiedMac.add(MACAddress.parse("00:14:BF:3B:C7:C6"));
		certifiedMac.add(MACAddress.parse("00:14:BF:B1:97:8A"));
		certifiedMac.add(MACAddress.parse("00:14:BF:B1:97:81"));
		certifiedMac.add(MACAddress.parse("00:16:B6:B7:5D:8C"));
		certifiedMac.add(MACAddress.parse("00:11:88:28:5E:E0"));
		
		for(MACAddress macAddress : certifiedMac)
		{
			ComputeDataPlot1 computeDataPlot1 = new ComputeDataPlot1(macAddress, 25, 5);
			computeDataPlot1.computeTrace();
			computeDataPlot1.computeDist();
		}

	}
}
