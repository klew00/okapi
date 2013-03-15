/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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
import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;

/**
 * Converts filters events into a {@link RawDocument}. This class implements the
 * {@link net.sf.okapi.common.pipeline.IPipelineStep} interface for a step that 
 * takes filter events and creates an output document using the {@link IFilterWriter}
 * implementation provided by the filter through the START_DOCUMENT even.
 * When the document is completed, a {@link RawDocument} is generated.
 * 
 * @see RawDocumentToFilterEventsStep
 * @see FilterEventsWriterStep
 */
@UsingParameters() // No parameters
public class FilterEventsToRawDocumentStep extends BasePipelineStep {

	private IFilterWriter filterWriter;
	private File outputFile;
	private URI outputURI;
	private LocaleId targetLocale;
	private String outputEncoding;

	/**
	 * Create a new FilterEventsToRawDocumentStep object.
	 */
	public FilterEventsToRawDocumentStep () {
	}
	
	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_URI)
	public void setOutputURI (URI outputURI) {
		this.outputURI = outputURI;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_ENCODING)
	public void setOutputEncoding (String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}
	
	@Override
	public String getName() {
		return "Filter Events to Raw Document";
	}

	@Override
	public String getDescription() {
		return "Combine filter events into a full document and pass it along as a raw document."
			+ " Expects: filter events. Sends back: raw document.";
	}

	/**
	 * Catch all incoming {@link Event}s and write them out to the output document.
	 * This step generates NO_OP events until the input events are exhausted, at
	 * which point a RawDocument event is sent.
	 */
	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			handleStartDocument(event);
			return Event.NOOP_EVENT;

		case END_DOCUMENT:
			return processEndDocument(event);
		
		case START_SUBDOCUMENT:
		case START_GROUP:
		case END_SUBDOCUMENT:
		case END_GROUP:
		case START_SUBFILTER:
		case END_SUBFILTER:
		case DOCUMENT_PART:
		case TEXT_UNIT:
			// handle all the events between START_DOCUMENT and END_DOCUMENT
			filterWriter.handleEvent(event);
			return Event.NOOP_EVENT;
		}
		
		// Else, just return the event
		return event;
	}

	private Event processEndDocument (Event event) {
		// Handle the END_DOCUMENT event and close the writer
		filterWriter.handleEvent(event);
		filterWriter.close();

		// Return the RawDocument Event that is the end result of all previous Events
		// Note that the source locale is now set to the 'target locale' value since it is an output
		// We also set the target to the same value to have a value
		RawDocument input = new RawDocument(outputFile.toURI(), outputEncoding, targetLocale, targetLocale);
		return new Event(EventType.RAW_DOCUMENT, input);
	}
	
	@Override
	protected Event handleStartDocument (Event event) {

		StartDocument startDoc = (StartDocument)event.getResource();
		if ( outputEncoding == null ) outputEncoding = startDoc.getEncoding();
		
		filterWriter = startDoc.getFilterWriter();
		filterWriter.setOptions(targetLocale, outputEncoding);
		
		if ( isLastOutputStep() ) {
			outputFile = new File(outputURI);
			Util.createDirectories(outputFile.getAbsolutePath());
		}
		else {
			try {
				outputFile = File.createTempFile("okp-fe2rd_", ".tmp");
			}
			catch ( Throwable e ) {
				throw new OkapiIOException("Cannot create temporary output.", e);
			}
			outputFile.deleteOnExit();
		}
		
		filterWriter.setOutput(outputFile.getAbsolutePath());
		filterWriter.handleEvent(event);
		return event;
	}
}
