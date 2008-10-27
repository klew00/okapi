package net.sf.okapi.apptest.resource;

import java.util.ArrayList;

public class Group extends ArrayList<IReferenceable>
	implements IReferenceable {

	private static final long serialVersionUID = 1L;
	
	protected String id;
	protected boolean isReference;
	protected String name;
	protected String parentId;
	
	public Group (String parentId) {
		this.parentId = parentId;
	}

	public Group (String parentId,
		String id)
	{
		this.parentId = parentId;
		this.id = id;
	}

	public Group (String parentId,
		String id,
		boolean isReference)
	{
		this.parentId = parentId;
		this.id = id;
		this.isReference = isReference;
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
