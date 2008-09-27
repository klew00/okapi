/**
 * 
 */
package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.BasePipelineStep;
import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IProducer;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

/**
 * @author HargraveJE
 * 
 */
public class ConsumerProducer extends BasePipelineStep implements IConsumer, IProducer {
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

	public void finish() throws InterruptedException {
	}

	public void initialize() throws InterruptedException {

	}

	public PipelineReturnValue process() throws InterruptedException {
		IPipelineEvent event = consumerQueue.take();
		producerQueue.put(event);
		if (event.getEventType() == PipelineEventType.FINISHED) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}
}
