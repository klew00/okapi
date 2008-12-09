package net.sf.okapi.common.resource;

import java.util.Set;


public interface INameable {

	/**
	 * Gets the name of this resource. The resource name is the equivalent of the XLIFF resname attribute.
	 * @return This resource name, or null if there is none.
	 */
	public String getName () ;
	
	/**
	 * Sets the name of this resource. The resource name is the equivalent of the XLIFF resname attribute.
	 * @param name New name to set.
	 */
	public void setName (String name);

	/**
	 * Gets the type information associated with this resource.
	 * @return The type information associated with this resource.
	 */
	public String getType ();
	
	/**
	 * Sets the type information associated with this resource.
	 * @param value The new type information.
	 */
	public void setType (String value);
	
	/**
	 * Gets the resource-level property for a given name.
	 * @param name Name of the property to retrieve.
	 * @return The property or null if it does not exist.
	 */
	public Property getProperty (String name);

	/**
	 * Sets a resource-level property. If a property already exists it is overwritten.
	 * @param property The new property to set.
	 * @return The property that has been set.
	 */
	public Property setProperty (Property property);
	
	/**
	 * Gets the names of all the resource-level properties for this resource.
	 * @return All the names of the resource-level properties for this resource.
	 */
	public Set<String> getPropertyNames ();

	/**
	 * Gets the source property for a given name.
	 * @param name The name of the source property to retrieve.
	 * @return The property or null if it does not exist.
	 */
	public Property getSourceProperty (String name);

	/**
	 * Sets a source property. If a property already exists it is overwritten. 
	 * @param property The new property to set.
	 * @return The property that has been set.
	 */
	public Property setSourceProperty (Property property);
	
	/**
	 * Gets the names of all the source properties for this resource.
	 * @return All the names of the source properties for this resource.
	 */
	public Set<String> getSourcePropertyNames ();

	/**
	 * Gets the target property for a given name and target language.
	 * @param language The language of the property to retrieve.
	 * @param name The name of the property to retrieve.
	 * @return The property or null if it does not exist.
	 */
	public Property getTargetProperty (String language,
		String name);
	
	/**
	 * Sets a target property. If a property already exists it is overwritten.
	 * @param language The target language for which this property should be set.
	 * @param property The new property to set.
	 * @return The property that has been set.
	 */
	public Property setTargetProperty (String language,
		Property property);
	
	/**
	 * Gets the names of all the properties for a given target language in this resource.
	 * @param language The target language to query.
	 * @return All the names of the target properties for the given language in this resource.
	 */
	public Set<String> getTargetPropertyNames (String language);

	/**
	 * Gets the names of all the target languages for this resource.
	 * @return All the names of the target languages for this resource.
	 */
	public Set<String> getTargetLanguages ();
	
	/**
	 * Indicates if a property exists for a given name and target language.
	 * @param language The target language to query.
	 * @param name The name of the property to query.
	 * @return True if a property exists, false otherwise.
	 */
	public boolean hasTargetProperty (String language,
			String name);

	/**
	 * Creates or get a target property based on the corresponding source.
	 * @param language The target language to use.
	 * @param name The name of the property to create (or retrieve)
	 * @param overwriteExisting True to overwrite any existing property.
	 * False to not create a new property if one exists already. 
	 * @param creationOptions Creation options:
	 * <ul><li>CREATE_EMPTY: Creates an empty property, only the read-only flag 
	 * of the source is copied.</li>
	 * <li>COPY_CONTENT: Creates a new property with all its data copied from 
	 * the source.</li></ul>
	 * @return The property that was created, or retrieved. 
	 */
	public Property createTargetProperty (String language,
		String name,
		boolean overwriteExisting,
		int creationOptions);

	public boolean isTranslatable ();
	
	public void setIsTranslatable (boolean value);

	public boolean preserveWhitespaces ();
	
	public void setPreserveWhitespaces (boolean value);
	
}
