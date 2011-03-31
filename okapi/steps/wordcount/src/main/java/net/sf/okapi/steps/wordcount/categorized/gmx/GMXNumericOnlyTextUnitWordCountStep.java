package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.TokenCountStep;

public class GMXNumericOnlyTextUnitWordCountStep extends TokenCountStep {

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
	protected String getMetric() {
		return METRIC;
	}

	@Override
	protected boolean countOnlyTranslatable() {
		return true;
	}

}
