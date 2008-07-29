/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.applications.rainbow.utilities;

import net.sf.okapi.common.IParameters;

/**
 * Provides a common way of executing an arbitrary utility.
 */
public interface IUtility {

	/**
	 * Gets the unique string that identifies the utility.
	 * @return
	 */
	String getID ();

	/**
	 * Resets the input and output lists. This is to call when a utility 
	 * uses more than one input list, before {@link #addInputData(String, String, String)}
	 * and {@link #addOutputData(String, String)}. 
	 */
	void resetLists ();
	
	/**
	 * Executes any prolog steps needed by this utility.
	 * This method should be called once, before processing each input.
	 * @param sourceLanguage Language code for the source.
	 * @param targetLanguage Language code for the target.
	 */
	void doProlog (String sourceLanguage,
		String targetLanguage);

	/**
	 * Executes any concluding steps needed by this utility.
	 * This method should be called once, after processing all inputs.
	 */
	void doEpilog ();
	
	/**
	 * Indicates if the utility has parameters.
	 * @return True if the utility has parameters, false otherwise.
	 */
	boolean hasParameters ();
	
	/**
	 * Gets the parameters object for the utility.
	 */
	IParameters getParameters ();
	
	/**
	 * Sets the parameters object for the utility.
	 * @param paramsObject The new parameters object.
	 */
	void setParameters (IParameters paramsObject);
	
	/**
	 * Indicates if the utility need the root to be defined.
	 * @return True if the root is needed, false otherwise.
	 */
	boolean needsRoots ();
	
	/**
	 * Gets the current input root for the utility.
	 * @return The current input root for the utility.
	 */
	String getInputRoot ();
	
	/**
	 * Gets the output root for the utility.
	 * @return The current output root for the utility.
	 */
	String getOutputRoot ();
	
	/**
	 * Sets the input and output roots for the utility.
	 * @param inputRoot The input root for the utility.
	 * @param outputRoot The output root for the utility. 
	 */
	void setRoots (String inputRoot,
		String outputRoot);

	/**
	 * Indicates if the utility is filter driven or not. If it is not, the utility 
	 * is processed using the {@link #processInput()} method.
	 * @return True if the utility is filter-driven, false otherwise.
	 */
	boolean isFilterDriven ();
	
	/**
	 * Gets the number of input documents needed for this utility.
	 * @return The number of input documents needed.
	 */
	int getInputCount ();

	/**
	 * Adds an set of document information for the the input.
	 * @param path The full path of the input to process.
	 * @param encoding The default encoding.
	 * @param filterSettings The filter settings to use.
	 */
	void addInputData (String path,
		String encoding,
		String filterSettings);
	
	/**
	 * Adds an set of document information for the the output.
	 * @param path The full path of the output.
	 * @param encoding The encoding.
	 */
	void addOutputData (String path,
		String encoding);

}
