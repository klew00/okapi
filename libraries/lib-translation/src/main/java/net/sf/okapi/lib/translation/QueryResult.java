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

import net.sf.okapi.common.resource.TextFragment;

/**
 * Stores one result of a query.
 */
public class QueryResult implements Comparable<QueryResult> {

	/**
	 * Weight for this result.
	 */
	public int weight;
	
	/**
	 * Score of this result (a value between 0 and 100).
	 */
	public int score;
	
	/**
	 * Text of the source for this result.
	 */
	public TextFragment source;
	
	/**
	 * Text of the target for this result.
	 */
	public TextFragment target;
	
	/**
	 * ID of the connector that generated this result.
	 */
	public int connectorId;

	/**
	 * Compares this QueryResult with another one.
	 * The weight is used for the comparison.
	 * @param other The other QueryResult to compare this one with.
	 * @return 0 if both are equal, 1 if this one is greater than the other one,
	 * -1 if this one is lesser than the other one.
	 */
	public int compareTo (QueryResult other) {
		if ( weight > other.weight ) return 1;
		if ( weight < other.weight ) return -1; 
		return 0;
	}

}
