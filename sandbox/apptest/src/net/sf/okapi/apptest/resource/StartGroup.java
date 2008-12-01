package net.sf.okapi.apptest.resource;

public class StartGroup extends BaseReferenceable {

	/**
	 * Creates a new StartGroup object.
	 * @param parentId The ID of the parent resource for this resource.
	 */
	public StartGroup (String parentId) {
		super();
		this.parentId = parentId;
	}

	public StartGroup (String parentId,
		String id)
	{
		super();
		this.parentId = parentId;
		this.id = id;
	}

	public StartGroup (String parentId,
		String id,
		boolean isReference)
	{
		super();
		this.parentId = parentId;
		this.id = id;
		this.isReferent = isReference;
	}

}
