package net.sf.okapi.apptest.resource;

import java.util.Hashtable;
import java.util.Set;

import net.sf.okapi.apptest.annotation.Annotations;
import net.sf.okapi.apptest.common.IAnnotation;

public class TextContainer {

	protected TextFragment text;
	protected Hashtable<String, Property> properties;
	protected Annotations annotations;
	
	public TextContainer () {
		create(null);
	}

	public TextContainer (String sourceText) {
		create(sourceText);
	}
	
	private void create (String sourceText) {
		text = new TextFragment();
		if ( sourceText != null ) text.append(sourceText);
		annotations = new Annotations();
	}

	@Override
	public String toString () {
		return text.toString();
	}
	
	public TextFragment getContent () {
		return text;
	}
	
	public void setContent (TextFragment content) {
		text = content;
		// We don't change the current annotations
	}

	public boolean hasProperty (String name) {
		return (getProperty(name) != null);
	}
	
	public Property getProperty (String name) {
		if ( properties == null ) return null;
		return properties.get(name);
	}

	public Property setProperty (Property property) {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		properties.put(property.getName(), property);
		return property;
	}
	
	public Set<String> getPropertyNames () {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		return properties.keySet();
	}

	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		annotations.set(annotation);
	}

}
