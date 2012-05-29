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

package net.sf.okapi.steps.common;

import java.io.File;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;

/**
 * Verifies if a {@link RawDocument} is extracted and merged back properly.
 * This step performs a first extraction, merges the result without changing
 * the data, then re-exact the file generated by the merge, and compare the event
 * generated in both extraction. There should be no difference.
 * <p>This verification does not verify that the merge file is valid, but it
 * should catch most of the problems caused by invalid merges.
 */
@UsingParameters(ExtractionVerificationStepParameters.class) // No parameters
public class ExtractionVerificationStep extends BasePipelineStep {

	private static final Logger LOGGER = Logger.getLogger(ExtractionVerificationStep.class.getName());
	
	private IFilter filter1, filter2;
	private IFilterWriter writer;
	private IFilterConfigurationMapper fcMapper;
	private String filterConfigId;
	private ExtractionVerificationStepParameters params;
	ExtractionVerificationUtil verificationUtil;
	LocaleId localeId;
	
	/**
	 * Creates a new ExtractionVerificationStep object. This constructor is
	 * needed to be able to instantiate an object from newInstance()
	 */
	public ExtractionVerificationStep() {
		params = new ExtractionVerificationStepParameters();
		verificationUtil = new ExtractionVerificationUtil();
	}
	
	@Override
	public void setParameters(IParameters params) {
		this.params = (ExtractionVerificationStepParameters) params;
	}
	
	@Override
	public ExtractionVerificationStepParameters getParameters() {
		return params;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_ID)
	public void setFilterConfigurationId (String filterConfigId) {
		this.filterConfigId = filterConfigId;
	}
	
	public String getName () {
		return "Extraction Verification";
	}

	public String getDescription () {
		return "Verifies a raw document can be extracted, merged, then extracted again and produces the same set of events during both extractions."
			+ " Expects: raw document. Sends back: unmodified raw document.";
	}

	@Override
	protected Event handleRawDocument (Event event) {

		if ( !params.getStepEnabled() ) {
			LOGGER.info("ExtractionVerificationStep is disabled");
			return event;
		}
		
		verificationUtil.setCompareSkeleton(params.getCompareSkeleton());
		verificationUtil.setTargetLocaleOverriden(false);
		
		Event event1=null;
		Event event2=null;
		int count1 = 0;
		int count2 = 0;
		int errorCount = 0;
		int limit = params.getLimit();
		boolean interrupt = params.getInterrupt();
		boolean reachedMax = false;
		
		
		try {
			if ( Util.isEmpty(filterConfigId) ) {
				return event;
			}
			// Else: Get the filter to use
			filter1 = fcMapper.createFilter(filterConfigId);
			filter2 = fcMapper.createFilter(filterConfigId);
			if (filter1 == null) {
				throw new RuntimeException("Unsupported filter type.");
			}
			
			//=== First extraction
			
			RawDocument initialDoc = event.getRawDocument();
			verificationUtil.setTargetLocale(initialDoc.getTargetLocale());
			
			// Open the document
			filter1.open(initialDoc);
			// Create the filter and write out the document 
			writer = filter1.createFilterWriter();
			// Open the output document
			File outFile = File.createTempFile("okp-vx_", ".tmp");
			outFile.deleteOnExit();
			writer.setOutput(outFile.getAbsolutePath());
			writer.setOptions(initialDoc.getSourceLocale(), initialDoc.getEncoding());

			while ( filter1.hasNext() ) {
				event1 = filter1.next();
				writer.handleEvent(event1);
			}
			writer.close();
			filter1.close();
			
			//=== Second pass: Extract from the merged file and compare
			
			RawDocument tmpDoc = new RawDocument(outFile.toURI(), initialDoc.getEncoding(), initialDoc.getSourceLocale(), initialDoc.getTargetLocale());
			filter1 = fcMapper.createFilter(filterConfigId);
			filter1.open(initialDoc);
			filter2.open(tmpDoc);

			boolean hasNext1 = filter1.hasNext();
			boolean hasNext2 = filter2.hasNext();
			
			while ( hasNext1 || hasNext2 ) {

				if(hasNext1){
					count1++;
					event1 = filter1.next();
				}
				if(hasNext2){
					count2++;
					event2 = filter2.next();
				}
				if(hasNext1 && hasNext2 && !reachedMax){
		
					// Compare events
					if ( !identicalEvent(event1, event2) ) {
						errorCount++;
						LOGGER.warning("different events");
						
						if(errorCount >= limit && limit > 0){
							reachedMax = true;
						}
						
						if(reachedMax && interrupt){
							throw new OkapiBadStepInputException("Reached maximum verification errors");
						}
						
						break;
					}				
				}
				
				hasNext1 = filter1.hasNext();
				hasNext2 = filter2.hasNext();
			}
			
			// Compare total number of events
			if(count1 > count2){
				LOGGER.warning("ExtractionVerification: Additional events found in the first run");
			}else if(count2 > count1){
				LOGGER.warning("ExtractionVerification: Additional events found in the second run");
			}
			
			// Compare total number of events
			if(errorCount > 0){
				LOGGER.warning("ExtractionVerification: "+errorCount+ " or more events fail.");
			}else{
				LOGGER.info("ExtractionVerification: All events pass.");
			}
			
		}
		catch ( Throwable e ) {
			throw new RuntimeException("ExtractionVerification failed.\n" + e.getMessage(), e);
		}
		finally {
			closeFilterAndWriter();
		}

		return event; // Return the original document
	}

