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

package net.sf.okapi.steps.sentencealigner;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.observer.IObservable;
import net.sf.okapi.common.observer.IObserver;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.AlignedPair;
import net.sf.okapi.common.resource.IAlignedSegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.lib.segmentation.SRXDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple alignment of sentences one to one between source and target paragraphs (TextUnits).
 * 
 * @author HARGRAVEJE
 * 
 */
@UsingParameters(Parameters.class)
public class SimpleSentenceAlignerStep extends BasePipelineStep implements IObserver {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private SimpleAlignerParameters params;
	private IFilter filter = null;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId targetLocale;
	private LocaleId sourceLocale;
	private RawDocument targetInput = null;
	private ISegmenter sourceSegmenter;
	private ISegmenter targetSegmenter;

	public SimpleSentenceAlignerStep() {
		params = new SimpleAlignerParameters();
	}

	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper(IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale(LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALES)
	public void setTargetLocale(LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.SECOND_INPUT_RAWDOC)
	public void setSecondInput(RawDocument secondInput) {
		this.targetInput = secondInput;
	}

	@Override
	public String getName() {
		return "Simple Sentence Alignment";
	}

	@Override
	public String getDescription() {
		return "Aligns sentences within text units (paragraphs). " +
				"Simply aligns sentences one to one. If the number of sentences are different, " +
				"the source and target sentences are collapsed into a iisngle sentence, then aligned. " +
				"Produces sentence alignments as bilingual text units.";
	}

	@Override
	public IParameters getParameters() {
		return params;
	}

	@Override
	public void setParameters(IParameters params) {
		this.params = (SimpleAlignerParameters)params;
	}

	@Override
	protected Event handleStartBatch(Event event) {
		boolean loadDefault = true;
		SRXDocument srxDocument = new SRXDocument();

		// Prepare source segmentation if needed
		if (params.isSegmentSource()) {
			// Load default or custom rules
			if (params.isUseCustomSourceRules()) {
				try {
					srxDocument.loadRules(params.getCustomSourceRulesPath());
					loadDefault = false;
				} catch (Exception e) {
					LOGGER.warn("Custom source segmentation rules file '{}' cannot be read.\nUsing the default rules instead.",
									params.getCustomSourceRulesPath());
				}
			}
			if (loadDefault) {
				srxDocument.loadRules(SimpleSentenceAlignerStep.class.getResourceAsStream("/net/sf/okapi/steps/gcaligner/default.srx"));
			}
			// TODO: decide how we deal with leading/trailing spaces
			// srxDocument.setTrimLeadingWhitespaces(false);
			sourceSegmenter = srxDocument.compileLanguageRules(sourceLocale, null);
		}

		// Prepare target segmentation if needed
		if (params.isSegmentTarget()) {
			loadDefault = true;
			// Load default or custom rules
			if (params.isUseCustomTargetRules()) {
				try {
					srxDocument.loadRules(params.getCustomTargetRulesPath());
					loadDefault = false;
				} catch (Exception e) {
					LOGGER.warn("Custom target segmentation rules file '{}' cannot be read.\nUsing the default rules instead.",
									params.getCustomTargetRulesPath());
				}
			}
			if (loadDefault) {
				srxDocument.loadRules(SimpleSentenceAlignerStep.class.getResourceAsStream("/net/sf/okapi/steps/gcaligner/default.srx"));
			}
			// TODO: decide how we deal with leading/trailing spaces
			// srxDocument.setTrimLeadingWhitespaces(false);
			targetSegmenter = srxDocument.compileLanguageRules(targetLocale, null);
		}

		return event;
	}


	@Override
	protected Event handleStartDocument(Event event) {
		if (targetInput != null) {
			initializeFilter();
		}		
		return event;
	}

	@Override
	protected Event handleEndDocument(Event event) {
		if (filter != null) {
			filter.close();
		}
		return event;
	}

	@Override
	protected Event handleTextUnit(Event sourceEvent) {
		ITextUnit sourceTu = sourceEvent.getTextUnit();
		ITextUnit targetTu = null;

		// Skip non-translatable and empty
		if ( !sourceTu.isTranslatable() || sourceTu.isEmpty() ) {
			return sourceEvent;
		}

		// Move to the next target TU
		if (targetInput != null) {
			Event targetEvent = synchronize(EventType.TEXT_UNIT, sourceTu);
			targetTu = targetEvent.getTextUnit();
		}

		// collapse whitespace if needed *before* segmentation and alignment
		// FIXME: When we get parallel pipelines we should move this to a step!!!
		if (params.isCollapseWhitespace()) {
			for (TextPart p : sourceTu.getSource().getSegments()) {
				p.text.collapseWhitespace();
			}
			if (targetInput == null) {
				for (TextPart p : sourceTu.getTarget(targetLocale).getSegments()) {
					p.text.collapseWhitespace();
				}
			} else {
				for (TextPart p : targetTu.getSource().getSegments()) {
					p.text.collapseWhitespace();
				}
			}
		}

		// Segment the source if requested
		if ( params.isSegmentSource() ) {
			sourceTu.createSourceSegmentation(sourceSegmenter);
		}

		// Segment the target if requested
		if ( params.isSegmentTarget() ) {
			if ( targetTu == null ) {
				// TextUnit is bilingual
				sourceTu.createTargetSegmentation(targetSegmenter, targetLocale);
			}
			else {
				// Separate target TextUnit
				targetTu.createSourceSegmentation(targetSegmenter);
			}			
		}
		
		ITextUnit alignedTextUnit = sourceTu;
		if ( targetInput == null ) {
			// bilingual case
			if (alignedTextUnit.getSourceSegments().count() != 
					alignedTextUnit.getTargetSegments(targetLocale).count()) {
				// collapse sentences
				alignedTextUnit.getSource().joinAll();
				alignedTextUnit.getTarget(targetLocale).joinAll();
			}
			alignedTextUnit.getAlignedSegments().align(targetLocale);
		}
		else {
			// monolingual case where we have separate source and target TextUnits
			if (alignedTextUnit.getSourceSegments().count() != targetTu.getSourceSegments().count()) {
				// collapse sentences
				alignedTextUnit.getSource().joinAll();
				targetTu.getSource().joinAll();
			}
			List<AlignedPair> alignedPairs = new LinkedList<AlignedPair>();
			Iterator<Segment> targetSegments = targetTu.getSourceSegments().iterator();
			for (Segment sourceSegment : alignedTextUnit.getSourceSegments()) {
				alignedPairs.add(new AlignedPair(sourceSegment, targetSegments.next(), targetLocale));
			}
			alignedTextUnit.getAlignedSegments().align(alignedPairs, targetLocale);
		}
				
		// remove leading and trailing whitespace in the aligned TextUnit 
		// for both source and target		
		TextUnitUtil.trimSegments(alignedTextUnit.getVariantSources().get(targetLocale));
		TextUnitUtil.trimSegments(alignedTextUnit.getTarget(targetLocale));
		
		// align codes between source and target and 
		// copy source code data to corresponding target codes		
		IAlignedSegments segments = alignedTextUnit.getAlignedSegments();
		for (Segment s : segments) {
			Segment t = segments.getCorrespondingTarget(s, targetLocale);
			s.text.alignCodeIds(t.text);
			TextUnitUtil.copySrcCodeDataToMatchingTrgCodes(s.text, t.text, true, false, null, alignedTextUnit);
		}
		
		// pass on the aligned (possibly partially aligned)
		return new Event(EventType.TEXT_UNIT, alignedTextUnit);
	}

	private void initializeFilter() {
		// Initialize the filter to read the translation to compare
		filter = fcMapper.createFilter(targetInput.getFilterConfigId(), null);

		// Open the second input for this batch item
		filter.open(targetInput);
	}

	private Event synchronize(EventType untilType, ITextUnit sourceTu) {
		boolean found = false;
		Event event = null;
		while (!found && filter.hasNext()) {
			event = filter.next();
			if (event.isTextUnit()) {
				ITextUnit stu = event.getTextUnit();
				// Skip non-translatable and empty just like our primary filter
				if ( !stu.isTranslatable() || stu.isEmpty() ) {
					continue;
				}
			}
			found = (event.getEventType() == untilType);
		}
		if (!found) {
			String targetDoc = (targetInput == null) ? "null" : targetInput.getInputURI().toString();
			throw new RuntimeException(
					"Different number of source or target TextUnits. " +
					"The source and target documents are not paragraph aligned at:\n" +
					"Source: " + sourceTu.getName() + " <> " + sourceTu.getSource().toString() +
					"\nTarget Document: " + targetDoc);
		}
		return event;
	}

	@Override
	public void update(IObservable o, Object event) {
	}
}
