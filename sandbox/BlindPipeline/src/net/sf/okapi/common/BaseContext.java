package net.sf.okapi.common;

import java.util.Hashtable;

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

public abstract class BaseContext {

	private Hashtable<String, Object> properties;
	private Annotations annotations;
	
	public String getString (String name) {
		if ( properties == null ) return null;
		return (String)properties.get(name);
	}
	
	public void setString (String name,
		String value)
	{
		if ( properties == null ) {
			properties = new Hashtable<String, Object>();
		}
		properties.put(name, value);
	}

	public boolean getBoolean (String name) {
		if ( properties == null ) return false;
		return (Boolean)properties.get(name);
	}
	
	public void setBoolean (String name,
		boolean value)
	{
		if ( properties == null ) {
			properties = new Hashtable<String, Object>();
		}
		properties.put(name, value);
	}
	
	public int getInteger (String name) {
		if ( properties == null ) return 0;
		return (Integer)properties.get(name);
	}
	
	public void setInteger (String name,
		int value)
	{
		if ( properties == null ) {
			properties = new Hashtable<String, Object>();
		}
		properties.put(name, value);
	}
	
	public void clearProperties () {
		if ( properties != null ) {
			properties.clear();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		if ( annotations == null ) return null;
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) {
			annotations = new Annotations();
		}
		annotations.set(annotation);
	}

	public void clearAnnotations () {
		if ( annotations != null ) {
			annotations.clear();
		}
	}

}
