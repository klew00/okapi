/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiFilterCreationException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.StartDocument;

/**
 * Outputs filters events into a document.
 * This class implements the {@link net.sf.okapi.common.pipeline.IPipelineStep}
 * interface for a step that takes filter events and creates an output document
 * using a provided {@link IFilterWriter} implementation. Each event and its 
 * resource are passed on to the next step.
 * @see RawDocumentToFilterEventsStep
 * @see FilterEventsToRawDocumentStep 
 */
public class FilterEventsWriterStep extends BasePipelineStep {

	private IFilterWriter filterWriter;
	private IFilterWriter customFilterWriter;
	private IFilterConfigurationMapper fcMapper;
	private String filterConfigId;
	private URI outputURI;
	private String targetLanguage;
	private String outputEncoding;
	private String documentsRoot;

	/**
	 * Creates a new FilterEventsWriterStep object.
	 * This constructor is needed to be able to instantiate an object from newInstance()
	 */
	public FilterEventsWriterStep () {
	}
	
	/**
	 * Creates a new FilterEventsWriterStep object.
	 * This constructor is needed to be able to instantiate an object from newInstance()
	 * 
	 * @param filterWriter the writer used to convert Events to a document
	 */
	public FilterEventsWriterStep (IFilterWriter filterWriter) {
		setFilterWriter(filterWriter);
	}

	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_ID)
	public void setFilterConfigurationId (String filterConfigId) {
		this.filterConfigId = filterConfigId;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_URI)
	public void setOutputURI (URI outputURI) {
		this.outputURI = outputURI;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LANGUAGE)
	public void setTargetLanguage (String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_ENCODING)
	public void setOutputEncoding (String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}
	
	/**
	 * Sets the filter writer for this EventsWriterStep object.
	 * @param filterWriter the filter writer to use.
	 */
	public void setFilterWriter (IFilterWriter filterWriter) {
		customFilterWriter = filterWriter;
	}
	
	/**
	 * Sets the root of the documents to process. This is to be used when
	 * creating XLIFF output, as a temporary solution for the 'original' attribute.
	 * The value specified is used to fix-up the start document name.
	 * @param documentsRoot documents root.
	 */
	public void setDocumentRoots (String newDocumentsRoot) {
		// Set and normalize root
		File file = new File(newDocumentsRoot);
		documentsRoot = file.toURI().getPath();
		documentsRoot = documentsRoot.replace('\\', '/');
		// Make sure it ends with a '/'
		if ( !documentsRoot.endsWith("/") ) {
			documentsRoot += "/";
		}
	}

	public String getName() {
		return "Filter Events Writer";
	}

	public String getDescription () {
		return "Write out filter events into a document.";
	}

	public boolean needsOutput (int inputIndex) {
		return (inputIndex == 0);
	}
	
	@Override
	public Event handleEvent(Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			if ( customFilterWriter == null ) {
				// Create a writer from the filter information
				IFilter tmp = fcMapper.createFilter(filterConfigId);
				if ( tmp == null ) {
					throw new OkapiFilterCreationException("Error when creating writer from filter.");
				}
				filterWriter = tmp.createFilterWriter();
				filterWriter.setOptions(targetLanguage, outputEncoding);
				filterWriter.setParameters(tmp.getParameters());
				filterWriter.setOutput(outputURI.getPath());
			}
			else { // If we have a custom writer, use it
				filterWriter = customFilterWriter;
				filterWriter.setOptions(targetLanguage, outputEncoding);
				filterWriter.setOutput(outputURI.getPath());
				normalizeDocumentResourceName(event);
			}
			// Fall thru

		// Filter events:
		case START_SUBDOCUMENT:
		case END_SUBDOCUMENT:
		case START_GROUP:
		case END_GROUP:
		case TEXT_UNIT:
		case DOCUMENT_PART:
			return filterWriter.handleEvent(event);
			
		case END_DOCUMENT:
			filterWriter.handleEvent(event);
			if ( filterWriter != null ) {
				filterWriter.close();
			}
			return event;

		default: // Any other event
			return event;
		}
	}	
	
	@Override
	public void destroy() {
		if ( filterWriter != null ) {
			filterWriter.close();
		}
	}

	private void normalizeDocumentResourceName (Event event) {
		if ( documentsRoot == null ) return; // Nothing to do
		StartDocument sd = (StartDocument)event.getResource();
		String name = sd.getName();
		if ( Util.isEmpty(name) ) return; // Nothing to do
		name = name.replace('\\', '/');
		if ( name.startsWith(documentsRoot) ) {
			name = name.substring(documentsRoot.length());
		}
		sd.setName(name);
	}

}
