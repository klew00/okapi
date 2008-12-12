/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.common.IParameters;

/**
 * Provides a common way of executing an arbitrary utility.
 */
public interface IUtility {

	/**
	 * Sets the filter access information to use with the utility.
	 * This allows utilities to re-use loaded filter access objects to instantiate
	 * filters.
	 * @param filterAccess The filter access object to use.
	 * @param paramsFolder The parameters folder to use.
	 */
	public void setFilterAccess (FilterAccess filterAccess,
		String paramsFolder);
	
	/**
	 * Sets the context UI to use with the utility.
	 * This allows utilities to create UI elements during the process that are
	 * attached to the main UI shell from where the utilities are called.
	 * @param contextUI The shell object to use.
	 */
	public void setContextUI (Object contextUI);

	/**
	 * Adds a cancel listener to this utility.
	 * @param listener The listener to add.
	 */
	public void addCancelListener (CancelListener listener);
	
	/**
	 * Gets the unique string that identifies the utility.
	 * @return
	 */
	public String getName ();

	/**
	 * Resets the input and output lists. This is to call when a utility 
	 * uses more than one input list, before {@link #addInputData(String, String, String)}
	 * and {@link #addOutputData(String, String)}. 
	 */
	public void resetLists ();
	
	/**
	 * Sets the runtime options for this utility.
	 * This method should be called once, before processing each input.
	 * @param sourceLanguage Language code for the source.
	 * @param targetLanguage Language code for the target.
	 */
	public void setOptions (String sourceLanguage,
		String targetLanguage);

	/**
	 * Indicates if the utility has parameters.
	 * @return True if the utility has parameters, false otherwise.
	 */
	public boolean hasParameters ();
	
	/**
	 * Gets the parameters object for the utility.
	 */
	public IParameters getParameters ();
	
	/**
	 * Sets the parameters object for the utility.
	 * @param paramsObject The new parameters object.
	 */
	public void setParameters (IParameters paramsObject);
	
	/**
	 * Indicates if the utility need the root to be defined.
	 * @return True if the root is needed, false otherwise.
	 */
	public boolean needsRoots ();
	
	/**
	 * Sets the input and output roots for the utility.
	 * @param inputRoot The input root for the utility.
	 * @param outputRoot The output root for the utility. 
	 */
	public void setRoots (String inputRoot,
		String outputRoot);

	/**
	 * Indicates if the utility is filter driven or not. If it is not, the utility 
	 * is processed using the {@link #processInput()} method.
	 * @return True if the utility is filter-driven, false otherwise.
	 */
	public boolean isFilterDriven ();
	
	/**
	 * Gets the number of input documents needed for this utility.
	 * @return The number of input documents needed.
	 */
	public int requestInputCount ();

	/**
	 * Adds a set of document information for the the input.
	 * @param path The full path of the input to process.
	 * @param encoding The default encoding.
	 * @param filterSettings The filter settings to use.
	 */
	public void addInputData (String path,
		String encoding,
		String filterSettings);
	
	/**
	 * Adds a set of document information for the the output.
	 * @param path The full path of the output.
	 * @param encoding The encoding.
	 */
	public void addOutputData (String path,
		String encoding);

	/**
	 * Gets the most useful folder for the given utility, or null if there is
	 * no relevant folder. This folder is the one that make more sense for a user
	 * to go after the utility has been executed, for example, the folder where
	 * the output documents have been created.
	 * @return The output folder for the utility, or null.
	 */
	public String getFolderAfterProcess ();
	
	public void preprocess ();
	
	public void postprocess ();
	
	public void cancel ();

	public void pause ();

	public void resume ();

}
