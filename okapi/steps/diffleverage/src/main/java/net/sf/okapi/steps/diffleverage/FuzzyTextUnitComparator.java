/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.diffleverage;

import java.util.Comparator;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.lib.search.lucene.analysis.AlphabeticNgramTokenizer;
import net.sf.okapi.lib.search.lucene.scorer.Util;

/**
 * Fuzzily Compare two source {@link TextUnit}s. Text, isReferent() and (optionally) codes must all be same in order to
 * be a match. The fuzzy compare uses n-grams and does a in memory comparison to get a score. Only scores greater than {
 * {@link #threshold} are considered matches.
 * 
 * @author HARGRAVEJE
 * 
 */
public class FuzzyTextUnitComparator implements Comparator<ITextUnit> {
	private static final int NGRAM_SIZE = 3;

	private boolean codeSensitive;
	private int threshold;
	private final AlphabeticNgramTokenizer tokenizer;

	public FuzzyTextUnitComparator(final boolean codeSensitive, final int threshold,
			final LocaleId localeId) {
		this.codeSensitive = codeSensitive;
		setThreshold(threshold);
		tokenizer = Util.createNgramTokenizer(NGRAM_SIZE, localeId);		
	}

	public void setCodeSensitive(final boolean codeSensitive) {
		this.codeSensitive = codeSensitive;
	}

	public boolean isCodeSensitive() {
		return codeSensitive;
	}

	public void setThreshold(final int threshold) {
		this.threshold = threshold;
	}

	public float getThreshold() {
		return threshold;
	}

	@Override
	public int compare(final ITextUnit oldTextUnit, final ITextUnit newTextUnit) {
		if (oldTextUnit.isReferent() && !newTextUnit.isReferent()) {
			return -1; // old is greater than new
			// (not sure what greater means in this case but we have to return something)
		} else if (!oldTextUnit.isReferent() && newTextUnit.isReferent()) {
			return 1; // new is greater than old
			// (not sure what greater means in this case but we have to return something)
		} else {
			// both are either referents or not
			final int result = oldTextUnit.getSource().compareTo(newTextUnit.getSource(),
					codeSensitive);
			if (result == 0) {
				return result;
			} else {
				// do fuzzy compare
				return fuzzyCompare(oldTextUnit, newTextUnit, result);
			}
		}
	}

	private int fuzzyCompare(final ITextUnit oldSource, final ITextUnit newSource,
			int exactCompareResult) {
		float score = Util.calculateNgramDiceCoefficient(
				oldSource.getSource().getUnSegmentedContentCopy().getText(), 
				newSource.getSource().getUnSegmentedContentCopy().getText(), 
				tokenizer);
		if (score >= threshold) {
			return 0;
		}

		return exactCompareResult;
	}
}
