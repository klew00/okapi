package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.steps.wordcount.categorized.CategoryGroup;
import net.sf.okapi.steps.wordcount.categorized.CategoryHandler;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.TokenCountStep;

public class GMXMeasurementOnlyTextUnitWordCountStep extends TokenCountStep implements CategoryHandler {

	public static final String METRIC = GMX.MeasurementOnlyTextUnitWordCount;
	
	@Override
	protected String[] getTokenNames() {
		return new String[] {"DATE", "TIME", "CURRENCY"};
	}

	@Override
	public String getName() {
		return "GMX Measurement Only Word Count";
	}

	@Override
	public String getDescription() {
		return "An accumulation of the word count from measurement-only text units."
		+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getMetric() {
		return METRIC;
	}

	@Override
	protected boolean countOnlyTranslatable() {
		return true;
	}

	@Override
	public CategoryGroup getCategoryGroup() {
		return CategoryGroup.GMX_WORD_COUNTS;
	}
}
