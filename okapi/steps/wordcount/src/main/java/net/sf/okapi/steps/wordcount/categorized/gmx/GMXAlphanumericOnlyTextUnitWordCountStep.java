package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.TokenCountStep;

public class GMXAlphanumericOnlyTextUnitWordCountStep extends TokenCountStep {

	@Override
	protected String[] getTokenNames() {
		return new String[] {"ABBREVIATION", "E-MAIL", "INTERNET", "COMPANY", "EMOTICON", "MARKUP"};
	}

	@Override
	public String getName() {
		return "GMX Alphanumeric Only Word Count";
	}

	@Override
	public String getDescription() {
		return "An accumulation of the word count for text units that have been identified as containing only alphanumeric words."
		+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	protected String getMetric() {
		return GMX.AlphanumericOnlyTextUnitWordCount;
	}

	@Override
	protected boolean countOnlyTranslatable() {
		return true;
	}

}
