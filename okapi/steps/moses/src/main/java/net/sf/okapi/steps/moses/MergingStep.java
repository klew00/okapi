/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.moses;

import java.io.File;
import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.filters.mosestext.MosesTextFilter;

public class MergingStep extends BasePipelineStep {

	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private URI inputURI;
	private RawDocument mosesDoc;
	private MosesTextFilter filter;
	private IFilterWriter writer;
	
	public MergingStep () {
	}
	
	@Override
	public String getDescription () {
		return "Merges an original source document with its Moses text data."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "Merging Moses InlineText";
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.INPUT_URI)
	public void setInputURI (URI inputURI) {
		this.inputURI = inputURI;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SECOND_INPUT_RAWDOC)
	public void setSecondInput(final RawDocument secondInput) {
		mosesDoc = secondInput;
	}

	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument();
			return event;
			
		case END_DOCUMENT:
			processEndDocument();
			return event;
			
		default:
			return event;
		}
	}
	
	private void processStartDocument () {
		// Open the corresponding Moses file
		// First try to get it from the secondary input
		RawDocument rd = mosesDoc;
		if ( rd == null ) {
			// If not available: guess the path from the input document
			// = same path plus a .txt extension
			String path = inputURI.getPath() + ".txt";
			rd = new RawDocument(new File(path).toURI(), "UTF-8", sourceLocale);
		}
		
		// Open the Moses file
		filter = new MosesTextFilter();
		filter.open(rd);
		
		// Prepare the writer
		writer = filter.createFilterWriter();
		//writer.setOptions(targetLocale, TODO);
		//writer.setOutput(path);
	}

	private void processEndDocument () {
		// Close the Moses filter
		if ( filter != null ) {
			filter.close();
			filter = null;
		}
	}

}
