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

import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.filters.mosestext.MosesTextFilterWriter;

public class ExtractionStep extends BasePipelineStep {

	private LocaleId sourceLocale;
	private URI inputURI;
	private MosesTextFilterWriter writer;
	
	public ExtractionStep () {
	}
	
	@Override
	public String getDescription () {
		return "Creates a Moses InlineText file from the input document."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "Moses InlineText Extraction";
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.INPUT_URI)
	public void setInputURI (URI inputURI) {
		this.inputURI = inputURI;
	}

	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			writer = new MosesTextFilterWriter();
			writer.setOptions(sourceLocale, "UTF-8");
			writer.setOutput(inputURI.getPath() + ".txt");
			return writer.handleEvent(event);
			
		case END_DOCUMENT:
			if ( writer != null ) {
				event = writer.handleEvent(event);
				writer.close();
			}
			return event;
			
		default:
			// The writer creates and closes the files
			if ( writer != null ) {
				return writer.handleEvent(event);
			}
			return event;
		}
	}
	
}
