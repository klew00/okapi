package net.sf.okapi.apptest.filters;

import java.io.OutputStream;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;

public interface IOutputFilter {

	public String getName ();
	
	public void setOptions (String language,
		String defaultEncoding);
	
	public void setOutput (String outputPath);
	
	public void setOutput (OutputStream outputStream);
	
	public void handleEvent (int eventType,
		IResource resource);
	
	public void close ();

	public IParameters getParameters ();
	
	public void setParameters (IParameters params);

}
