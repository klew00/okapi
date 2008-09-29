/**
 * 
 */
package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.BasePipelineStep;
import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

/**
 * @author HargraveJE
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
