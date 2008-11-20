package net.sf.okapi.apptest.resource;

import java.security.InvalidParameterException;
import java.util.Hashtable;

import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;

public class BaseContainer implements IResource, INameable {

	protected String id;
	protected ISkeleton skeleton;
	protected String name;
	protected Hashtable<String, Property> properties;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}

	public ISkeleton getSkeleton () {
		return skeleton;
	}
	
	public void setSkeleton (ISkeleton skeleton) {
		this.skeleton = skeleton;
	}

	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
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
