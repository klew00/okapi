/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.wordcount;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.RuleBasedBreakIterator;
import com.ibm.icu.util.ULocale;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.Metrics;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;

public class SimpleWordCountStep extends BasePipelineStep {
	private RuleBasedBreakIterator srcWordIterator = null;
	private RuleBasedBreakIterator trgWordIterator = null;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private long srcBatchItemWordCount;
	private long srcBatchWordCount;
	private ParametersSimpleWordCountStep params;	
	
	public SimpleWordCountStep() {
		params = new ParametersSimpleWordCountStep();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale(LocaleId sourceLocale) {
		this.srcLoc = sourceLocale;
		srcWordIterator = (RuleBasedBreakIterator) BreakIterator.getWordInstance(ULocale
				.createCanonical(srcLoc.toString()));
		RuleBasedBreakIterator.registerInstance(srcWordIterator, srcLoc.toJavaLocale(),
				BreakIterator.KIND_WORD);
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale(LocaleId targetLocale) {
		this.trgLoc = targetLocale;
		trgWordIterator = (RuleBasedBreakIterator) BreakIterator.getWordInstance(ULocale
				.createCanonical(trgLoc.toString()));
		RuleBasedBreakIterator.registerInstance(trgWordIterator, trgLoc.toJavaLocale(),
				BreakIterator.KIND_WORD);
	}
	
	@Override
	protected Event handleStartBatch(Event event) {
		srcBatchWordCount = 0;
		return super.handleStartBatch(event);
	}
	
	@Override
	protected Event handleStartBatchItem(Event event) {
		srcBatchItemWordCount = 0;
		return super.handleStartBatchItem(event);
	}
	
	@Override
	protected Event handleEndBatchItem(Event event) {
		Ending res = event.getEnding();
		if (res == null) {
			res = new Ending("");
			event.setResource(res);
		}
			
		MetricsAnnotation sma = res.getAnnotation(MetricsAnnotation.class);
		if (sma == null) {
			sma = new MetricsAnnotation();
			res.setAnnotation(sma);
		}
		Metrics m = sma.getMetrics();
		m.setMetric(GMX.TotalWordCount, srcBatchItemWordCount);

		srcBatchWordCount += srcBatchItemWordCount;
		
		return super.handleEndBatchItem(event);
	}
	
	@Override
	protected Event handleEndBatch(Event event) {
		Ending res = event.getEnding();
		if (res == null) {
			res = new Ending("");
			event.setResource(res);
		}
			
		MetricsAnnotation sma = res.getAnnotation(MetricsAnnotation.class);
		if (sma == null) {
			sma = new MetricsAnnotation();
			res.setAnnotation(sma);
		}
		Metrics m = sma.getMetrics();
		m.setMetric(GMX.TotalWordCount, srcBatchWordCount);
		
		return super.handleEndBatch(event);
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		ITextUnit tu = event.getTextUnit();

		if (tu.isEmpty() || !tu.isTranslatable()) {
			return event;
		}

		if (!tu.getSource().isEmpty()) {
			long srcWordCount = countWords(tu.getSource().getUnSegmentedContentCopy().getText(), true);
			srcBatchItemWordCount += srcWordCount;
		}
		return event;
	}
	
	@Override
	public String getName() {
		return "Simple Word Count";
	}

	@Override
	public String getDescription() {
		return "Annotates each text units with a total word count (for both source and all targets)"
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public IParameters getParameters() {		
		return params;
	}

	@Override
	public void setParameters(IParameters params) {	
		this.params = (ParametersSimpleWordCountStep)params;
	}

	private long countWords(String text, boolean source) {
		long totalWordCount = 0;
		int current = 0;
		RuleBasedBreakIterator wordIterator;

		if (Util.isEmpty(text)) {
			return totalWordCount;
		}

		if (source) {
			wordIterator = srcWordIterator;
		} else {
			wordIterator = trgWordIterator;
		}
		wordIterator.setText(text);

		while (true) {
			if (current == BreakIterator.DONE) {
				break;
			}

			current = wordIterator.next();
			// don't count various space and punctuation
			if (wordIterator.getRuleStatus() != RuleBasedBreakIterator.WORD_NONE) {
				totalWordCount++;
			}
		}

		return totalWordCount;
	}
}