	private void closeFilterAndWriter () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
		if ( filter1 != null ) {
			filter1.close();
			filter1 = null;
		}
		if ( filter2 != null ) {
			filter2.close();
			filter2 = null;
		}
	}

	public void destroy () {
		closeFilterAndWriter();
	}

	public void cancel () {
		if ( filter1 != null ) filter1.cancel();
		if ( filter2 != null ) filter2.cancel();
	}

	private boolean identicalEvent (Event event1,
		Event event2)
	{
		if (( event1 == null ) && ( event2 != null )) {
			LOGGER.warning("Event from first run is null");
			return false;
		}
		if (( event1 != null ) && ( event2 == null )) {
			LOGGER.warning("Event from second run is null");
			return false;
		}
		if (( event1 == null ) && ( event2 == null )) {
			return true; // They are the same
		}

		if ( event1.getEventType() != event2.getEventType() ) {
			LOGGER.warning("Event Types are different");
			return false;
		}

		if(event1.getEventType() == EventType.TEXT_UNIT){
			return verificationUtil.compareTextUnits(event1.getTextUnit(), event2.getTextUnit());
		}else if (params.getAllEvents()){
			
			switch ( event1.getEventType() ) {
			case START_DOCUMENT:
				StartDocument sd = event1.getStartDocument();
				verificationUtil.setMultilingual(sd.isMultilingual());
				break;
			case START_SUBDOCUMENT:
				return verificationUtil.compareStartSubDocument((StartSubDocument)event1.getResource(), (StartSubDocument)event2.getResource());
			case START_GROUP:
				return verificationUtil.compareBaseReferenceable(event1.getStartGroup(), event2.getStartGroup());
			case START_SUBFILTER:
				return verificationUtil.compareBaseReferenceable(event1.getStartSubfilter(), event2.getStartSubfilter());
			case END_DOCUMENT:
			case END_SUBDOCUMENT:
			case END_GROUP:
			case END_SUBFILTER:
				return verificationUtil.compareIResources(event1.getEnding(), event2.getEnding());
			case DOCUMENT_PART:
				return verificationUtil.compareBaseReferenceable(event1.getDocumentPart(), event2.getDocumentPart());
			}
		}

		return true;
	}
}
