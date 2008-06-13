package net.sf.okapi.common.resource;

public interface ICommonResource extends IBaseResource {

	/**
	 * Gets the resource name of the object.
	 * @return The resource name of the object.
	 */
	String getName ();
	
	/**
	 * Sets the resource name of the object.
	 * @param resname The new resource name to set.
	 */
	void setName (String name);
	
	/**
	 * Gets the resource type of the object.
	 * @return The resource type of the object.
	 */
	String getType ();
	
	/**
	 * Sets the resource type of the object.
	 * @param restype The resource type to set.
	 */
	void setType (String restype);
	
	/**
	 * Gets the identifier of the object. This value must be unique within the current
	 * document. It may be sequential or not, it may change depending on the parameters.
	 * It must be the same for two identical input processed with the same parameters. 
	 * @return The identifier of the object.
	 */
	String getID ();
	
	/**
	 * Sets the identifier of the object.
	 * @param id The identifier value to set.
	 */
	void setID (String id);
	
	/**
	 * Indicates if the object is translatable. Some objects may be part of the extraction 
	 * scope but because of specific parameters set in the filter they may be seen as 
	 * non-translatable.
	 * @return True if the content of the object is translatable, false otherwise.
	 */
	boolean isTranslatable ();
	
	/**
	 * Sets the flag that indicates if the object is translatable.
	 * @param isTranslatable The new value to set.
	 */
	void setIsTranslatable (boolean isTranslatable);
	
	/**
	 * Indicates if the content of this object should have its white-spaces preserved.
	 * @return True if the white-spaces should be preserved, false otherwise.
	 */
	boolean preserveSpaces ();
	
	/**
	 * Sets the flag that indicates if the white-spaces of the object should be
	 * preserved.
	 * @param preserve The new value to set.
	 */
	void setPreserveSpaces (boolean preserve);
	
	/**
	 * Sets the property value associated with a given property name.
	 * @param name The name of the property (case sensitive).
	 * @param value The new value to set.
	 */
	void setProperty (String name,
		String value);
	
	/**
	 * Gets the value associated with a given property name.
	 * @param name The name of the property (case sensitive).
	 * @return The current value associated with the given property name, this
	 * can be null. Null is also return if there is no property for the given name.
	 */
	String getProperty (String name);
	
	/**
	 * Removes the list of properties associated with the object.
	 */
	void clearProperties ();
	
	/**
	 * Sets the extension value associated with a given name.
	 * @param name The name of the extension (case sensitive).
	 * @param value The new extension to set.
	 */
	void setExtension (String name,
		IExtension value);
	
	/**
	 * Gets the extension associated with a given name.
	 * @return A resource extension, or null if there is no extension 
	 * associated with the given name.  
	 */
	IExtension getExtension (String name);
	
	/**
	 * Removes all the extensions associated with the object.
	 */
	void clearExtensions ();
	
}
