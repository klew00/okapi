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

package net.sf.okapi.common.threadedpipeline;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;

public abstract class BasePipelineStep implements IPipelineStep {
	private PipelineReturnValue result;
	private volatile boolean pause;
	private BlockingQueue<FilterEvent> producerQueue;
	private BlockingQueue<FilterEvent> consumerQueue;

	protected void addToQueue(FilterEvent event) throws InterruptedException {
		if (producerQueue == null) {
			throw new RuntimeException("This class is a consumer not a producer");
		}
		producerQueue.put(event);
	}

	protected FilterEvent takeFromQueue() throws InterruptedException {
		if (consumerQueue == null) {
			throw new RuntimeException("This class is a producer not a consumer");
		}
		return consumerQueue.take();
	}

	public void setConsumerQueue(BlockingQueue<FilterEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void setProducerQueue(BlockingQueue<FilterEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public void pause() {
		pause = true;
	}

	public synchronized void resume() {
		pause = false;
		notify();
	}

	public PipelineReturnValue process() throws InterruptedException {
		if (this instanceof IConsumer && this instanceof IProducer) {
			FilterEvent event = takeFromQueue();			
			handleEvent(event);
			addToQueue(event);
			if (event.getEventType() == FilterEventType.FINISHED) {
				return PipelineReturnValue.SUCCEDED;
			}
		} else if (this instanceof IConsumer) {
			FilterEvent event = takeFromQueue();			
			handleEvent(event);
			if (event.getEventType() == FilterEventType.FINISHED) {
				return PipelineReturnValue.SUCCEDED;
			}
		} else if (this instanceof IProducer) { 
			throw new RuntimeException("All intial pipeline steps (i.e., producers only) such as filters must override this method");
		}
		return PipelineReturnValue.RUNNING;
	}

	public PipelineReturnValue call() throws Exception {
		result = PipelineReturnValue.RUNNING;
		try {
			initialize();
			while (result == PipelineReturnValue.RUNNING) {

				result = process();

				if (pause) {
					synchronized (this) {
						while (pause)
							wait();
					}
				}

				// Interrupted exception only thrown in waiting mode
				if (Thread.currentThread().isInterrupted()) {
					return PipelineReturnValue.INTERRUPTED;
				}
			}
		} catch (InterruptedException e) {
			return PipelineReturnValue.INTERRUPTED;
		} finally {
			finish();
		}

		return result;
	}

	public final void handleEvent(FilterEvent event) {
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

		case TEXT_GROUP:
			handleTextGroup(event);
			break;

		case SKELETON_UNIT:
			handleSkeletonUnit(event);
			break;

		default:
			break;
		}
	}

	// By default we eat all events - override if you want special processing
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

	protected void handleTextGroup(FilterEvent event) {
	}

	protected void handleSkeletonUnit(FilterEvent event) {
	}
}
