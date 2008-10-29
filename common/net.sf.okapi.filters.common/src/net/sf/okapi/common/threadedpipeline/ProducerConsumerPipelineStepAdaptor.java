/**
 * 
 */
package net.sf.okapi.common.threadedpipeline;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.PipelineReturnValue;

public class ProducerConsumerPipelineStepAdaptor extends BaseThreadedPipelineStepAdaptor implements IProducer,
		IConsumer {
	private BlockingQueue<FilterEvent> producerQueue;
	private BlockingQueue<FilterEvent> consumerQueue;

	public ProducerConsumerPipelineStepAdaptor(IPipelineStep step) {
		super(step);
	}

	public void setConsumerQueue(BlockingQueue<FilterEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void setProducerQueue(BlockingQueue<FilterEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	protected void addToQueue(FilterEvent event) {
		if (producerQueue == null) {
			throw new RuntimeException("This class is a consumer not a producer");
		}
		try {
			producerQueue.put(event);
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e);
		}
	}

	protected FilterEvent takeFromQueue() {
		if (consumerQueue == null) {
			throw new RuntimeException("This class is a producer not a consumer");
		}

		FilterEvent event;
		try {
			event = consumerQueue.take();
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e);
		}
		return event;
	}

	public FilterEvent handleEvent(FilterEvent event) {
		FilterEvent e;
		e = takeFromQueue();
		step.handleEvent(e);
		addToQueue(e);
		return e;
	}

	public String getName() {
		return null;
	}

	@Override
	protected PipelineReturnValue processBlockingQueue() {
		FilterEvent event = handleEvent(null);
		if (event.getEventType() == FilterEventType.FINISHED) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}
}
