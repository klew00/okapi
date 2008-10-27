package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.IResource;

public class BaseResource implements IResource {

	protected String id;
	
	public String getID () {
		return id;
	}
	
	public void setID (String id) {
		this.id = id;
	}

}
