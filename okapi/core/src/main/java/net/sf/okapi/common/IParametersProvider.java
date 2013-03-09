/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

package net.sf.okapi.common;

/**
 * Common way to get access to the parameters of a given component.
 */
public interface IParametersProvider {

	/**
	 * Loads a parameters object from a given location. 
	 * @param location the string that encodes the source location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @return the loaded parameters object or null if an error occurred.
	 * @throws Exception
	 */
	public IParameters load (String location)
		throws Exception;
	
	/**
	 * Gets the default parameters for a given provider.
	 * @param location the string that encodes the source location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @return the defaults parameters object or null if an error occurred.
	 * @throws Exception
	 */
	public IParameters createParameters (String location)
		throws Exception;

	/**
	 * Saves a parameters object to a given location.
	 * @param location the string that encodes the target location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @param paramsObject the parameters object to save.
	 * @throws Exception
	 */
	public void save (String location,
		IParameters paramsObject)
		throws Exception;
	
	/**
	 * Deletes a parameters object at a given location. 
	 * @param location the string that encodes the target location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @return true if the parameters object was delete, false if it was not.
	 */
	public boolean deleteParameters (String location);
	
	/**
	 * Split a given location into its components.
	 * @param location the string that encodes the location. The value depends
	 * on each implementation. It can be a path, a filter setting string, etc.
	 * @return an array of string corresponding to each component of the location.
	 * The values depend on each implementation.
	 */
	public String[] splitLocation (String location);
	
	/**
	 * Gets the list of available sets of parameters (for example, the list
	 * of all filter settings). 
	 * @return an array of string, each string being the string you
	 * would pass to load the give set of parameters. 
	 */
	public String[] getParametersList ();
}
