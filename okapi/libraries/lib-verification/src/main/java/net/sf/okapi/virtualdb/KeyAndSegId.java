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

/**
 * Stores an item key and an optional segment id.
 * If the segment id is null, the object refers to the whole item.
 */
public class KeyAndSegId {
	
	/**
	 * Creates a new KeyAndSegId object with a given key and segment id.
	 * @param key the key value.
	 * @param segId the segment id (can be null).
	 */
	public KeyAndSegId (long key,
		String segId)
	{
		this.key = key;
		this.segId = segId;
	}
	
	public long key;
	public String segId;

}
