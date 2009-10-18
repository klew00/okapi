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
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.lib.extra.OkapiComponent;

/**
 * Abstract implementation of the {@link IPipelineStep} interface. 
 */
abstract public class AbstractPipelineStep extends OkapiComponent implements IPipelineStep {

	private LocaleId language;
	private boolean isLastOutputStep = false;

	public AbstractPipelineStep() {
		super();
	}

	protected LocaleId  getLanguage() {
		return language;
	}
	
	public void cancel() {
		//TODO???
	}
	
	public void destroy() {
		// Do nothing by default
	}

	public Event handleEvent(Event event) {
		
		if (event == null) return null;
		
		switch ( event.getEventType() ) {
		
		case START_BATCH:
			component_init();
			handleStartBatch(event);
			break;
			
		case END_BATCH:
			component_done();
			handleEndBatch(event);
			break;
			
		case START_BATCH_ITEM:
			handleStartBatchItem(event);
			break;
			
		case END_BATCH_ITEM:
			handleEndBatchItem(event);
			break;
			
		case RAW_DOCUMENT:
			handleRawDocument(event);
			break;
			
		case START_DOCUMENT:
			handleStartDocument(event);
			break;
			
		case END_DOCUMENT:
			handleEndDocument(event);
			break;
			
		case START_SUBDOCUMENT:
			handleStartSubDocument(event);
			break;
			
		case END_SUBDOCUMENT:
			handleEndSubDocument(event);
			break;
			
		case START_GROUP:
			handleStartGroup(event);
			break;
			
		case END_GROUP:
			handleEndGroup(event);
			break;
			
		case TEXT_UNIT:
			handleTextUnit(event);
			break;
			
		case DOCUMENT_PART:
			handleDocumentPart(event);
			break;
			
		case CUSTOM:
			handleCustom(event);
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
	protected void handleStartBatch (Event event) {
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_BATCH} event.
	 * @param event the event itself. 
	 */
	protected void handleEndBatch (Event event) {
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_BATCH_ITEM} event.
	 * @param event the event itself. 
	 */
	protected void handleStartBatchItem (Event event) {
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_BATCH_ITEM} event.
	 * @param event the event itself. 
	 */
	protected void handleEndBatchItem (Event event) {
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#RAW_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleRawDocument (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleStartDocument (Event event) {
		
		StartDocument sd = (StartDocument) event.getResource();
		
		if (sd != null) language = sd.getLanguage();
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleEndDocument (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_SUBDOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleStartSubDocument (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_SUBDOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleEndSubDocument (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_GROUP} event.
	 * @param event the event itself. 
	 */
	protected void handleStartGroup (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_GROUP} event.
	 * @param event the event itself. 
	 */
	protected void handleEndGroup (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#TEXT_UNIT} event.
	 * @param event the event itself. 
	 */
	protected void handleTextUnit (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#DOCUMENT_PART} event.
	 * @param event the event itself. 
	 */
	protected void handleDocumentPart (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#CUSTOM} event.
	 * @param event the event itself. 
	 */
	protected void handleCustom (Event event) {
	}

}
