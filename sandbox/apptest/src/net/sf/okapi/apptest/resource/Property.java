package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.annotation.Annotations;
import net.sf.okapi.apptest.common.IAnnotation;

public class Property {
	
	private final String name;
	private String value;
	private final boolean isReadOnly;
	protected Annotations annotations;

	public Property (String name, String value, boolean isReadOnly) {
		this.name = name;
		this.value = value;
		this.isReadOnly = isReadOnly;
	}
	
	@Override
	public String toString () {
		return value;
	}
	
	public Property clone () {
		Property prop = new Property(name, value, isReadOnly);
		//TODO: copy annotations?? prop.annotations = annotations.clone();
		return prop;
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
	
	public boolean isReadOnly () {
		return isReadOnly;
	}
	
	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		if ( annotations == null ) return null;
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) annotations = new Annotations();
		annotations.set(annotation);
	}

}
