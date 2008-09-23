/**
 * 
 */
package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IPipelineStep;
import net.sf.okapi.common.pipeline2.IProducer;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

/**
 * @author HargraveJE
 * 
 */
public class ConsumerProducer implements IConsumer, IPipelineStep, IProducer {
	private BlockingQueue<IPipelineEvent> producerQueue;
	private BlockingQueue<IPipelineEvent> consumerQueue;

	public void setConsumerQueue(BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void setProducerQueue(BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public String getName() {
		return "ProducerConsumer";
	}

	public PipelineReturnValue call() throws Exception {
		while (true) {
			try {				
				IPipelineEvent event = consumerQueue.take();
				producerQueue.add(event);
				if (event.getEventType() == PipelineEventType.FINISHED) {
					return PipelineReturnValue.SUCCEDED;
				}				
			} catch (InterruptedException e) {
				return PipelineReturnValue.INTERRUPTED;
			}
		}
	}
}
