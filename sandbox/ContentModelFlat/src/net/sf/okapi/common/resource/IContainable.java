package net.sf.okapi.common.resource;

public interface IContainable {

	/**
	 * Gets the extraction ID of the resource.
	 * @return The ID of the resource.
	 */
	public String getID ();
	
	/**
	 * Sets the extraction ID of the resource.
	 * @param value The new ID to set.
	 */
	public void setID (String value);
	
	/**
	 * Indicates if the resource has any content.
	 * @return True if there is non-empty content, false otherwise.
	 */
	public boolean isEmpty ();

}
