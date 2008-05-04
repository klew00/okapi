package net.sf.okapi.Library.Base;

public interface IParametersProvider {

	/**
	 * Loads a parameters object from a given location. 
	 * @param location The string that encodes the source location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @return The loaded parameters object or null if an error occurred.
	 * @throws Exception
	 */
	public IParameters load (String location)
		throws Exception;
	
	/**
	 * Gets the default parameters for a given provider.
	 * @param location The string that encodes the source location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @return The defaults parameters object or null if an error occurred.
	 * @throws Exception
	 */
	public IParameters createParameters (String location)
		throws Exception;

	/**
	 * Saves a parameters object to a given location.
	 * @param location The string that encodes the target location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @param paramsObject The parameters object to save.
	 * @throws Exception
	 */
	public void save (String location,
		IParameters paramsObject)
		throws Exception;
	
	/**
	 * Split a given location into its components.
	 * @param location The string that encodes the location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @return An array of string corresponding to each component of the location.
	 * The values depend on each implementation.
	 */
	public String[] splitLocation (String location);
	
	/**
	 * Gets the list of available sets of parameters (for example, the list
	 * of all filter settings). 
	 * @return An array of string, each string being the string you
	 * would pass to load the give set of parameters. 
	 */
	public String[] getParametersList ();
}
