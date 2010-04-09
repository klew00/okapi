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

package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.common.HashCodeUtil;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Represents a TM Hit. This stores a reference to the TranslationUnit and its
 * score and {@link TmMatchType}
 * 
 * @author HaslamJD
 * @author HARGRAVEJE
 */
public class TmHit implements Comparable<TmHit> {
	private TranslationUnit tu;
	private float score;
	private TmMatchType matchType;
	private boolean codeMismatch;
	private int docId;

	/**
	 * Default constructor which sets the TmMatchType to NONE. 
	 */
	public TmHit() {
		setMatchType(TmMatchType.NONE);
		setCodeMismatch(false);
	}

	/**
	 * Create a new TmHit.
	 * @param tu
	 * @param matchType
	 * @param score
	 */
	public TmHit(TranslationUnit tu, TmMatchType matchType, float score) {
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
	public TranslationUnit getTu() {
		return tu;
	}

	/**
	 * Set the TmHit's {@link TranslationUnit}
	 * @param tu
	 */
	public void setTu(TranslationUnit tu) {
		this.tu = tu;
	}

	/**
	 * Set the Tmhit's {@link TmMatchType}
	 * @param matchType
	 */
	public void setMatchType(TmMatchType matchType) {
		this.matchType = matchType;
	}

	/**
	 * Get the Tmhit's {@link TmMatchType}
	 * @return a {@link TmMatchType}
	 */
	public TmMatchType getMatchType() {
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

	/**
	 * This method implements a three way sort on (1) TmMatchType (2) score (3)
	 * source string. TmMatchType is the primary key, score secondary and source
	 * string tertiary.
	 * 
	 * @param other - the TmHot we are comparing against.
	 */
	public int compareTo(TmHit other) {
		final int EQUAL = 0;

		if (this == other)
			return EQUAL;

		String thisSource = this.tu.getSource().getContent().toString();
		String otherSource = other.tu.getSource().getContent().toString();

		// compare TmMatchType
		int comparison = this.matchType.compareTo(other.getMatchType());
		if (comparison != EQUAL)
			return comparison;

		// compare score
		comparison = Float.compare(this.score, other.getScore());
		if (comparison != EQUAL)
			return comparison * -1;;  // we want to reverse the normal score sort

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
		if (this == other)
			return true;
		if (!(other instanceof TmHit))
			return false;

		TmHit otherHit = (TmHit) other;
		return (this.matchType == otherHit.getMatchType())
				&& (this.tu.getSource().getContent().toString().equals(otherHit
						.getTu().getSource().getContent().toString()))
				&& (this.tu.getTarget().getContent().toString().equals(otherHit
						.getTu().getTarget().getContent().toString()));
	}

	/**
	 * A class that overrides equals must also override hashCode.
	 */
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, matchType);
		result = HashCodeUtil.hash(result, tu.getSource().getContent()
				.toString());
		result = HashCodeUtil.hash(result, tu.getTarget().getContent()
				.toString());
		return result;
	}
}
