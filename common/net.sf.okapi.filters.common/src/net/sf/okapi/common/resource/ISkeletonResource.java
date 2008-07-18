package net.sf.okapi.common.resource;

public interface ISkeletonResource extends IBaseResource {

	/**
	 * Sets the skeleton data for the object.
	 * @param text The new text data to set.
	 */
	void setData (String text);
	
	/**
	 * Appends skeleton data at the end of the current data.
	 * @param text The text data to append.
	 */
	void appendData (String text);
	
	/**
	 * Appends skeleton data at the end of the current data.
	 * @param text The StringBuilder object to append.
	 */
	void appendData (StringBuilder text);
	
	/**
	 * Indicates if the object contains no skeleton data.
	 * @return True if the object is empty.
	 */
	boolean isEmpty ();

	/**
	 * Sets the offset information for this skeleton object.
	 * @param offset Start of the skeleton data position.
	 * @param length Length in character of the data.
	 */
	void setSkeleton (long offset,
		int length);

	/**
	 * Indicates if the data of this skeleton resource is offset-based (vs data buffer).
	 * @return True if the data are offset-based.
	 */
	boolean isOffsetBased ();
}
