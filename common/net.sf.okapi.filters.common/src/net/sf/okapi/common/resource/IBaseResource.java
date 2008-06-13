package net.sf.okapi.common.resource;

public interface IBaseResource {

	public static final int KIND_DOCUMENT   = 0;
	public static final int KIND_SKELETON   = 1;
	public static final int KIND_GROUP      = 2;
	public static final int KIND_ITEM       = 3;

	/**
	 * Gets the type of resource the object is in.
	 * @return One of the KIND_* values.
	 */
	int getKind ();
	
	/** Gets A string representation of the content in a format close to
	 * its original format. There is no guarantee that this representation will
	 * be a valid representation of the original format.
	 * @return A string representation of the content.
	 */
	String toString ();
	
	/**
	 * Gets the XML representation of the object.
	 * @return The XML representation of the object.
	 */
	String toXML ();
}
