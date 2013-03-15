/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.virtualdb;

import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;

public interface IVDocument extends IVSet {

	/**
	 * Gets the StartDocument resource associated with this document.
	 * @return the StartDocument resource associated with this document.
	 */
	public StartDocument getStartDocument ();
	
	/**
	 * Gets the Ending resource associated with this document.
	 * @return the Ending resource associated with this document.
	 */
	public Ending getEndDocument ();
	
	/**
	 * Gets the item for a given extraction id.
	 * @param extractionId the extraction id for the item to retrieve.
	 * @return the item for the given extraction id.
	 */
	public IVItem getItem (String extractionId);
	
	/**
	 * Gets the item for a given key.
	 * @param key the key for the item to retrieve.
	 * @return the item retrieved for the given key.
	 */
	public IVItem getItem (long key);
	
	/**
	 * Gets the virtual text unit for a given extraction id.
	 * @param extractionId the extraction id of the text unit to retrieve.
	 * @return the virtual text unit for a given extraction id.
	 */
	public IVTextUnit getTextUnit (String extractionId);
	
}
