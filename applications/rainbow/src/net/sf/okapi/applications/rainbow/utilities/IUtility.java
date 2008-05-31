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
import net.sf.okapi.common.pipeline.IOutputPipe;
import net.sf.okapi.common.pipeline.IResourceBuilder;

/**
 * Provides a common way of executing an arbitrary utility.
 */
public interface IUtility extends IResourceBuilder, IOutputPipe {

	/**
	 * Executes any prolog steps needed by this utility.
	 * This method should be called once, before processing each input.
	 * @param sourceLanguage Language code for the source.
	 * @param targetLanguage Language code for the target.
	 */
	void doProlog (String sourceLanguage,
		String targetLanguage);

	/**
	 * Executes any epilog steps needed by this utility.
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
	boolean needsRoot ();
	
	/**
	 * Gets the current input root for the utility.
	 * @return The current input root for the utility.
	 */
	String getRoot ();
	
	/**
	 * Sets the input root for the utility.
	 * @param root The new input root for the utility.
	 */
	void setRoot (String root);

	/**
	 * Indicates if the utility has an output to pipe into
	 * the output filters.
	 * @return True if there is an output to pipe in, false otherwise.
	 */
	boolean needsOutput ();
	
	/**
	 * Indicates if the utility is filter driven or not. If it is not, the utility 
	 * is processed using the {@link #execute()} method.
	 * @return True if the utility is filter-driven, false otherwise.
	 */
	boolean isFilterDriven ();
	
	/**
	 * Executes the utility (for non filter-driven utilities).
	 * @param inputPath Full path of the input file on which to apply the utility.
	 */
	void execute (String inputPath);
}
