package net.sf.okapi.common.resource2;

import java.util.Hashtable;

public interface IAnnotatable {
	
	/**
	 * Gets the name of the resource. This value is the same as the resname
	 * attribute in XLIFF.
	 * @return The name of the resource.
	 */
	public String getName ();
	
	/**
	 * Sets the name of the resource. This value is the same as the resname
	 * attribute in XLIFF.
	 * @param value The new name to set.
	 */
	public void setName (String value);
	
	/**
	 * Gets the type of the resource. This value is the same as the restype
	 * attribute in XLIFF.
	 * @return The type of the resource.
	 */
	public String getType ();
	
	/**
	 * Sets the typee of the resource. This value is the same as the restype
	 * attribute in XLIFF.
	 * @param value The new type to set.
	 */
	public void setType (String value);
	
	/**
	 * Indicates if the white spaces must be preserved in the content of the
	 * resource.
	 * @return True if the white spaces must be preserved.
	 */
	public boolean preserveWhitespaces ();
	
	/**
	 * Sets the flag indicating if the white spaces must be preserved in the
	 * content of the resource.
	 * @param value The new value of the flag.
	 */
	public void setPreserveWhitespaces (boolean value);

	public String getProperty (String name);
	
	public void setProperty (String name, String value);
	
	public Hashtable<String, String> getProperties ();
	
	public IExtension getExtension (String name);
	
	public void setExtension (String name, IExtension value);

	public Hashtable<String, IExtension> getExtensions ();
}
