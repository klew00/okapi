package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.common.Event;
import net.sf.okapi.steps.wordcount.categorized.CategoryGroup;
import net.sf.okapi.steps.wordcount.categorized.CategoryHandler;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.TokenCountStep;

public class GMXAlphanumericOnlyTextUnitWordCountStep extends TokenCountStep implements CategoryHandler {

	public static final String METRIC = GMX.AlphanumericOnlyTextUnitWordCount;

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
	public String getMetric() {
		return METRIC;
	}

	@Override
	protected boolean countOnlyTranslatable() {
		return true;
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		// TODO Auto-generated method stub
		return super.handleTextUnit(event);
	}

	@Override
	public CategoryGroup getCategoryGroup() {
		return CategoryGroup.GMX_WORD_COUNTS;
	}

}
