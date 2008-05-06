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

package net.sf.okapi.utility;

import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.IParameters;

/**
 * Provides a common way of executing an arbitrary utility.
 */
public interface IUtility {

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
	public boolean needRoot ();
	
	/**
	 * Gets the current root for the utility.
	 * @return The current root for the utility.
	 */
	public String getRoot ();
	
	/**
	 * Sets the root for the utility.
	 * @param root The new root for the utility.
	 */
	public void setRoot (String root);

	public void processStartDocument (IFilter filter,
		String outputPath,
		String outputLanguage,
		String outputEncoding);
	
	public void processEndDocument ();
	
	public void processItem (IFilterItem filterItem);
	
}
