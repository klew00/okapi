/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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
===========================================================================*/

package net.sf.okapi.lib.tmdb;

import java.util.List;

import net.sf.okapi.common.LocaleId;

public interface IRepository {

	/**
	 * Gets the name of this repository.
	 * @return the name of this repository.
	 */
	public String getName ();
	
	/**
	 * Closes and free any resources allocated by this repository.
	 * This method can be called even when the repository is not open.
	 */
	public void close ();

	/**
	 * Gets the list of all TMs in this repository.
	 * @return the list of all TMs in this repository.
	 */
	public List<String> getTmNames ();
	
	/**
	 * Creates a new TM.
	 * @param name the name of the new tM to create.
	 * @param description the description for the new TM.
	 * @param locId the locale of the initial language.
	 * @return the ITm object for the newly created TM, or null if the TM was not created.
	 */
	public ITm createTm (String tmName,
		String description,
		LocaleId locId);
	
	/**
	 * Deletes a given TM from this repository.
	 * If there is no TM with such name in the repository, nothing happens.
	 * @param name the name of the TM to remove.
	 */
	public void deleteTm (String tmName);

	/**
	 * Create a new object that gives access to the TM of the given name.
	 * Each call returns a new object!
	 * @param name the name of the TM to access.
	 * @return the ITm object for the given TM name, or null if the name is not the one
	 * of an existing TM.
	 */
	public ITm openTm (String tmName);

	public long getTotalSegmentCount (String tmName);
}
