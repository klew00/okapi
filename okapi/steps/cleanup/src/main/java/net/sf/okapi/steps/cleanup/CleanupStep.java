/*
 * ===========================================================================
 * Copyright (C) 2013 by the Okapi Framework contributors
 * -----------------------------------------------------------------------------
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
 * ===========================================================================
 */

package net.sf.okapi.steps.cleanup;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.UsingParameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UsingParameters(Parameters.class)
public class CleanupStep extends BasePipelineStep {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private Parameters params;
	private Cleaner cleaner;

	private LocaleId sourceLocale;
	private LocaleId targetLocale;

	public CleanupStep() {

		this.params = new Parameters();
		this.cleaner = new Cleaner();
	}

	@Override
	public String getName() {

		return "Cleanup";
	}

	@Override
	public String getDescription() {

		return "Cleans strings by normalizing quotes, punctuation, etc. ready for futher processing. "
				+ "Expects: filter events. Sends back: filter events.";
	}

	@Override
	public IParameters getParameters() {

		return params;
	}

	@Override
	public void setParameters(IParameters params) {

		this.params = (Parameters) params;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale(LocaleId targetLocale) {

		this.targetLocale = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale(LocaleId sourceLocale) {

		this.sourceLocale = sourceLocale;
	}

	@Override
	protected Event handleTextUnit(Event event) {

		ITextUnit tu = event.getTextUnit();
		
		if (!tu.isEmpty()) {
			ISegments srcSegs = tu.getSourceSegments();
			for (Segment srcSeg : srcSegs) {
				Segment trgSeg = tu.getTargetSegment(targetLocale, srcSeg.getId(), false);

				// Skip non-translatable parts
				if (trgSeg != null) {
					if (params.getNormalizeQuotes()) {
						cleaner.normalizeQuotation(srcSeg.text, trgSeg.text);
					}
					cleaner.normalizePunctuation(srcSeg.text, trgSeg.text);
				}
			}
		}
		
		// return event iff tu has text, else remove tu
		if (cleaner.pruneTextUnit(tu, targetLocale) == true) {
			return Event.NOOP_EVENT;
		} else {
			return event;
		}
	}

}
