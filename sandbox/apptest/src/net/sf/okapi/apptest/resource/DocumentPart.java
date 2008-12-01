package net.sf.okapi.apptest.resource;

public class DocumentPart extends BaseReferenceable {

	/**
	 * Creates a new DocumentPart object.
	 * @param id The ID of the resource.
	 * @param isReferent Indicates if this resource is a referent (i.e. is referred to
	 * by another resource) or not.
	 */
	public DocumentPart (String id,
		boolean isReferent)
	{
		this.id = id;
		this.isReferent = isReferent;
	}
	
}
