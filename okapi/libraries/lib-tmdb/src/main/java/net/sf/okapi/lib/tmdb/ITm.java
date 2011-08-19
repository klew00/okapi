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
import java.util.Map;

public interface ITm {

	/**
	 * Gets the UUID of this TM.
	 * @return the UUID of this TM.
	 */
	public String getUUID ();
	
	/**
	 * Gets the database key of this TM.
	 * @return the database key of this TM.
	 */
	public long getKey ();
	
	/**
	 * Gets the name of this TM.
	 * @return the name of this TM.
	 */
	public String getName ();
	
	/**
	 * Gets the description of this TM.
	 * @return the description of this TM.
	 */
	public String getDescription ();

	/**
	 * Sets the list of fields to be returned by {@link #getRecords()}.
	 * @param names list of fields to be returned. 
	 */
	public void setRecordFields (List<String> names);

	public IRecord addRecord (Map<String, String> fields);

	// Not sure about this one
	public long addRecordVar (String ... vars);

	//TODO: we should have page-based getters
	public List<IRecord> getRecords ();
	
	/**
	 * Gets a list of all available fields in this TM.
	 * @return the list of all available fields in this TM.
	 */
	public List<String> getAvailableFields ();
	
}
