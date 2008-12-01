package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.IReferenceable;

public class BaseReferenceable extends BaseNameable implements IReferenceable {

	protected boolean isReferent;
	protected String parentId;
	
	public boolean isReferent () {
		return isReferent;
	}

	public void setIsReferent (boolean value) {
		isReferent = value;
	}

	/**
	 * Gets the ID of the parent resource of this resource.
	 * @return The ID of this resource's parent, or null if there is none.
	 */
	public String getParentId () {
		return parentId;
	}
	
	/**
	 * Sets the ID of the parent resource of this resource.
	 * @param id The ID to set.
	 */
	public void setParentId (String id) {
		parentId = id;
	}

}
