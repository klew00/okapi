/**
 * 
 */
package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IPipelineStep;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

/**
 * @author HargraveJE
 * 
 */
public class Consumer implements IConsumer, IPipelineStep {
	private BlockingQueue<IPipelineEvent> consumerQueue;
	private volatile boolean pause;

	public void setConsumerQueue(BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public String getName() {
		return "Consumer";
	}

	public PipelineReturnValue call() throws Exception {
		while (true) {
			try {				
				IPipelineEvent event = consumerQueue.take();				
				System.out.println("EventType: " + event.getEventType().name());
				System.out.println("Order: " + Integer.valueOf(event.getOrder()));
				System.out.println();
				if (event.getEventType() == PipelineEventType.FINISHED) {
					return PipelineReturnValue.SUCCEDED;
				}
				
				if (pause) {
                    synchronized(this) {
                        while (pause)
                            wait();
                    }
                }
				
			} catch (InterruptedException e) {
				return PipelineReturnValue.INTERRUPTED;
			}
		}
	}

	public void pause() {
		pause = true;
	}

	public synchronized void resume() {
		pause = false;
		notify();	
	}
}
