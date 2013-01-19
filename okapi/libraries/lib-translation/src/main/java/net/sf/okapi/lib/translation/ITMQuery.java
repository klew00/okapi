/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.translation;

import net.sf.okapi.common.query.IQuery;

/**
 * Provides the methods common to all query engines of translation resources
 * that are translation memory systems. 
 */
public interface ITMQuery extends IQuery {

	/**
	 * Sets the maximum number of hits to retrieve.
	 * @param max The maximum number of hits to retrieve.
	 */
	public void setMaximumHits (int max);
	
	/**
	 * Gets the current maximum number of hits to retrieve.
	 * @return the current maximum number of hits to retrieve.
	 */
	public int getMaximumHits ();
	
	/**
	 * Sets the threshold value to use for the query.
	 * @param threshold The threshold value (between 0 and 100).
	 */
	public void setThreshold (int threshold);
	
	/**
	 * Gets the current threshold value to use for the query.
	 * @return The current threshold value to use for the query.
	 */
	public int getThreshold ();

}
