/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.steps.wordcount.WordCounter;
import net.sf.okapi.steps.wordcount.categorized.CategoryGroup;
import net.sf.okapi.steps.wordcount.categorized.CategoryHandler;
import net.sf.okapi.steps.wordcount.common.BaseCountStep;
import net.sf.okapi.steps.wordcount.common.GMX;

public class GMXProtectedWordCountStep extends BaseCountStep implements CategoryHandler {
	
	public static final String METRIC = GMX.ProtectedWordCount;
	
	@Override
	public String getName() {
		return "GMX Protected Word Count";
	}

	@Override
	public String getDescription() {
		return "An accumulation of the word count for text that has been marked as 'protected', or otherwise " +
				"not translatable (XLIFF text enclosed in <mrk mtype=\"protected\"> elements)."
		+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getMetric() {
		return METRIC;
	}

	@Override
	protected long count(TextContainer textContainer, LocaleId locale) {
		long count = WordCounter.getCount(getSource());
		if (count == 0) // No metrics found on the container
			count = WordCounter.count(getSource(), locale); // Word Count metrics are based on counting in source
		return count;
	}

	@Override
	protected long count(Segment segment, LocaleId locale) {
		long count = WordCounter.getCount(segment);
		if (count == 0) // No metrics found on the container
			count = WordCounter.count(segment, locale); // Word Count metrics are based on counting in source
		return count;
	}

	@Override
	protected long countInTextUnit(ITextUnit textUnit) {
		if (textUnit == null) return 0;
		if (textUnit.isTranslatable()) { // Count only in non-translatable TUs
			removeMetric(textUnit);
			return 0; 
		}
		
		LocaleId srcLocale = getSourceLocale();
		TextContainer source = textUnit.getSource();
		
		// Individual segments metrics
		long segCount = 0;
		long segmentsCount = 0;
		long textContainerCount = 0;
		
		ISegments segs = source.getSegments();
		if (segs != null) {
			for (Segment seg : segs) {
				segCount = count(seg, srcLocale);
				segmentsCount += segCount;
				saveToMetrics(seg, segCount);
			}
		}
		// TC metrics
		textContainerCount = count(source, srcLocale);
		saveToMetrics(source, textContainerCount);
		
		if (textContainerCount > 0) return textContainerCount;  
		if (segmentsCount > 0) return segmentsCount;
		return 0;
	}

	private void removeMetric (ITextUnit textUnit) {
		TextContainer source = textUnit.getSource();
		
		ISegments segs = source.getSegments();
		if (segs != null) {
			for (Segment seg : segs) {
				removeFromMetrics(seg, getMetric());
			}
		}
		removeFromMetrics(source, getMetric());
		removeFromMetrics(textUnit, getMetric());
	}

	@Override
	protected boolean countOnlyTranslatable() {
		return false;
	}

	@Override
	public CategoryGroup getCategoryGroup() {
		return CategoryGroup.GMX_WORD_COUNTS;
	}
}
