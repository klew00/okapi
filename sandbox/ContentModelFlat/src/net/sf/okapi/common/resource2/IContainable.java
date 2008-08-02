package net.sf.okapi.common.resource2;

public interface IContainable {

	/**
	 * Gets the ID of the object.
	 * @return The ID of the object (can be null).
	 */
	public String getID ();
	
	/**
	 * Sets the ID of the object.
	 * @param value The new ID to set.
	 */
	public void setID (String value);
	
	/**
	 * Indicates if the object has any content.
	 * @return True if there is non-empty content, false otherwise.
	 */
	public boolean isEmpty ();

}
