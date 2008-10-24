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
package net.sf.okapi.common.threadedpipeline.tests;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.threadedpipeline.BasePipelineStep;
import net.sf.okapi.common.threadedpipeline.IProducer;
import net.sf.okapi.common.threadedpipeline.PipelineReturnValue;

public class Producer extends BasePipelineStep implements IProducer {	
	private int order = -1;

	public String getName() {
		return "Producer";
	}

	public void finish() throws InterruptedException {
		addToQueue(new FilterEvent(FilterEventType.FINISHED, null));
	}

	public void initialize() throws InterruptedException {
		addToQueue(new FilterEvent(FilterEventType.START, null));
	}

	@Override
	public PipelineReturnValue process() throws InterruptedException {
		order++;
		if (order >= 5) {
			return PipelineReturnValue.SUCCEDED;
		}
		
		Thread.sleep(1000);
		addToQueue(new FilterEvent(FilterEventType.TEXT_UNIT, null));
		return PipelineReturnValue.RUNNING;
	}
}
