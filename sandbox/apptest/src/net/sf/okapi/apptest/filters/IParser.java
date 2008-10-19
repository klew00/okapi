package net.sf.okapi.apptest.filters;

import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.resource.IContainable;

public interface IParser {

	public void setOptions (String language, String defaultEncoding);
	
	public void open(InputStream input);

	public void open(CharSequence inputText);

	public void open(URL inputURL);

	public IContainable getResource();

	public boolean hasNext ();
	
	public int next();

	public void close();

	public void cancel ();
	
	public IParameters getParameters ();
	
	public void setParameters (IParameters params);
}
