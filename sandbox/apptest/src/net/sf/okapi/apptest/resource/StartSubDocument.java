package net.sf.okapi.apptest.resource;

public class StartSubDocument extends BaseContainer {

	private String parentId;
	
	public StartSubDocument (String parentId) {
		setParentId(parentId);
	}

	public String getParentId () {
		return parentId;
	}
	
	public void setParentId (String parentId) {
		this.parentId = parentId;
	}
	
}
