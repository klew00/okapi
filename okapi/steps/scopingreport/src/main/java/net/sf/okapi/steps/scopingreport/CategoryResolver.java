package net.sf.okapi.steps.scopingreport;

import net.sf.okapi.steps.wordcount.categorized.gmx.GMXAlphanumericOnlyTextUnitWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXExactMatchedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXFuzzyMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXLeveragedMatchedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXMeasurementOnlyTextUnitWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXNumericOnlyTextUnitWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXProtectedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXRepetitionMatchedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ConcordanceWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactDocumentContextMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactLocalContextMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactPreviousVersionMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactRepairedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactStructuralMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactTextOnlyPreviousVersionMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactTextOnlyUniqueIdMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactTextOnlyWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactUniqueIdMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.FuzzyMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.FuzzyPreviousVersionMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.FuzzyRepairedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.FuzzyUniqueIdMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.MTWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.PhraseAssembledWordCountStep;

public class CategoryResolver {

	private static final String[] OkapiCategories = new String[] {
		ExactUniqueIdMatchWordCountStep.METRIC,
		ExactPreviousVersionMatchWordCountStep.METRIC,
		ExactLocalContextMatchWordCountStep.METRIC,
		ExactDocumentContextMatchWordCountStep.METRIC,
		ExactStructuralMatchWordCountStep.METRIC,
		ExactMatchWordCountStep.METRIC,
		ExactTextOnlyUniqueIdMatchWordCountStep.METRIC,
		ExactTextOnlyPreviousVersionMatchWordCountStep.METRIC,		
		ExactTextOnlyWordCountStep.METRIC,
		ExactRepairedWordCountStep.METRIC,
		FuzzyUniqueIdMatchWordCountStep.METRIC,
		FuzzyPreviousVersionMatchWordCountStep.METRIC,		
		FuzzyMatchWordCountStep.METRIC,
		FuzzyRepairedWordCountStep.METRIC,
		PhraseAssembledWordCountStep.METRIC,
		MTWordCountStep.METRIC,
		ConcordanceWordCountStep.METRIC
	};
	
	private static final String[] GMXCategories = new String[] {
		GMXProtectedWordCountStep.METRIC,
		GMXExactMatchedWordCountStep.METRIC,
		GMXLeveragedMatchedWordCountStep.METRIC,
		GMXRepetitionMatchedWordCountStep.METRIC,
		GMXFuzzyMatchWordCountStep.METRIC,
		GMXAlphanumericOnlyTextUnitWordCountStep.METRIC,
		GMXNumericOnlyTextUnitWordCountStep.METRIC,
		GMXMeasurementOnlyTextUnitWordCountStep.METRIC		
	};
	
	
}
