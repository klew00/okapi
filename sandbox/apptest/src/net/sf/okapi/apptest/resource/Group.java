package net.sf.okapi.apptest.resource;

import java.util.ArrayList;

public class Group extends ArrayList<IReferenceable>
	implements IReferenceable {

	private static final long serialVersionUID = 1L;
	
	protected String id;
	protected boolean isReference;
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
	
	public Group (String parentId) {
		setParentID(parentId);
	}

	public boolean isReference () {
		return isReference;
	}

	public void setIsReference (boolean value) {
		isReference = value;
	}

	public String getID () {
		return id;
	}

	public void setID (String id) {
		this.id = id;
	}

}
