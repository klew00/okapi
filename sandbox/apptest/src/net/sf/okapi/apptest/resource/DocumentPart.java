package net.sf.okapi.apptest.resource;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.sf.okapi.apptest.filters.IWriterHelper;

public class DocumentPart extends BaseResource {

	private Hashtable<String, Property> properties;

	public DocumentPart (String id,
		boolean isReferent)
	{
		this.id = id;
		this.isReferent = isReferent;
	}
	
	public String toString (IWriterHelper writerHelper) {
		if ( skeleton != null ) return skeleton.toString(writerHelper);
		else return "";
	}

	public Property getProperty (String name) {
		if ( name == null ) throw new InvalidParameterException();
		if ( properties == null ) return null;
		return properties.get(name);
	}

	public void setProperty (Property property) {
		if ( property == null ) throw new InvalidParameterException();
		if ( properties == null ) properties = new Hashtable<String, Property>();
		properties.put(property.getName(), property);
	}
}
