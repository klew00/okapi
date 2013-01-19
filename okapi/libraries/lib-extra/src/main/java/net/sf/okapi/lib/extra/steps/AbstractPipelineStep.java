/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.extra.steps;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.lib.extra.OkapiComponent;

/**
 * Abstract implementation of the {@link IPipelineStep} interface. 
 */
abstract public class AbstractPipelineStep extends OkapiComponent implements IPipelineStep {

	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private boolean isLastOutputStep = false;

	public AbstractPipelineStep() {
		super();
	}

	protected LocaleId  getSourceLocale() {
		return srcLoc;
	}
	
	protected LocaleId  getTargetLocale() {
		return trgLoc;
	}
	
	public void cancel() {
		//TODO???
	}
	
	public void destroy() {
		// Do nothing by default
	}

	public String getHelpLocation () {
		// Wiki call: name of the page is the name of teh step + " Step"
		return getName() + " Step";
		//return ".." + File.separator + "help" + File.separator + "steps";
	}
	
	public Event handleEvent(Event event) {
		
		if (event == null) return null;
		
		switch ( event.getEventType() ) {
		
		case START_BATCH:
			component_init();
			event = handleStartBatch(event);
			break;
			
		case END_BATCH:
			component_done();
			event = handleEndBatch(event);
			break;
			
		case START_BATCH_ITEM:
			event = handleStartBatchItem(event);
			break;
			
		case END_BATCH_ITEM:
			event = handleEndBatchItem(event);
			break;
			
		case RAW_DOCUMENT:
			event = handleRawDocument(event);
			break;
			
		case START_DOCUMENT:
			event = handleStartDocument(event);
			break;
			
		case END_DOCUMENT:
			event = handleEndDocument(event);
			break;
			
		case START_SUBDOCUMENT:
			event = handleStartSubDocument(event);
			break;
			
		case END_SUBDOCUMENT:
			event = handleEndSubDocument(event);
			break;
			
		case START_GROUP:
			event = handleStartGroup(event);
			break;
			
		case END_GROUP:
			event = handleEndGroup(event);
			break;
			
		case START_SUBFILTER:
			event = handleStartSubfilter(event);
			break;
			
		case END_SUBFILTER:
			event = handleEndSubfilter(event);
			break;
			
		case TEXT_UNIT:
			event = handleTextUnit(event);
			break;
			
		case DOCUMENT_PART:
			event = handleDocumentPart(event);
			break;
			
		case CUSTOM:
			event = handleCustom(event);
			break;
			
		// default:
		// Just pass it through
		}
		return event;
	}

	public boolean isDone() {
		return true;
	}

	public boolean isLastOutputStep () {
		return isLastOutputStep;
	}

	public void setLastOutputStep (boolean isLastStep) {
		this.isLastOutputStep = isLastStep;
	}

	// By default we simply pass the event on to the next step.
	// Override these methods if you need to process the event

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_BATCH} event.
	 * @param event the event itself. 
	 */
	protected Event handleStartBatch (Event event) {
		return event;
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_BATCH} event.
	 * @param event the event itself. 
	 */
	protected Event handleEndBatch (Event event) {
		return event;
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_BATCH_ITEM} event.
	 * @param event the event itself. 
	 */
	protected Event handleStartBatchItem (Event event) {
		return event;
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_BATCH_ITEM} event.
	 * @param event the event itself. 
	 */
	protected Event handleEndBatchItem (Event event) {
		return event;
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#RAW_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected Event handleRawDocument (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected Event handleStartDocument (Event event) {
		
		StartDocument sd = (StartDocument) event.getResource();
		
		if (sd != null) srcLoc = sd.getLocale();
		return event;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
		this.srcLoc = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.trgLoc = targetLocale;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected Event handleEndDocument (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_SUBDOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected Event handleStartSubDocument (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_SUBDOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected Event handleEndSubDocument (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_GROUP} event.
	 * @param event the event itself. 
	 */
	protected Event handleStartGroup (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_GROUP} event.
	 * @param event the event itself. 
	 */
	protected Event handleEndGroup (Event event) {
		return event;
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_SUBFILTER} event.
	 * @param event the event itself. 
	 */
	protected Event handleStartSubfilter (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_SUBFILTER} event.
	 * @param event the event itself. 
	 */
	protected Event handleEndSubfilter (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#TEXT_UNIT} event.
	 * @param event the event itself. 
	 */
	protected Event handleTextUnit (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#DOCUMENT_PART} event.
	 * @param event the event itself. 
	 */
	protected Event handleDocumentPart (Event event) {
		return event;
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#CUSTOM} event.
	 * @param event the event itself. 
	 */
	protected Event handleCustom (Event event) {
		return event;
	}
}
