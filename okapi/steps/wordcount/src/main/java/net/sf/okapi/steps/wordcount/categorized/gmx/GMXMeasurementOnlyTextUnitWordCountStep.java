package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.TokenCountStep;

public class GMXMeasurementOnlyTextUnitWordCountStep extends TokenCountStep {

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
	protected String getMetric() {
		return GMX.MeasurementOnlyTextUnitWordCount;
	}

	@Override
	protected boolean countOnlyTranslatable() {
		return true;
	}

}
