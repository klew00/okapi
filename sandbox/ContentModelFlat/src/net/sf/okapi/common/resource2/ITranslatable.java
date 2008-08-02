package net.sf.okapi.common.resource2;

public interface ITranslatable extends IContainable {

	/**
	 * Indicates if the content of the object is translatable.
	 * @return True if the content is translatable, false otherwise.
	 */
	public boolean isTranslatable ();
	
	/**
	 * Sets the flag indicating if the content of the object is translatable.
	 * @param value The new value to set.
	 */
	public void setIsTranslatable (boolean value);
	
	/**
	 * Gets the current parent of the object.
	 * @return The parent of the object or null if it has no parent.
	 */
	public ITranslatable getParent ();
	
	/**
	 * Sets the parent for this object.
	 * @param value The new parent of this object.
	 */
	public void setParent (ITranslatable value);

}
