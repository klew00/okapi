package net.sf.okapi.apptest.resource;

import java.util.Hashtable;

import net.sf.okapi.apptest.filters.IWriterHelper;

public class TextUnit2 extends BaseResource {

	private TextFragment source;
	private Hashtable<String, Object> annotations;
	private Hashtable<String, Property> properties;
	
	public TextUnit2 () {
		source = new TextContainer();
		annotations = new Hashtable<String, Object>();
		properties = new Hashtable<String, Property>();
	}
	
	public String toString (IWriterHelper writerHelper) {
		//TODO: handle target choice
		return source.toString(writerHelper);
	}

	public TextFragment getContent () {
		return source;
	}
	
	public void setContent (TextFragment content) {
		source = content;
		// We don't change the current annotations
	}

	public Object getAnnotation (String name) {
		return annotations.get(name);
	}
	
	public void setAnnotation (String name, Object annotation) {
		annotations.put(name, annotation);
	}
	
	public void setProperty (String name, Property property) {
		properties.put(name, property);
	}
	
	public Property getProperty (String name) {
		return properties.get(name);
	}

}

