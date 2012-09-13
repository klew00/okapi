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

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.observer.IObservable;
import net.sf.okapi.common.observer.IObserver;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.IAlignedSegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.steps.gcaligner.AlignmentScorer;

/**
 * Align sentences between source and target paragraphs (TextUnits) and produce a TMX file with aligned sentences. This
 * {@link IPipelineStep} (via configuration) can also output aligned (multilingual {@link TextUnit}s)
 * 
 * @author HARGRAVEJE
 * 
 */
@UsingParameters(Parameters.class)
public class SentenceAlignerStep extends BasePipelineStep implements IObserver {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private Parameters params;
	private IFilter filter = null;
	private XMLWriter writer;
	private TMXWriter tmx;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId targetLocale;
	private LocaleId sourceLocale;
	private RawDocument targetInput = null;
	private SentenceAligner sentenceAligner;
	private ISegmenter sourceSegmenter;
	private ISegmenter targetSegmenter;

	public SentenceAlignerStep() {
		params = new Parameters();
		List<AlignmentScorer<Segment>> scorerList = new LinkedList<AlignmentScorer<Segment>>();
		scorerList.add(new SimpleGaleAndChurch());
		sentenceAligner = new SentenceAligner(scorerList);
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
		return "Sentence Alignment";
	}

	@Override
	public String getDescription() {
		return "Aligns sentences within text units (paragraphs). Produces sentence alignments as bilingual text units or a TMX file.";
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
					LOGGER.warn(String
							.format("Custom source segmentation rules file '%s' cannot be read.\nUsing the default rules instead.",
									params.getCustomSourceRulesPath()));
				}
			}
			if (loadDefault) {
				InputStream is = SentenceAlignerStep.class.getResourceAsStream("/net/sf/okapi/steps/gcaligner/default.srx");
				srxDocument.loadRules(is);
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
					LOGGER.warn(String
							.format("Custom target segmentation rules file '%s' cannot be read.\nUsing the default rules instead.",
									params.getCustomTargetRulesPath()));
				}
			}
			if (loadDefault) {
				InputStream is = SentenceAlignerStep.class.getResourceAsStream("/net/sf/okapi/steps/gcaligner/default.srx");
				srxDocument.loadRules(is);
			}
			// TODO: decide how we deal with leading/trailing spaces
			// srxDocument.setTrimLeadingWhitespaces(false);
			targetSegmenter = srxDocument.compileLanguageRules(targetLocale, null);
		}

		return event;
	}

	protected Event handleEndBatch(Event event) {
		if (tmx != null) {
			tmx.writeEndDocument();
			tmx.close();
			tmx = null;
		}
		return event;
	}

	@Override
	protected Event handleStartDocument(Event event) {
		if (targetInput != null) {
			initializeFilter();
		}
		
		// Start TMX writer (one for all input documents)
		if (tmx == null && params.isGenerateTMX()) {
			String mimeType = event.getStartDocument().getMimeType();
			tmx = new TMXWriter(params.getTmxOutputPath());
			tmx.writeStartDocument(sourceLocale, targetLocale, getClass().getName(), null,
					"sentence", null, mimeType);
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
		
		ITextUnit alignedTextUnit;
		if ( targetInput == null ) {
			// case where the TextUnit is already bilingual
			alignedTextUnit = sentenceAligner.align(sourceTu, sourceLocale, targetLocale, params.isOutputOneTOneMatchesOnly());
		}
		else {
			// case where we have separate source and target TextUnits
			alignedTextUnit = sentenceAligner.align(sourceTu, targetTu, sourceLocale, targetLocale, params.isOutputOneTOneMatchesOnly());
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
		
		// Send the aligned TU to the TMX file
		if (params.isGenerateTMX()) {
			tmx.writeTUFull(alignedTextUnit);
		}
	
		// pass on the aligned (possibly partially aligned)
		return new Event(EventType.TEXT_UNIT, alignedTextUnit);
	}

	private void initializeFilter() {
		// Initialize the filter to read the translation to compare
		filter = fcMapper.createFilter(targetInput.getFilterConfigId(), null);

		// Open the second input for this batch item
		filter.open(targetInput);

		if (writer != null) {
			writer.close();
		}
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
			if (params.isGenerateTMX() && (tmx != null)) {
				tmx.writeEndDocument();
				tmx.close();
				tmx = null;
			}
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
