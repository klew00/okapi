/**
 * 
 */
package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IPipelineStep;
import net.sf.okapi.common.pipeline2.IProducer;

/**
 * @author HargraveJE
 *
 */
public class ConsumerProducer implements IConsumer, IPipelineStep, IProducer {
	private BlockingQueue<IPipelineEvent> producerQueue;
	private BlockingQueue<IPipelineEvent> consumerQueue;
	private boolean stop = false;
	
	public void setConsumerQueue(BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void setProducerQueue(BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}
		
	public void run() {
		while (true) {
			try {
				if (stop) return;
				IPipelineEvent event = consumerQueue.take();
				producerQueue.add(event);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}

	}
	
	public void stop() {
		stop = true;
	}
}
