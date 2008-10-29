package net.sf.okapi.common.threadedpipeline;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.PipelineReturnValue;

public class ConsumerPipelineStepAdaptor extends BaseThreadedPipelineStepAdaptor implements IConsumer {	
	private BlockingQueue<FilterEvent> consumerQueue;

	public ConsumerPipelineStepAdaptor(IPipelineStep step) {
		super(step);		
	}

	public void setConsumerQueue(BlockingQueue<FilterEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
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
		FilterEvent e = takeFromQueue();
		step.handleEvent(e);
		return e;
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
