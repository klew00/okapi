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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.PipelineReturnValue;
import net.sf.okapi.common.resource.RawDocument;

public class ThreadedPipeline implements IPipeline {
	private static final int DEFAULT_BLOCKING_QUEUE_SIZE = 50;

	private ExecutorService executor;
	private int blockingQueueSize;
	private int totalThreads;
	private volatile PipelineReturnValue state;
	private LinkedList<BaseThreadedPipelineStepAdaptor> threadedSteps;
	private LinkedList<IPipelineStep> steps;
//	private ProducerPipelineStepAdaptor firstThreadedStep;
	private BlockingQueue<Event> previousQueue;
	private BlockingQueue<Event> inputQueue;
	private String id;

	public ThreadedPipeline() {
		this(Executors.newCachedThreadPool(), DEFAULT_BLOCKING_QUEUE_SIZE);
	}

	public ThreadedPipeline(ExecutorService executor, int blockingQueueSize) {
		steps = new LinkedList<IPipelineStep>();
		this.executor = executor;
		this.blockingQueueSize = blockingQueueSize;
		initialize();
	}

	private void initialize() {
		totalThreads = 0;
		state = PipelineReturnValue.PAUSED;
		threadedSteps = new LinkedList<BaseThreadedPipelineStepAdaptor>();
		inputQueue = new ArrayBlockingQueue<Event>(blockingQueueSize, true);
	}

	private void prepareThreadedSteps() {
		BlockingQueue<Event> queue = null;
		for (IPipelineStep step : steps) {
			if (threadedSteps.isEmpty()) {
				// first step is a producer wrap it with threaded adaptor
				queue = new ArrayBlockingQueue<Event>(blockingQueueSize, true);
				ProducerPipelineStepAdaptor producerStep = new ProducerPipelineStepAdaptor(
						step);
				producerStep.setProducerQueue(queue);
				producerStep.setInputQueue(inputQueue);
				executor.submit(producerStep);
				threadedSteps.add(producerStep);
//				firstThreadedStep = producerStep;
			} else if (step == steps.getLast()) {
				// last step is a consumer
				if (previousQueue == null) {
					throw new RuntimeException(
							"Previous queue should not be null");
				}
				ConsumerPipelineStepAdaptor consumerStep = new ConsumerPipelineStepAdaptor(
						step);
				consumerStep.setConsumerQueue(previousQueue);
				executor.submit(consumerStep);
				threadedSteps.add(consumerStep);
			} else {
				// this is a middle step it is a Producer and Consumer
				if (previousQueue == null) {
					throw new RuntimeException(
							"Previous queue should not be null");
				}
				ProducerConsumerPipelineStepAdaptor producerConsumerStep = new ProducerConsumerPipelineStepAdaptor(
						step);
				queue = new ArrayBlockingQueue<Event>(blockingQueueSize, true);
				producerConsumerStep.setProducerQueue(queue);
				producerConsumerStep.setConsumerQueue(previousQueue);
				executor.submit(producerConsumerStep);
				threadedSteps.add(producerConsumerStep);
			}
			totalThreads += 1;
			previousQueue = queue;
		}
	}

	public void addStep(IPipelineStep step) {
		steps.add(step);
	}

	public void cancel() {
		for (BaseThreadedPipelineStepAdaptor  step : threadedSteps) {
			step.cancel();
		}
		
		inputQueue.clear();
		state = PipelineReturnValue.CANCELLED;
	}

	public PipelineReturnValue getState() {
		return state;
	}

	public void destroy() {
		for (BaseThreadedPipelineStepAdaptor  step : threadedSteps) {
			step.destroy();
		}
		
		inputQueue.clear();
		inputQueue = null;
		clearSteps();
		executor.shutdownNow();
		state = PipelineReturnValue.DESTROYED;
	}

	@Override
	public void process(RawDocument input) {
		process(new Event(EventType.RAW_DOCUMENT, input));
	}

	@Override
	public void process(Event event) {
		state = PipelineReturnValue.RUNNING;
		try {
			inputQueue.put(event);
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e);
		}
	}

	@Override
	public List<IPipelineStep> getSteps() {
		return steps;
	}

	@Override
	public void startBatch() {
		prepareThreadedSteps();
		try {
			inputQueue.put(new Event(EventType.START_BATCH));
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e);
		}
	}

	@Override
	public void endBatch() {
		try {
			inputQueue.put(new Event(EventType.END_BATCH));
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e);
		}
	}

	@Override
	public void clearSteps() {		
		steps.clear();
		threadedSteps.clear();
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}
}
