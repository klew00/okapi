package net.sf.okapi.filters.openoffice;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilterWriter;

public class FilterWriter implements IFilterWriter {

	private Parameters params;
	private OutputStreamWriter writer;

	public void close() {
		try {
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public String getName () {
		return "OpenOfficeFilterWriter";
	}

	public IParameters getParameters () {
		return params;
	}

	public void handleEvent (FilterEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		// TODO Auto-generated method stub
		
	}

	public void setOutput (String path) {
		// TODO Auto-generated method stub
		
	}

	public void setOutput (OutputStream output) {
		// TODO Auto-generated method stub
		
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

}
