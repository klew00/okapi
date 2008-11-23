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
	
	public String getParentId () {
		return parentId;
	}
	
	public void setParentId (String id) {
		parentId = id;
	}

}
