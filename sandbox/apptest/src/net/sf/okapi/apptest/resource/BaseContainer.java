package net.sf.okapi.apptest.resource;

public class BaseContainer extends BaseResource {

	protected String name;
	protected String parentId;
	
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
