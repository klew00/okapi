package net.sf.okapi.common.resource;

import java.util.Hashtable;

public class LocaleProperties {

	private Hashtable<String, String>  props;
	
	
	public String getProperty (String name) {
		if ( props == null ) return null;
		return props.get(name);
	}
	
	public void setProperty (String name,
		String value)
	{
		if ( props == null ) props = new Hashtable<String, String>();
		props.put(name, value);
	}
	
	public Hashtable<String, String> getProperties () {
		if ( props == null ) props = new Hashtable<String, String>();
		return props;
	}

}

/*
Group
	LocalizableProperties source;
	List<LocalizableProperties> targets

LocalizableData extends LocalizableProperties

TextUnit
	LocalizableData source
	List<LocalizableData> targets;

	source.getproperties();
targets.get(0).getProperties

	*/

