package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.IResource;

public class BaseContainer implements IResource {

	protected String id;
	protected String name;
	protected String parentId;
	
	public String getID () {
		return id;
	}
	
	public void setID (String id) {
		this.id = id;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getParentID () {
		return parentId;
	}
	
	public void setParentID (String parentId) {
		this.parentId = parentId;
	}
	
}
