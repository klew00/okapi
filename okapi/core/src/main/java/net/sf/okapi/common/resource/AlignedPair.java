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

package net.sf.okapi.common.resource;

import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.LocaleId;

/**
 * An aligned pair consists of a source and target list of {@link TextPart}s or
 * {@link Segment}s, along with a {@link LocaleId} specifying the locale of the
 * target.
 * <p> 
 * An AlignedPair is read-only.
 * 
 * @author HARGRAVEJE
 * 
 */
public final class AlignedPair {
	private final List<TextPart> sourceParts;
	private final List<TextPart> targetParts;
	private final LocaleId localeId;

	/**
	 * Creates an AlignedPair from source and target {@link TextPart}s
	 * 
	 * @param sourceParts
	 *            List source inter-segment and segment parts
	 * @param targetParts
	 *            List target inter-segment and segment parts
	 * @param localeId
	 *            {@link LocaleId} of the target parts
	 */
	public AlignedPair(final List<TextPart> sourceParts,
			final List<TextPart> targetParts, final LocaleId localeId) {
		this.sourceParts = sourceParts;
		this.targetParts = targetParts;
		this.localeId = localeId;
	}

	/**
	 * Creates an AlignedPair from source and target {@link Segment}s
	 * 
	 * @param sourceSegment
	 *            - the source {@link Segment}
	 * @param targetSegment
	 *            - the target {@link Segment}
	 * @param localeId
	 *            - {@link LocaleId} of the target {@link Segment}
	 */
	public AlignedPair(final Segment sourceSegment,
			final Segment targetSegment, final LocaleId localeId) {
		List<TextPart> sourceParts = new LinkedList<TextPart>();
		if (sourceSegment != null) {
			sourceParts.add(sourceSegment);
		}

		List<TextPart> targetParts = new LinkedList<TextPart>();
		if (targetSegment != null) {
			targetParts.add(targetSegment);
		}

		this.sourceParts = sourceParts;
		this.targetParts = targetParts;
		this.localeId = localeId;
	}

	/**
	 * Gets the source {@link TextPart}s
	 * 
	 * @return list of {@link TextPart}s
	 */
	public List<TextPart> getSourceParts() {
		return sourceParts;
	}

	/**
	 * Gets the target {@link TextPart}s
	 * 
	 * @return list of {@link TextPart}s
	 */
	public List<TextPart> getTargetParts() {
		return targetParts;
	}

	/**
	 * Gets the {@link LocaleId} of the target parts
	 * 
	 * @return a {@link LocaleId}
	 */
	public LocaleId getLocaleId() {
		return localeId;
	}
}
