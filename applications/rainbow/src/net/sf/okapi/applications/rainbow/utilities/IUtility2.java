package net.sf.okapi.applications.rainbow.utilities;

import net.sf.okapi.common.IParameters;

public interface IUtility2 {
	/**
	 * Gets the unique name of the utility. This name is an ID not a
	 * translatable name.
	 * @return The unique name of the utility.
	 */
	public String getName ();
	
	/**
	 * Indicates if the utility is filter-driven
	 * @return True if the utility is filter-driven, false otherwise.
	 */
	public boolean isFilterDriven ();
	
	/**
	 * Executes any general action needed to free resources when the object
	 * is not used any more.
	 */
	public void finish ();

	/**
	 * Indicates if the utility has parameters.
	 * @return true if the utility uses parameters, false otherwise.
	 */
	public boolean hasParameters ();
	
	/**
	 * Gets the current parameters for the utility.
	 * @return the current parameters. Null is there are none.
	 */
	public IParameters getParameters ();
	
	/**
	 * Sets the parameters for the utility.
	 * @param params The parameters to use.
	 */
	public void setParameters (IParameters params);

	/**
	 * Gets the number of input documents needed for this utility.
	 * @return The number of input documents needed.
	 */
	int getRequestedInputCount ();

	/**
	 * Adds a set of document information for the the input.
	 * @param path The full path of the input to process.
	 * @param encoding The default encoding.
	 * @param filterSettings The filter settings to use. Can be null.
	 */
	void addInputData (String path,
		String encoding,
		String filterSettings);
	
	/**
	 * Adds a set of document information for the the output.
	 * @param path The full path of the output.
	 * @param encoding The encoding.
	 */
	void addOutputData (String path,
		String encoding);

	/**
	 * Adds a CancelListener to the object listener list.
	 * @param listener The listener to add.
	 */
	void addCancelListener (CancelListener listener);

	/**
	 * Removes a CancelListener from the object listener list.
	 * @param listener The listener to add.
	 */
	void removeCancelListener (CancelListener listener);

}
