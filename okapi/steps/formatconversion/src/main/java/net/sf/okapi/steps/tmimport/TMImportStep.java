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

package net.sf.okapi.steps.tmimport;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.filters.pensieve.PensieveFilterWriter;

@UsingParameters(Parameters.class)
public class TMImportStep extends BasePipelineStep {

	private Parameters params;
	private IFilterWriter writer;
	private LocaleId targetLocale;
	private String rootDir;

	public TMImportStep () {
		params = new Parameters();
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}
	
	@Override
	public String getDescription () {
		return "Import text into a new or existing Pensieve TM."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "TM Import";
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@Override
	public Parameters getParameters () {
		return params;
	}

	@Override
	public Event handleEvent (Event event) {
		switch (event.getEventType()) {
		case START_BATCH:
			// Nothing to do so far
			// We initialize on first START_DOCUMENT
			break;
			
		case END_BATCH:
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
			break;
			
		case START_DOCUMENT:
			if ( writer == null ) {
				writer = new PensieveFilterWriter();
				writer.setOutput(Util.fillRootDirectoryVariable(params.getTmDirectory(), rootDir));
				writer.setOptions(targetLocale, "UTF-8");
				((PensieveFilterWriter)writer).setOverwriteSameSource(params.getOverwriteSameSource());
				writer.handleEvent(event);
			}
			break;
			
		case END_DOCUMENT:
			// Do nothing: all documents go to the same TM
			// TM will be closed on END_BATCH
			break;
			
		case START_SUBDOCUMENT:
		case END_SUBDOCUMENT:
		case START_GROUP:
		case END_GROUP:
		case START_SUBFILTER:
		case END_SUBFILTER:
			writer.handleEvent(event);
			break;

		case TEXT_UNIT:
			writer.handleEvent(event);
			break;
			
		case START_BATCH_ITEM:
		case END_BATCH_ITEM:
		case RAW_DOCUMENT:
		case DOCUMENT_PART:
		case CUSTOM:
			// Do nothing
			break;
		
		}
		return event;
	}

}
