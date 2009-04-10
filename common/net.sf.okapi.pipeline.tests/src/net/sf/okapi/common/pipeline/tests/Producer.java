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
package net.sf.okapi.common.pipeline.tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.pipeline.BasePipelineStep;

public class Producer extends BasePipelineStep {	
	
	private static final Logger LOGGER = Logger.getLogger(Producer.class.getName());
	
	private int eventCount = -1;

	public String getName() {
		return "Producer";
	}

	@Override
	public void postprocess() {	
		LOGGER.log(Level.FINEST, getName() + " postprocess");
	}

	@Override
	public void preprocess() {
		LOGGER.log(Level.FINEST, getName() + " preprocess");		
	}

	@Override
	public Event handleEvent(Event event) {			
		event = new Event(EventType.TEXT_UNIT, null);		
		return event;
	}

	public boolean hasNext() {
		eventCount++;
		if (eventCount >= 10) {					
			return false;
		}
		return true;
	}
}
