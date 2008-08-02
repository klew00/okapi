package net.sf.okapi.common.resource2;

import java.util.ArrayList;
import java.util.Hashtable;

public class ResourceContainer extends ArrayList<IContainable>
	implements IResourceContainer, IAnnotatable
{
	private static final long serialVersionUID = 1L;

	protected String                        name;
	protected boolean                       preserveWS;
	protected Hashtable<String, String>     propList;
	protected Hashtable<String, IExtension> extList;

	
	public String getName () {
		return name;
	}

	public void setName (String value) {
		name = value;
	}

	public boolean preserveWhitespaces() {
		return preserveWS;
	}

	public void setPreserveWhitespaces (boolean value) {
		preserveWS = value;
	}

	public String getProperty (String name) {
		if ( propList == null ) return null;
		return propList.get(name);
	}

	public void setProperty (String name,
		String value)
	{
		if ( propList == null ) propList = new Hashtable<String, String>();
		propList.put(name, value);
	}
	
	public Hashtable<String, String> getProperties () {
		if ( propList == null ) propList = new Hashtable<String, String>();
		return propList;
	}

	public IExtension getExtension (String name) {
		if ( extList == null ) return null;
		return extList.get(name);
	}

	public void setExtension (String name,
		IExtension value)
	{
		if ( extList == null ) extList = new Hashtable<String, IExtension>();
		extList.put(name, value);
	}

	public Hashtable<String, IExtension> getExtensions () {
		if ( extList == null ) extList = new Hashtable<String, IExtension>();
		return extList;
	}

}
