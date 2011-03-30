package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.wordcount.WordCounter;
import net.sf.okapi.steps.wordcount.common.BaseCountStep;
import net.sf.okapi.steps.wordcount.common.GMX;

public class GMXProtectedWordCountStep extends BaseCountStep {
	
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
	protected String getMetric() {
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
	protected long countInTextUnit(TextUnit textUnit) {
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

	private void removeMetric(TextUnit textUnit) {
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

}
