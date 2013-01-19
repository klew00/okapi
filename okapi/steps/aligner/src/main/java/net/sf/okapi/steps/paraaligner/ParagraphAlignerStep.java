/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.paraaligner;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.lib.extra.diff.incava.DiffLists;
import net.sf.okapi.steps.gcaligner.AlignmentScorer;
import net.sf.okapi.steps.sentencealigner.SentenceAlignerStep;

/**
 * Align paragraphs (TextUnits) between a source and target document. Uses inter-paragraph
 * formatting and other heuristics to align paragraphs. TextUnits from this step can be sent the the
 * {@link SentenceAlignerStep} for more fine grained alignment. <b>TextUnits should not be
 * segmented.</b>
 * 
 * @author HARGRAVEJE
 */
@UsingParameters(Parameters.class)
public class ParagraphAlignerStep extends BasePipelineStep {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private Parameters params;
	private IFilter filter = null;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId targetLocale;
	private LocaleId sourceLocale;
	private List<Event> srcEvents;
	private List<Event> trgEvents;
	private RawDocument targetInput = null;
	private EventComparator comparator;
	private ParagraphAligner paragraphAligner;

	public ParagraphAlignerStep() {
		params = new Parameters();
		List<AlignmentScorer<ITextUnit>> scorerList = new LinkedList<AlignmentScorer<ITextUnit>>();
		scorerList.add(new ParagraphGaleAndChurch());
		paragraphAligner = new ParagraphAligner(scorerList);
	}

	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper(IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale(LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale(LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.SECOND_INPUT_RAWDOC)
	public void setSecondInput(RawDocument secondInput) {
		this.targetInput = secondInput;
	}

	@Override
	public String getName() {
		return "Paragraph Aligner";
	}

	@Override
	public String getDescription() {
		return "Align paragraphs (text units) between a source and a target document.";
	}

	@Override
	public IParameters getParameters() {
		return params;
	}

	@Override
	public void setParameters(IParameters params) {
		this.params = (Parameters) params;
	}

	@Override
	protected Event handleStartBatch(Event event) {
		return event;
	}

	@Override
	protected Event handleEndBatch(Event event) {
		return event;
	}

	@Override
	protected Event handleStartDocument(Event event) {
		if (targetInput != null) {
			initializeFilter();
		}
		srcEvents = new LinkedList<Event>();
		trgEvents = new LinkedList<Event>();
		comparator = new EventComparator();
		return event;
	}

	@Override
	protected Event handleEndDocument(Event event) {
		// align skeleton chunks
		DiffLists<Event> skeletonAlignments = skeletonAlign();
		
		// align paragraphs between aligned skeleton (TextUnits)
		paragraphAlign(skeletonAlignments);

		// the diff leverage is over now send the cached events down the
		// pipeline as a MULTI_EVENT
		// add the end document event so its not eaten
		srcEvents.add(event);

		// create a multi event and pass it on to the other steps
		Event multiEvent = new Event(EventType.MULTI_EVENT, new MultiEvent(srcEvents));

		if (filter != null) {
			filter.close();
		}

		srcEvents.clear();
		srcEvents = null;
		trgEvents.clear();
		trgEvents = null;

		return multiEvent;
	}

	@Override
	protected Event handleDocumentPart(final Event event) {
		srcEvents.add(event);
		return Event.NOOP_EVENT;
	}

	@Override
	protected Event handleStartSubDocument(final Event event) {
		srcEvents.add(event);
		return Event.NOOP_EVENT;
	}

	@Override
	protected Event handleEndSubDocument(final Event event) {
		srcEvents.add(event);
		return Event.NOOP_EVENT;
	}

	@Override
	protected Event handleStartGroup(final Event event) {
		srcEvents.add(event);
		return Event.NOOP_EVENT;
	}

	@Override
	protected Event handleEndGroup(final Event event) {
		srcEvents.add(event);
		return Event.NOOP_EVENT;
	}

	@Override
	protected Event handleTextUnit(Event sourceEvent) {
		srcEvents.add(sourceEvent);
		return Event.NOOP_EVENT;
	}

	private void initializeFilter() {
		if (targetInput == null) {
			throw new OkapiBadStepInputException("No target document found.");
		}

		// Initialize the filter to read the translation to compare
		filter = fcMapper.createFilter(targetInput.getFilterConfigId(), null);
		// Open the second input for this batch item
		filter.open(targetInput);
		// populate target Event list
		filterTarget();
	}

	private void filterTarget() {
		Event event = null;
		while (filter.hasNext()) {
			event = filter.next();
			trgEvents.add(event);
		}
	}

	private DiffLists<Event> skeletonAlign() {
		DiffLists<Event> diffEvents;

		diffEvents = new DiffLists<Event>(srcEvents, trgEvents, comparator);

		// diff the two TextUnit lists based on the provided Comparator
		diffEvents.diff();

		return diffEvents;
	}
	
	private void paragraphAlign(DiffLists<Event> skeletonAlignments) {
		//TODO for Jerry
	}
}
