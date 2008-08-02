package net.sf.okapi.common.resource2;

import java.util.Hashtable;

public interface IAnnotatable {
	
	public String getName ();
	
	public void setName (String value);
	
	public boolean preserveWhitespaces ();
	
	public void setPreserveWhitespaces (boolean value);

	public String getProperty (String name);
	
	public void setProperty (String name, String value);
	
	public Hashtable<String, String> getProperties ();
	
	public IExtension getExtension (String name);
	
	public void setExtension (String name, IExtension value);

	public Hashtable<String, IExtension> getExtensions ();
}
