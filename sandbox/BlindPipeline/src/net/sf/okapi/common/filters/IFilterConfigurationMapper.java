package net.sf.okapi.common.filters;

import java.util.List;

public interface IFilterConfigurationMapper {

	/**
	 * Adds a new configuration to this mapper.
	 * @param config the configuration to add.
	 * @param mimeType the MIME type associated with the new configuration.
	 */
	public void addMapping (FilterConfiguration config, String mimeType);
	
	/**
	 * Removes a given configuration from this mapper.
	 * @param configId the identifier of the configuration to remove.
	 */
	public void removeMapping (String configId);
	
	/**
	 * Creates an instance of the filter for a given configuration identifier
	 * and loads its corresponding parameters.
	 * @param configId the configuration identifier to use for look-up.  
	 * @return a new IFilter object (with its parameters loaded) for the given
	 * configuration identifier, or null if the object could not be created.
	 */
	public IFilter createFilter (String configId);
	
	/**
	 * Gets the FilterConfiguration object for a given configuration identifier.
	 * @param configId the configuration identifier to search for.
	 * @return the FilterConfiguration object for the given configuration identifier,
	 * or null if a match could not be found.
	 */
	public FilterConfiguration getConfiguration (String configId);
	
	/**
	 * Gets the first filter configuration for a given MIME type.
	 * @param mimeType MIME type to search for.
	 * @return the filter configuration for the given MIME type.
	 */
	public FilterConfiguration getDefaultConfiguration (String mimeType);
	
	/**
	 * Gets a list of all FilterConfiguration objects for a given MIME type.
	 * @param mimeType mimeType MIME type to search for.
	 * @return a list of all FilterConfiguration objects found for the
	 * given MIME type (it may be empty).
	 */
	public List<FilterConfiguration> getConfigurations (String mimeType);

	/**
	 * Gets the full path for the parameters file of a given custom filter configuration.
	 * This method provides a way for this mapper to implements how it retrieves
	 * custom filter parameters files. for example it could simply define a root
	 * where all custom files are located, or a PATH mechanism, or whatever is 
	 * appropriate for the implementation.  
	 * @param the custom configuration for which the method should return the 
	 * filter parameters file path.
	 * @return the full path for the given custom filter configuration, or null
	 * if the path could not be provided.
	 */
	public String getCustomParametersPath (FilterConfiguration config);
	
}
