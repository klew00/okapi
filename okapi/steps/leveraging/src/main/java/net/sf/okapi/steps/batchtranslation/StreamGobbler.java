package net.sf.okapi.steps.batchtranslation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
	
	private InputStream inStream;
	private String display;

	public StreamGobbler (InputStream inStream,
		String display)
	{
		this.inStream = inStream;
		this.display = display;
	}

	public void run () {
		try {
			InputStreamReader isr = new InputStreamReader(inStream);
			BufferedReader br = new BufferedReader(isr);
			String line=null;
			// We cannot use the log because the thread is different 
			while ( (line = br.readLine()) != null)
				System.out.println(display + "> " + line);
		}
		catch ( IOException e ) {
			e.printStackTrace();  
		}
	}
}
