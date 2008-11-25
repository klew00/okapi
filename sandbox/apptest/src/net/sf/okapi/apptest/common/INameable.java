package net.sf.okapi.apptest.common;

import net.sf.okapi.apptest.resource.Property;

public interface INameable {

	public String getName () ;
	
	public void setName (String name);
	
	public Property getProperty (String name);

	public void setProperty (Property property);
	
	public Property getTargetProperty (String language, String name, int creationOptions);
	
	public void setTargetProperty (String language, Property property);
	
}
