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

	public TmHit() {
		setMatchType(TmMatchType.NONE);
		setCodeMismatch(false);
	}

	public TmHit(TranslationUnit tu, TmMatchType matchType, float score) {
		setTu(tu);
		setMatchType(matchType);
		setScore(score);
		setCodeMismatch(false);
	}

	public float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public TranslationUnit getTu() {
		return tu;
	}

	public void setTu(TranslationUnit tu) {
		this.tu = tu;
	}

	public void setMatchType(TmMatchType matchType) {
		this.matchType = matchType;
	}

	public TmMatchType getMatchType() {
		return matchType;
	}

	public void setCodeMismatch(boolean codeMismatch) {
		this.codeMismatch = codeMismatch;
	}

	public boolean isCodeMismatch() {
		return codeMismatch;
	}

	/**
	 * This method implements a three way sort on (1) TmMatchType (2) score (3)
	 * source string. TmMatchType is the primary key, score secondary and source
	 * string tertiary.
	 */
	public int compareTo(TmHit other) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;

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
