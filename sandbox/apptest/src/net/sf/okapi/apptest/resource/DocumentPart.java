package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.filters.IWriterHelper;

public class DocumentPart extends BaseResource {

	public DocumentPart (String id,
		boolean isReferent)
	{
		this.id = id;
		this.isReferent = isReferent;
	}
	
	public String toString (IWriterHelper writerHelper) {
		if ( skeleton != null ) return skeleton.toString(writerHelper);
		else return "";
	}

}
