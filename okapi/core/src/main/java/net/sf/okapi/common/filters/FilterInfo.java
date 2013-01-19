/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.filters;

/**
 * Storage for filter information.
 */
public class FilterInfo implements Comparable<FilterInfo> {

	/**
	 * Name of the filter.
	 */
	public String name;
	
	/**
	 * display name of the filter.
	 */
	public String displayName;
	
	/**
	 * Name of the class of the filter.
	 */
	public String className;
	
	/**
	 * Returns the display name of this filter.
	 */
	@Override
	public String toString () {
		return displayName;
	}

	/**
	 * Compares the display names of the two filters.
	 * @param obj the other filter info object to compare.
	 * @return the comparison result between the two filters display name, 0 if they are the same.
	 */
	@Override
	public int compareTo(FilterInfo obj) {
		return displayName.compareTo(obj.displayName);
	}

}
