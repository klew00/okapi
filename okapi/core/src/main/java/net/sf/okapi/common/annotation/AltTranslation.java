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

package net.sf.okapi.common.annotation;

import java.util.UUID;

import net.sf.okapi.common.HashCodeUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Stores the data representing an alternate translation.
 * <p>
 * This object is used with the {@link AltTranslationsAnnotation} annotation.
 */
public class AltTranslation implements Comparable<AltTranslation> {
	
	LocaleId srcLocId;
	LocaleId trgLocId;
	TextUnit tu;
	AltTranslationType type;
	int score;
	String origin;

	/**
	 * Creates a new AltTranslation object.
	 * 
	 * @param sourceLocId
	 *            the locale of the source.
	 * @param targetLocId
	 *            the locale of the target.
	 * @param originalSource
	 *            the original source content.
	 * @param alternateSource
	 *            the source content corresponding to the alternate translation.
	 * @param alternateTarget
	 *            the content of alternate translation.
	 * @param type
	 *            the type of alternate translation.
	 * @param score
	 *            the score for this alternate translation (must be between 0 and 100).
	 * @param origin
	 *            an optional identifier for the origin of this alternate translation.
	 */
	public AltTranslation(LocaleId sourceLocId, LocaleId targetLocId, TextFragment originalSource,
			TextFragment alternateSource, TextFragment alternateTarget, AltTranslationType type,
			int score, String origin) {
		this.srcLocId = sourceLocId;
		this.trgLocId = targetLocId;
		this.type = type;
		this.score = score;
		this.origin = origin;

		tu = new TextUnit(UUID.randomUUID().toString());
		if (alternateSource != null) {
			tu.setSourceContent(alternateSource);
		}

		// TODO: copy code-content from original source to alternate target if necessary
		// TODO: the magic should go here
		if (alternateTarget != null) {
			tu.setTargetContent(targetLocId, alternateTarget);
		}
	}

	/**
	 * Gets the target content of this entry.
	 * 
	 * @return the target content of this entry.
	 */
	public TextContainer getTarget() {
		return tu.getTarget(trgLocId);
	}

	/**
	 * Sets the target parts of this alternate translation.
	 * 
	 * @param targetLocId
	 *            the target locale.
	 * @param alternateTarget
	 *            the content of the alternate translation.
	 */
	public void setTarget(LocaleId targetLocId, TextFragment alternateTarget) {
		this.trgLocId = targetLocId;
		tu.setTargetContent(targetLocId, alternateTarget);
	}

	/**
	 * Gets the source locale for this entry.
	 * 
	 * @return the source locale for this entry.
	 */
	public LocaleId getSourceLocale() {
		return srcLocId;
	}

	/**
	 * Gets the target locale for this entry.
	 * 
	 * @return the target locale for this entry.
	 */
	public LocaleId getTargetLocale() {
		return trgLocId;
	}

	/**
	 * Gets the source content of this entry (can be empty)
	 * 
	 * @return the source content of this entry (can be empty)
	 */
	public TextContainer getSource() {
		return tu.getSource();
	}

	/**
	 * Gets the score for this entry.
	 * 
	 * @return the score for this entry.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Gets the origin for this entry (can be null).
	 * 
	 * @return the origin for this entry, or null if none is defined.
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * Gets the text unit for this entry.
	 * 
	 * @return the text unit for this entry.
	 */
	public TextUnit getEntry() {
		return tu;
	}

	/**
	 * Gets the type of this alternate translation. The value is on of the {@link AltTranslationType} values.
	 * 
	 * @return the type of this alternate translation.
	 */
	public AltTranslationType getType() {
		return type;
	}

	public void setType(AltTranslationType type) {
		this.type = type;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	/**
	 * This method implements a three way sort on (1) AltTranslationType (2) Score (3)
	 * source string match. AltTranslationType is the primary key, score secondary and source
	 * string tertiary.
	 * 
	 * @param other - the AltTranslation we are comparing against.
	 */
	public int compareTo(AltTranslation other) {
		final int EQUAL = 0;

		if (this == other)
			return EQUAL;

		String thisSource = this.getSource().toString();
		String otherSource = other.getSource().toString();

		// compare TmMatchType
		int comparison = this.getType().compareTo(other.getType());
		if (comparison != EQUAL)
			return comparison;

		// compare score
		comparison = Float.compare(this.score, other.getScore());
		if (comparison != EQUAL)
			return comparison * -1; // we want to reverse the normal score sort

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
		if (!(other instanceof AltTranslation))
			return false;

		AltTranslation otherHit = (AltTranslation) other;
		return (this.getType() == otherHit.getType())
				&& (this.getSource().toString().equals(otherHit.getSource().toString()))
				&& (this.getTarget().toString().equals(otherHit.getTarget().toString()));
	}

	/**
	 * A class that overrides equals must also override hashCode.
	 */
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, getType());
		result = HashCodeUtil.hash(result, getSource().toString());
		result = HashCodeUtil.hash(result, getTarget().toString());
		return result;
	}

}