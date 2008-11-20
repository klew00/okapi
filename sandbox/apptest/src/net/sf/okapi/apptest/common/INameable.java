package net.sf.okapi.apptest.common;

import net.sf.okapi.apptest.resource.Property;

public interface INameable {

	public String getName () ;
	
	public void setName (String name);
	
	public Property getProperty (String name);

	public void setProperty (Property property);
	
}
