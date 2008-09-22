/**
 * 
 */
package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IPipelineStep;

/**
 * @author HargraveJE
 * 
 */
public class Consumer implements IConsumer, IPipelineStep {
	private BlockingQueue<IPipelineEvent> consumerQueue;
	private boolean stop = false; 
	
	public void setConsumerQueue(BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void run() {
		while (true) {
			try {
				if (stop) return;
				IPipelineEvent event = consumerQueue.take();
				System.out.println("EventType: " + event.getEventType().name());
				System.out.println("Order: " + Integer.valueOf(event.getOrder()));
				System.out.println();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}	
	}

	public void stop() {
		stop = true;		
	}
}
