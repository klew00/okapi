package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.steps.wordcount.common.AltAnnotationBasedCountStep;
import net.sf.okapi.steps.wordcount.common.GMX;

public class GMXProtectedWordCountStep extends AltAnnotationBasedCountStep {
	
	public static final String METRIC = GMX.ProtectedWordCount;
	
	@Override
	protected boolean accept(MatchType type) {
		return false; // TODO Implement accept(), probably change the superclass
	}

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

}
