/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.common.pipeline.threaded;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.IPipelineStep;

class ProducerPipelineStepAdaptor extends BaseThreadedPipelineStepAdaptor implements IProducer, IFirstStep  {
	private BlockingQueue<Event> producerQueue;
	private BlockingQueue<Event> inputQueue;

	public ProducerPipelineStepAdaptor(IPipelineStep step) {
		super(step);
	}

	@Override
	public void setProducerQueue(BlockingQueue<Event> producerQueue) {
		this.producerQueue = producerQueue;
	}

	@Override
	public void setInputQueue(BlockingQueue<Event> inputQueue) {
		this.inputQueue = inputQueue;
	}
	
	protected void addToQueue(Event event) {
		if (producerQueue == null) {
			throw new RuntimeException("producer queue is null");
		}
		try {
			producerQueue.put(event);
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e);
		}
	}
	
	protected Event takeFromQueue() {
		if (inputQueue == null) {
			throw new RuntimeException("input queue is null");
		}

		Event event;
		try {
			event = inputQueue.take();
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e);
		}
		return event;
	}

	@Override
	protected void  processBlockingQueue() {
		Event e = takeFromQueue();
		e = getStep().handleEvent(e);
		addToQueue(e);
	}

	@Override
	protected void clearBlockingQueues() {
		producerQueue.clear();		
	}
}
