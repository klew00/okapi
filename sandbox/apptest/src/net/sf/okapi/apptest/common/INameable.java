package net.sf.okapi.apptest.common;

import java.util.Iterator;

import net.sf.okapi.apptest.resource.Property;

public interface INameable {

	public String getName () ;
	
	public void setName (String name);
	
	public Property getProperty (String name);

	public void setProperty (Property property);
	
	public Iterator<String> propertyNames ();

	public boolean hasTargetProperty (String language,
		String name);

	public Property getTargetProperty (String language,
		String name);
	
	public Property getTargetProperty (String language,
		String name,
		int creationOptions);
	
	public void setTargetProperty (String language,
		Property property);
	
	public Iterator<String> targetPropertyNames (String language);

}
