package project;
import java.io.File;
import java.io.IOException;

import org.pi4.locutil.MACAddress;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.macfilter.MacFilterExplizit;

public class GenerateTrace {

	public TraceGenerator generatedTrace;
	
	public GenerateTrace(int offlineSize, int onlineSize)
	{
		String offlineTrace = "../data/MU.1.5meters.offline.trace";
		String onlineTrace = "../data/MU.1.5meters.online.trace";

		File offlineFile = new File(offlineTrace);
		Parser offlineParser = new Parser(offlineFile);
		File onlineFile = new File(onlineTrace);
		Parser onlineParser = new Parser(onlineFile);

		MacFilterExplizit certifiedAP = new MacFilterExplizit();

		certifiedAP.add(MACAddress.parse("00:14:BF:B1:7C:54"));
		certifiedAP.add(MACAddress.parse("00:16:B6:B7:5D:8F"));
		certifiedAP.add(MACAddress.parse("00:14:BF:B1:7C:57"));
		certifiedAP.add(MACAddress.parse("00:14:BF:B1:97:8D"));
		certifiedAP.add(MACAddress.parse("00:16:B6:B7:5D:9B"));
		certifiedAP.add(MACAddress.parse("00:14:6C:62:CA:A4"));
		certifiedAP.add(MACAddress.parse("00:14:BF:3B:C7:C6"));
		certifiedAP.add(MACAddress.parse("00:14:BF:B1:97:8A"));
		certifiedAP.add(MACAddress.parse("00:14:BF:B1:97:81"));
		certifiedAP.add(MACAddress.parse("00:16:B6:B7:5D:8C"));
		certifiedAP.add(MACAddress.parse("00:11:88:28:5E:E0"));

		offlineParser.setMacFilter(certifiedAP);
		onlineParser.setMacFilter(certifiedAP);

		try {
			generatedTrace = new TraceGenerator(offlineParser, onlineParser,offlineSize,onlineSize);
		} catch (NumberFormatException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public TraceGenerator getTrace()
	{
		return generatedTrace;
	}
}
