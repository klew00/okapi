package net.sf.okapi.common.filters;

import java.io.OutputStream;

import net.sf.okapi.common.IParameters;

public interface IFilterWriter {

	public String getName ();

	public void setOptions (String language,
		String defaultEncoding);
	
	public void setOutput (String path);

	public void setOutput (OutputStream output);

	public FilterEvent handleEvent (FilterEvent event);

	public void close ();

	public IParameters getParameters ();

	public void setParameters (IParameters params);

}
