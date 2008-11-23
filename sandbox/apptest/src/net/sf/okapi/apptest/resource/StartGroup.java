package net.sf.okapi.apptest.resource;

public class StartGroup extends BaseResource {

	public StartGroup (String parentId) {
		this.parentId = parentId;
	}

	public StartGroup (String parentId,
		String id)
	{
		this.parentId = parentId;
		this.id = id;
	}

	public StartGroup (String parentId,
		String id,
		boolean isReference)
	{
		this.parentId = parentId;
		this.id = id;
		this.isReferent = isReference;
	}

}
