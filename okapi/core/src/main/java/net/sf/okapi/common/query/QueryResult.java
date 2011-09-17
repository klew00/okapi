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

package net.sf.okapi.common.query;

import java.security.InvalidParameterException;
import java.util.Date;

import net.sf.okapi.common.HashCodeUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Stores one result of a query.
 */
public class QueryResult implements Comparable<QueryResult> {

	public static int QUALITY_UNDEFINED = Integer.MIN_VALUE;
	public static int COMBINEDSCORE_UNDEFINED = Integer.MIN_VALUE;
	
	/**
	 * Convert a QueryResult to an {@link AltTranslation}
	 * 
	 * @param originalSource
	 *            the original source content.
	 * @param sourceLocId
	 *            the {@link LocaleId} of the source.
	 * @param targetLocId
	 *            the {@link LocaleId} of the target.
	 * @return an {@link AltTranslation} corresponding to this QueryResult
	 */
	public AltTranslation toAltTranslation(TextFragment originalSource,
		LocaleId sourceLocId,
		LocaleId targetLocId)
	{
		return new AltTranslation(sourceLocId, targetLocId, originalSource,
				source, target, matchType, getCombinedScore(), origin);
	}

	/**
	 * Weight for this result.
	 */
	public int weight;

	/**
	 * Score of this result (a value between 0 and 100).
	 */
	private int score;

	/**
	 * {@link MatchType} of this result.
	 */
	public MatchType matchType = MatchType.UKNOWN;

	/**
	 * Text of the source for this result.
	 */
	public TextFragment source;

	/**
	 * Text of the target for this result.
	 */
	public TextFragment target;

	/**
	 * Creation date of TM entry
	 */
	public Date creationDate = new Date(0);

	/**
	 * Unique id of this TM entry. Can be null.
	 */
	public String entryId = null;

	/**
	 * ID of the connector that generated this result.
	 */
	public int connectorId;

	/**
	 * String indicating the origin of the result (e.g. name of a TM). This
	 * value can be null and depends on each type of resource.
	 */
	public String origin;
	
	private int quality = QUALITY_UNDEFINED;
	private int combinedScore = COMBINEDSCORE_UNDEFINED;
	
	/**
	 * Indicator telling if the result is coming from a machine translation
	 * engine or not.
	 * 
	 * @return true if the result is coming from a machine translation engine,
	 *         false otherwise.
	 */
	public boolean fromMT() {
		return (matchType == MatchType.MT);
	}

	/** Gets the quality rating of the translation in this result.
	 * @return A value between 0 and 100, or {@link #QUALITY_UNDEFINED} if no quality rating is set.
	 */
	public int getQuality () {
		return quality;
	}
	
	/**
	 * Sets the quality rating of the translation in this result
	 * Each connector is responsible for adjusting the original quality information (if any) to this scale.
	 * @param quality the new quality value
	 * (a value between 0 and 100, or {@link #QUALITY_UNDEFINED} if the value is not defined.
	 * @throws InvalidParameterException if the parameter value is not valid.
	 */
	public void setQuality (int quality) {
		if ((( quality < 0 ) && quality != QUALITY_UNDEFINED ) || ( quality > 100 )) {
			throw new InvalidParameterException("Invalid quality value.");
		}
		this.quality = quality;
	}

	/**
	 * Gets the combined score for this result.
	 * @return the combined score for this result.
	 * If no combined score is set (default), this returns the "normal" score.
	 */
	public int getCombinedScore () {
		if ( combinedScore == COMBINEDSCORE_UNDEFINED ) {
			return score;
		}
		return combinedScore;
	}
	
	/**
	 * Sets the combined score for this result.
	 * @param combinedScore the new combined score value.
	 * <p>This is a re-calculated score to take into account quality (when available) 
	 * (a value between 0 and 100, or {@link #COMBINEDSCORE_UNDEFINED} if the value is not defined.
	 */
	public void setCombinedScore (int combinedScore) {
		if ((( combinedScore < 0 ) && combinedScore != COMBINEDSCORE_UNDEFINED ) || ( combinedScore > 100 )) {
			throw new InvalidParameterException("Invalid combined score value.");
		}
		this.combinedScore = combinedScore;
	}

	/**
	 * Gets the score for this result.
	 * @return the score for this result.
	 * @see #getCombinedScore()
	 */
	public int getScore () {
		return score;
	}
	
	/**
	 * Sets the score for this result.
	 * @param score the new combined score value
	 * (a value between 0 and 100).
	 */
	public void setScore (int score) {
		if (( score < 0 ) || ( score > 100 )) {
			throw new InvalidParameterException("Invalid score value.");
		}
		this.score = score;
	}

	/**
	 * This method implements a five way sort on (1) weight (2)
	 * {@link MatchType} (3) Score (4) source string match and (5) creation
	 * date. Weight is the primary key, {@link MatchType} secondary, score
	 * tertiary and source string quaternary.
	 * 
	 * @param other
	 *            the QueryResult we are comparing against.
	 * @return the comparison result (0 if both objects are equal).
	 */
	@Override
	public int compareTo(QueryResult other) {
		final int EQUAL = 0;

		if (this == other) {
			return EQUAL;
		}

		String thisSource = this.source.toText();
		String otherSource = other.source.toText();
		int comparison;

		// compare weight
		comparison = Float.compare(this.weight, other.weight);
		if (comparison != EQUAL) {
			return comparison;
		}

		// compare MatchType only if the two matches are not fuzzy
		if (!(isTrueFuzzy(this.matchType) && isTrueFuzzy(other.matchType))) {
			comparison = this.matchType.compareTo(other.matchType);
			if (comparison != EQUAL) {
				return comparison;
			}
		}

		// compare score
		comparison = Float.compare(this.score, other.score);
		if (comparison != EQUAL) {
			return comparison * -1; // we want to reverse the normal score sort
		}

		// compare source strings with codes
		comparison = thisSource.compareTo(otherSource);
		if (comparison != EQUAL) {
			return comparison;
		}

		// compare creation dates
		comparison = this.creationDate.compareTo(other.creationDate);
		if (comparison != EQUAL) {
			return comparison * -1; // we want more recent dates first
		}

		// default
		return EQUAL;
	}

	/**
	 * Define equality of state. <b>Note: this class has a natural ordering that
	 * is inconsistent with equals.</b>
	 * 
	 * @param other
	 *            the object to compare with.
	 * @return true if the objects are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QueryResult)) {
			return false;
		}

		QueryResult otherHit = (QueryResult) other;
		return (this.weight == otherHit.weight)
				&& (this.matchType == otherHit.matchType)
				&& (this.source.toText().equals(otherHit.source.toText()))
				&& (this.target.toText().equals(otherHit.target.toText()));
	}

	/**
	 * A class that overrides equals must also override hashCode.
	 * 
	 * @return the hash code for this object.
	 */
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, this.weight);
		result = HashCodeUtil.hash(result, this.matchType);
		result = HashCodeUtil.hash(result, this.source.toText());
		result = HashCodeUtil.hash(result, this.target.toText());
		return result;
	}

	private boolean isTrueFuzzy(MatchType type) {
		if (type == MatchType.FUZZY_PREVIOUS_VERSION
				|| type == MatchType.FUZZY_UNIQUE_ID || type == MatchType.FUZZY) {
			return true;
		}
		return false;
	}
}
