/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.idaligner;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;

/**
 * Align two {@link TextUnit}s based on matching ids. The ids are taken from the name (TextUnit.getName()) of each
 * {@link TextUnit}. Any {@link IFilter} that produces a name for its {@link TextUnit}s will work with this aligner.
 * Expects filtered {@link Event}s as input and returns a new (aligned) bi-lingual {@link TextUnit} {@link Event}. Optionally
 * produce a TMX file in the specified output path.
 * 
 * @author Greg Perkins
 * @author HargraveJE
 * 
 */
@UsingParameters(Parameters.class)
public class IdBasedAlignerStep extends BasePipelineStep {

	private static final Logger LOGGER = Logger.getLogger(IdBasedAlignerStep.class.getName());
	private Parameters params;
	private IFilter filter = null;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private RawDocument targetInput = null;
	private TMXWriter tmx;
	private Map<String, ITextUnit> targetTextUnitMap;
	private boolean useTargetText;
	private boolean hasIssue;

	public IdBasedAlignerStep() {
		params = new Parameters();
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
	public String getDescription() {
		return "Align text units in two id-based files (e.g. Java properties)."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName() {
		return "Id-Based Aligner";
	}

	@Override
	public IParameters getParameters() {
		return params;
	}

	@Override
	public void setParameters(final IParameters params) {
		this.params = (Parameters) params;
	}

	@Override
	protected Event handleStartBatch(final Event event) {		
		// Start TMX writer (one for all input documents)
		if (tmx == null && params.getGenerateTMX()) {
			tmx = new TMXWriter(params.getTmxOutputPath());
			tmx.writeStartDocument(sourceLocale, targetLocale, getClass().getName(), null,
					"paragraph", null, null);
		}

		return event;
	}

	@Override
	protected Event handleEndBatch(final Event event) {
		if ( tmx != null ) {
			tmx.writeEndDocument();
			tmx.close();
			tmx = null;
		}
		return event;
	}

	@Override
	protected Event handleStartDocument(Event event) {
		if (targetInput == null) {
			throw new OkapiBadStepInputException("Second input file (target) not configured.");
		}
		hasIssue = false;
		getTargetTextUnits();
		return event;
	}

	@Override
	protected Event handleEndDocument(Event event) {
		if ( hasIssue ) {
			LOGGER.warning("One or more possible issues found.");
		}
		if (filter != null) {
			filter.close();
		}

		return event;
	}

	@Override
	protected Event handleTextUnit (Event sourceEvent) {
		int score = 100;
		
		if (sourceEvent.getTextUnit().getSource().hasBeenSegmented()) {
			throw new OkapiBadStepInputException("IdBasedAlignerStep only aligns unsegmented text units.");
		}

		ITextUnit sourceTu = sourceEvent.getTextUnit();

		// Skip non-translatable and empty
		if (!sourceTu.isTranslatable() || sourceTu.isEmpty() || 
				!sourceTu.getSource().hasText()) {
			return sourceEvent;
		}
		// Populate the target TU
		ITextUnit alignedTextUnit = sourceTu.clone();		
		
		TextContainer targetTC = alignedTextUnit.createTarget(targetLocale, false, IResource.COPY_PROPERTIES);

		// Get the target content from the reference (if possible)
		ITextUnit refTu = targetTextUnitMap.get(sourceTu.getName());
		TextContainer refTc = null;
		boolean missingReferenceMatch = true;
		if (( refTu != null ) && !refTu.isEmpty() ) {
			if ( useTargetText ) {
				// For bilingual reference file: check also the source of the reference
				TextContainer srcRefTc = refTu.getSource();
				// Use the target only if the source is the same
				if ( srcRefTc.compareTo(alignedTextUnit.getSource(), true) == 0 ) {
					refTc = refTu.getTarget(targetLocale);
				}
				else {
					// We had a match, but the source text is not the same
					missingReferenceMatch = false;
				}
			}
			else { // Monolingual files:
				// Use the source of the reference as the target of the aligned TU
				refTc = refTu.getSource();
			}
		}
		
		if (( refTc != null ) && refTc.hasText()) {			
			// align codes (assume filter as numbered them correctly)										
			alignedTextUnit.getSource().getFirstContent().alignCodeIds(refTc.getFirstContent());		
			
			// adjust codes to match new source
			TextFragment tf = TextUnitUtil.copySrcCodeDataToMatchingTrgCodes(
				sourceTu.getSource().getFirstContent(),
				refTc.getFirstContent(), 
				true, false, null, alignedTextUnit);									
			
			if ( params.isCopyToTarget() ) {				
				targetTC.setContent(tf);
				Property prop = refTc.getProperty(Property.APPROVED);
				if ( prop != null ) targetTC.setProperty(prop.clone());
			}			
			
			if ( params.isStoreAsAltTranslation() ) {
				// make an AltTranslation and attach to the target container
				AltTranslation alt = new AltTranslation(sourceLocale, targetLocale, 
					alignedTextUnit.getSource().getUnSegmentedContentCopy(), 
					null, tf, MatchType.EXACT_UNIQUE_ID, score, getName());
								
				// add the annotation to the target container since we are diffing paragraphs only
				// we may need to create the target if it doesn't exist
				AltTranslationsAnnotation alta = TextUnitUtil.addAltTranslation(targetTC, alt);
				// resort AltTranslation in case we already had some in the list
				alta.sort();
			}
		}
		else { // No match in the reference file
			if ( missingReferenceMatch ) {
				if ( sourceTu.getName() == null ) {
					LOGGER.info(String.format("Entry without original identifier (id='%s').", sourceTu.getId()));
					hasIssue = true;
				}
				else {
					LOGGER.info("No match found for " + sourceTu.getName());
					hasIssue = true;
				}
			}
			else {
				LOGGER.info("Source texts differ for " + sourceTu.getName());
				hasIssue = true;
			}
			if ( params.getReplaceWithSource()) {
				// Use the source text if there is no target
				alignedTextUnit.setTarget(targetLocale, sourceTu.getSource());
			}
		}
		
		// Send the aligned TU to the TMX file or pass it on
		if ( params.getGenerateTMX() ) {
			tmx.writeTUFull(alignedTextUnit);
		}
		else { // Otherwise send each aligned TextUnit downstream
			return new Event(EventType.TEXT_UNIT, alignedTextUnit);
		}

		return sourceEvent;
	}

	private void getTargetTextUnits() {
		try {
			// Create the map
			targetTextUnitMap = new HashMap<String, ITextUnit>();
			
			// Initialize the filter to read the translation to compare
			filter = fcMapper.createFilter(targetInput.getFilterConfigId(), null);
			// Open the second input for this batch item
			filter.open(targetInput);

			while (filter.hasNext()) {
				final Event event = filter.next();
				if ( event.getEventType() == EventType.TEXT_UNIT ) {
					ITextUnit tu = event.getTextUnit();
					
					// check if this is a TU without a target (probably a parent tu)
					if (!tu.getSource().hasText()) {
						continue;
					}

					// check if we have a name value
					if (tu.getName() == null) {
						LOGGER.info(String.format("Missing original identifier in target (id='%s').", tu.getId()));
						hasIssue = true;
						continue;
					}

					// check if we have a duplicate name
					if (targetTextUnitMap.get(tu.getName()) != null) {
						LOGGER.info("Duplicate entry for " + tu.getName());
						hasIssue = true;
						continue;
					}
					
					// safe to continue storing the match
					targetTextUnitMap.put(tu.getName(), tu);
				}
				else if ( event.getEventType() == EventType.START_DOCUMENT ) {
					// Use target text if reference file is bilingual, otherwise use the source
					useTargetText = event.getStartDocument().isMultilingual();
				}
			}
		}
		finally {
			if ( filter != null ) {
				filter.close();
			}
		}
	}
}
