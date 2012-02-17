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

package net.sf.okapi.lib.tmdb.lucene;

import net.sf.okapi.common.HashCodeUtil;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

/**
 * All files in this package are based on the files by @author HaslamJD and @author HARGRAVEJE in the okapi-tm-pensieve project amd in most cases there are only minor changes.
 */

/**
 * @author fliden
 *
 */
public class OTmHit implements Comparable<OTmHit> {
	private OTranslationUnitResult tu;
	private float score;
	private MatchType matchType;
	private boolean codeMismatch;
	private int docId;
	
	private String segKey;

	/**
	 * Default constructor which sets the MatchType to NONE. 
	 */
	public OTmHit() {
		setMatchType(MatchType.UKNOWN);
		setCodeMismatch(false);
	}

	/**
	 * Create a new TmHit.
	 * @param tu
	 * @param matchType
	 * @param score
	 */
	public OTmHit(OTranslationUnitResult tu, MatchType matchType, float score) {
		setTu(tu);
		setMatchType(matchType);
		setScore(score);
		setCodeMismatch(false);
	}

	/**
	 * Get the TmHit's score. 
	 * @return the score as a float normalized between 0 and 1.0
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Set the TmHit's score.
	 * @param score
	 */
	public void setScore(Float score) {
		this.score = score;
	}

	/**
	 * Get the TmHit's {@link TranslationUnit}
	 * @return a {@link TranslationUnit}
	 */
	public OTranslationUnitResult getTu() {
		return tu;
	}

	/**
	 * Set the TmHit's {@link TranslationUnit}
	 * @param tu
	 */
	public void setTu(OTranslationUnitResult tu) {
		this.tu = tu;
	}

	/**
	 * Set the Tmhit's {@link MatchType}
	 * @param matchType
	 */
	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}

	/**
	 * Get the Tmhit's {@link MatchType}
	 * @return a {@link MatchType}
	 */
	public MatchType getMatchType() {
		return matchType;
	}

	/**
	 * Set true of the {@link Code}s between the TmHit and query {@link TextFragment} are different.
	 * @param codeMismatch
	 */
	public void setCodeMismatch(boolean codeMismatch) {
		this.codeMismatch = codeMismatch;
	}

	/**
	 * Is there a difference between the {@link Code}s of the TmHit and the query {@link TextFragment}?  
	 * @return true if there is a code difference.
	 */
	public boolean isCodeMismatch() {
		return codeMismatch;
	}

	/**
	 * Set the document id for the TmHit. This is usually the Lucene document id.  
	 * @param docId
	 */
	public void setDocId(int docId) {
		this.docId = docId;
	}

	/**
	 * Get the document id for the TmHit. This is usually the Lucene document id.
	 * @return integer specifying the TmHit's document id.
	 */
	public int getDocId() {
		return docId;
	}

	public String getSegKey(){
		return this.segKey;
	}
	
	public void setSegKey(String segKey) {
		this.segKey = segKey;
	}
	
	/**
	 * This method implements a three way sort on (1) MatchType (2) score (3)
	 * source string. MatchType is the primary key, score secondary and source
	 * string tertiary.
	 * 
	 * @param other - the TmHit we are comparing against.
	 */
	public int compareTo(OTmHit other) {
		final int EQUAL = 0;

		if (this == other)
			return EQUAL;

		String thisSource = this.tu.getResult().getContent().toText();
		String otherSource = other.tu.getResult().getContent().toText();
		
		// only sort by match type if this or other is some kind of exact match
		int comparison;
		if ( isExact(this.matchType) || isExact(other.matchType) ) {		
			// compare MatchType
			comparison = this.matchType.compareTo(other.getMatchType());
			if (comparison != EQUAL)
				return comparison;
		}

		// compare score
		comparison = Float.compare(this.score, other.getScore());
		if (comparison != EQUAL)
			return comparison * -1;  // we want to reverse the normal score sort

		// compare source strings with codes
		comparison = thisSource.compareTo(otherSource);
		if (comparison != EQUAL)
			return comparison;

		// default
		return EQUAL;
	}

	/**
	 * Define equality of state.
	 */
	@Override
	public boolean equals(Object other) {
		//--NOTE REMOVED TARGET FROM COMPARISON--
		if (this == other)
			return true;
		if (!(other instanceof OTmHit))
			return false;

		OTmHit otherHit = (OTmHit) other;
		return (this.matchType == otherHit.getMatchType())
				&& (this.tu.getResult().getContent().toText().equals(otherHit
						.getTu().getResult().getContent().toText()));
	}

	/**
	 * A class that overrides equals must also override hashCode.
	 */
	@Override
	public int hashCode() {
		//--NOTE REMOVED TARGET FROM HASH--
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, matchType);
		result = HashCodeUtil.hash(result, tu.getResult().getContent()
				.toText());
		return result;
	}
	
	private boolean isExact (MatchType type) {
		if ( type.ordinal() <= MatchType.EXACT_REPAIRED.ordinal() ) {
			return true;
		}
		return false;
	}
}
