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

package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.BasePipelineStep;
import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

/**
 * 
 */
public class Consumer extends BasePipelineStep implements IConsumer {
	private BlockingQueue<IPipelineEvent> consumerQueue;
	
	public void setConsumerQueue(BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public String getName() {
		return "Consumer";
	}

	public void finish() throws InterruptedException {
	}

	public void initialize() throws InterruptedException {
	}

	public PipelineReturnValue process() throws InterruptedException {		
		IPipelineEvent event = consumerQueue.take();
		System.out.println("EventType: " + event.getEventType().name());
		System.out.println("Order: " + Integer.valueOf(event.getOrder()));
		System.out.println();
		if (event.getEventType() == PipelineEventType.FINISHED) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}
}
