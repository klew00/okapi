package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.steps.wordcount.categorized.CategoryGroup;
import net.sf.okapi.steps.wordcount.categorized.CategoryHandler;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.TokenCountStep;

public class GMXNumericOnlyTextUnitWordCountStep extends TokenCountStep implements CategoryHandler {

	public static final String METRIC = GMX.NumericOnlyTextUnitWordCount;
		
	@Override
	protected String[] getTokenNames() {
		return new String[] {"NUMBER"};
	}

	@Override
	public String getName() {
		return "GMX Numeric Only Word Count";
	}

	@Override
	public String getDescription() {
		return "An accumulation of the word count for text units that have been identified as containing only numeric words."
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
