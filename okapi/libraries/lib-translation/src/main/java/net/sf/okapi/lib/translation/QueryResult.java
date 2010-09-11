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

import net.sf.okapi.common.HashCodeUtil;
import net.sf.okapi.common.query.MatchType;
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
	 * {@link MatchType} of this result.
	 */
	public MatchType matchType;
	
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
	 * String indicating the origin of the result (e.g. name of a TM).
	 * This value can be null and depends on each type of resource.
	 * It should be set to <code>Util.ORIGIN_MT</code> for results of machine translation. 
	 */
	public String origin;
	
	/**
	 * Indicator telling if the result is coming from a machine translation engine or not.
	 * @return true if the result is coming from a machine translation engine, false otherwise.
	 */
	public boolean fromMT() {
		return (matchType == MatchType.MT);
	}
	
	/**
	 * This method implements a four way sort on (1) weight (2) {@link MatchType} (3) Score (4)
	 * source string match. Weight is the primary key, {@link MatchType} secondary, score tertiary
	 * and source string quaternary.
	 * @param other the QueryResult we are comparing against.
	 * @return the comparison result (0 if both objects are equal).
	 */
	@Override
	public int compareTo (QueryResult other) {
		final int EQUAL = 0;

		if ( this == other ) {
			return EQUAL;
		}

		String thisSource = this.source.toText();
		String otherSource = other.source.toText();
		int comparison;
		
		// compare weight
		comparison = Float.compare(this.weight, other.weight);
		if ( comparison != EQUAL ) {
			return comparison;
		}
		
		// only sort by match type if this or other is some kind of exact match
		if ( isExact(this.matchType) || isExact(other.matchType) ) {					
			// compare MatchType
			comparison = this.matchType.compareTo(other.matchType);
			if ( comparison != EQUAL ) {
				return comparison;
			}
		}
		
		// compare score
		comparison = Float.compare(this.score, other.score);
		if ( comparison != EQUAL ) {
			return comparison * -1; // we want to reverse the normal score sort
		}

		// compare source strings with codes
		comparison = thisSource.compareTo(otherSource);
		if ( comparison != EQUAL ) {
			return comparison;
		}

		// default
		return EQUAL;
	}

	/**
	 * Define equality of state.
	 * @param other the object to compare with.
	 * @return true if the objects are equal, false otherwise.
	 */
	@Override
	public boolean equals (Object other) {
		if ( this == other ) {
			return true;
		}
		if ( !(other instanceof QueryResult) ) {
			return false;
		}

		QueryResult otherHit = (QueryResult) other;
		return  (this.weight == otherHit.weight)
				&& (this.matchType == otherHit.matchType)
				&& (this.source.toText().equals(otherHit.source.toText()))
				&& (this.target.toText().equals(otherHit.target.toText()));
	}

	/**
	 * A class that overrides equals must also override hashCode.
	 * @return the hash code for this object.
	 */
	@Override
	public int hashCode () {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, this.weight);
		result = HashCodeUtil.hash(result, this.matchType);
		result = HashCodeUtil.hash(result, this.source.toText());
		result = HashCodeUtil.hash(result, this.target.toText());
		return result;
	}
	
	private boolean isExact (MatchType type) {
		if ( type.ordinal() <= MatchType.EXACT_REPAIRED.ordinal() ) {
			return true;
		}
		return false;
	}
}
