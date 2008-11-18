package net.sf.okapi.apptest.resource;

import java.util.Hashtable;

public class Property {
	
	private String name;
	private String value;
	private boolean isWriteable;
	private Hashtable<String, Object> annotations;

	public Property (String name, String value, boolean isWriteable) {
		this.name = name;
		this.value = value;
		this.isWriteable = isWriteable;
		annotations = new Hashtable<String, Object>();
	}
	
	public String getName () {
		return name;
	}
	
	public String getValue () {
		return value;
	}
	
	public boolean isWriteable () {
		return isWriteable;
	}
	
	public void setValue (String value) {
		this.value = value;
	}
	
	public Object getAnnotation (String name) {
		return annotations.get(name);
	}
	
	public void setAnnotation (String name, Object annotation) {
		annotations.put(name, annotation);
	}
	
}
