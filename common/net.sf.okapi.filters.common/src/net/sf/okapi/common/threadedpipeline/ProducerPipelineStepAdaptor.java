/**
 * 
 */
package net.sf.okapi.common.threadedpipeline;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.PipelineReturnValue;

public class ProducerPipelineStepAdaptor extends BaseThreadedPipelineStepAdaptor implements IProducer {	
	private BlockingQueue<FilterEvent> producerQueue;
	
	public ProducerPipelineStepAdaptor(IPipelineStep step) {
		super(step);		
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
	
	public FilterEvent handleEvent(FilterEvent event) {	
		FilterEvent e = step.handleEvent(event);
		addToQueue(e);
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
