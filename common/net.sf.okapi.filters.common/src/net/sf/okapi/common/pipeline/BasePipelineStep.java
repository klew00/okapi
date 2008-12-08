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

import net.sf.okapi.common.filters.FilterEvent;

public abstract class BasePipelineStep implements IPipelineStep {

	public FilterEvent handleEvent(FilterEvent event) {
		switch (event.getEventType()) {

		case START:
			handleStart(event);
			break;

		case FINISHED:
			handleFinished(event);
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

		default:
			break;
		}

		return event;
	}

	// By default we eat all events - override these methods if need to process
	// the event
	
	protected void handleDocumentPart(FilterEvent event) {
	}
	
	protected void handleFinished(FilterEvent event) {
	}

	protected void handleStart(FilterEvent event) {
	}

	protected void handleStartDocument(FilterEvent event) {
	}

	protected void handleEndDocument(FilterEvent event) {
	}

	protected void handleStartSubDocument(FilterEvent event) {
	}

	protected void handleEndSubDocument(FilterEvent event) {
	}

	protected void handleStartGroup(FilterEvent event) {
	}

	protected void handleEndGroup(FilterEvent event) {
	}

	protected void handleTextUnit(FilterEvent event) {
	}
}
