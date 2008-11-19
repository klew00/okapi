package net.sf.okapi.apptest.resource;

import java.security.InvalidParameterException;
import java.util.Hashtable;

import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;

public abstract class BaseResource implements IResource, IReferenceable {

	protected String id;
	protected ISkeleton skeleton;
	protected boolean isReferent;
	protected String name;
	protected String parentId;
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

	public boolean isReferent () {
		return isReferent;
	}

	public void setIsReferent (boolean value) {
		isReferent = value;
	}

	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getParentId () {
		return parentId;
	}
	
	public void setParentId (String parentId) {
		this.parentId = parentId;
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
