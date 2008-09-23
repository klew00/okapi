/**
 * 
 */
package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IPipelineStep;
import net.sf.okapi.common.pipeline2.IProducer;
import net.sf.okapi.common.pipeline2.PipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

/**
 * @author HargraveJE
 * 
 */
public class Producer implements IProducer, IPipelineStep {
	private BlockingQueue<IPipelineEvent> producerQueue;
	private int order;
	private boolean stop = false;

	public void setProducerQueue(BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public void run() {		
			for (int i = 0; i < 10; i++) {
				order = i;
				try {
					Thread.sleep(2000);
					producerQueue.put(new PipelineEvent(PipelineEventType.TEXTUNIT, null, order));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
	}

	public void stop() {
		stop = true;
	}
}
