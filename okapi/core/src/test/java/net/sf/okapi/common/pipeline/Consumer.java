/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.pipeline;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.BasePipelineStep;

public class Consumer extends BasePipelineStep {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	public String getName() {
		return "Consumer";
	}

	public String getDescription() {
		return "Description";
	}

	@Override
	protected Event handleEndBatchItem (Event event) {		
		LOGGER.trace(getName() + " end-batch-item");
		return event;
	}

	@Override
	protected Event handleStartBatchItem (Event event) {		
		LOGGER.trace(getName() + " start-batch-item");
		return event;
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		LOGGER.trace("EventType: " + event.getEventType().name());
		return event;
	}
	
	@Override
	protected Event handleRawDocument(Event event) {		
		LOGGER.trace("EventType: " + event.getEventType().name());
		return event;
	}

}
