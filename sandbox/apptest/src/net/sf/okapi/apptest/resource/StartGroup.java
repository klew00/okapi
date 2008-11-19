package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.filters.IWriterHelper;

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

	public String toString (IWriterHelper writerHelper) {
		if ( skeleton != null ) return skeleton.toString(writerHelper);
		else return "";
	}
	
}
