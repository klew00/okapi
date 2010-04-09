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

package net.sf.okapi.common;

/**
 * Represents a range: a start and end position.
 */
public class Range {

	/**
	 * Starting position of this range.
	 */
	public int start;
	
	/**
	 * Ending position of this range.
	 */
	public int end;

	/**
	 * Creates a new range with given starting and ending values.
	 * @param start The start value of the new range.
	 * @param end The end value of the new range.
	 */
	public Range (int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Gets the string representation of the range (its start and end position between parenthesis).
	 * @return the string representation of the range.
	 */
	public String toString () {
		return String.format("(%d,%d)", start, end);
	}
}
