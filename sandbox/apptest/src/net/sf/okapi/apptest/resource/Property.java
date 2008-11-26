package net.sf.okapi.apptest.resource;

import java.util.Hashtable;

public class Property {
	
	private final String name;
	private final boolean isWriteable;
	private String value;
	private Hashtable<String, Object> annotations;

	public Property (String name, String value, boolean isWriteable) {
		this.name = name;
		this.value = value;
		this.isWriteable = isWriteable;
		annotations = new Hashtable<String, Object>();
	}
	
	@Override
	public String toString () {
		return value;
	}
	
	public String getName () {
		return name;
	}
	
	public String getValue () {
		return value;
	}
	
	public void setValue (String value) {
		this.value = value;
	}
	
	public boolean isWriteable () {
		return isWriteable;
	}
	
	public Object getAnnotation (String name) {
		return annotations.get(name);
	}
	
	public void setAnnotation (String name, Object annotation) {
		annotations.put(name, annotation);
	}
	
}
