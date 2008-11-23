package net.sf.okapi.apptest.resource;

public class StartSubDocument extends BaseNameable {

	private String parentId;
	
	public StartSubDocument (String parentId) {
		super();
		setParentId(parentId);
	}

	public String getParentId () {
		return parentId;
	}
	
	public void setParentId (String parentId) {
		this.parentId = parentId;
	}
	
}
