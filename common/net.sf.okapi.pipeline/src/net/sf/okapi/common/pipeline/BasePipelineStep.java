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

package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;

/**
 * Abstract implementation of the {@link IPipelineStep} interface.
 */
public abstract class BasePipelineStep implements IPipelineStep {

	// Override this if the step has parameters
	public IParameters getParameters () { return null; }

	// Override this if the step has parameters
	public void setParameters (IParameters params) {}

	public boolean hasNext() {
		return false;
	}

	public Event handleEvent(Event event) {
		// short circuit switch for null or noop cases
		if (event == null || event.getEventType() == EventType.NO_OP) {
			return event;
		}

		switch (event.getEventType()) {

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

		case RAW_DOCUMENT:
			handleRawDocument(event);
			break;

		case FINISHED:
			handleFinished(event);
			break;
			
		case CUSTOM:
			handleCustom(event);
			break;

		default:
			// TODO: specific exception
			throw new RuntimeException("Unkown Okapi Event: " + event.toString());
		}

		return event;
	}

	// Override these if there is a need for specialized pre or post processing.
	// These methods are called once for every pipeline execution.
	public void preprocess() {
	}

	public void postprocess() {
	}

	public void cancel() {
	}

	/**
	 * used to cleanup any resources, close files etc.. Only called at the end
	 * of the Pipeline life cycle.
	 */
	public void destroy() {
	}

	// By default we simply pass the event on to the next step. Override these
	// methods if we need to process
	// the event

	protected void handleDocumentPart(Event event) {
	}

	protected void handleStartDocument(Event event) {
	}

	protected void handleEndDocument(Event event) {
	}

	protected void handleStartSubDocument(Event event) {
	}

	protected void handleEndSubDocument(Event event) {
	}

	protected void handleStartGroup(Event event) {
	}

	protected void handleEndGroup(Event event) {
	}

	protected void handleTextUnit(Event event) {
	}

	protected void handleRawDocument(Event event) {
	}

	protected void handleFinished(Event event) {
	}
	
	protected void handleCustom(Event event) {
	}
}
