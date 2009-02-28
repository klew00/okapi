/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.Event;

public abstract class BasePipelineStep implements IPipelineStep {

	public Event handleEvent(Event event) {
		if (event == null) {
			return null;
		}

		switch (event.getEventType()) {

		case START:
			preprocess();
			break;

		case FINISHED:
			postprocess();
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

		case FILE_RESOURCE:
			handleFileResource(event);
			break;

		default:
			// TODO: specific exception
			throw new RuntimeException("Unkown event");
		}

		return event;
	}

	// By default we simply pass the event on to the next step. Override these
	// methods if need to process
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

	protected void handleFileResource(Event event) {
	}
}
