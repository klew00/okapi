package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IPipelineStep;
import net.sf.okapi.common.pipeline2.IProducer;
import net.sf.okapi.common.pipeline2.PipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

public class Producer implements IProducer, IPipelineStep {
	private BlockingQueue<IPipelineEvent> producerQueue;
	private int order = -1;	

	public void setProducerQueue(BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}
	
	public String getName() {
		return "Producer";
	}

	public PipelineReturnValue call() throws Exception {
		producerQueue.put(new PipelineEvent(PipelineEventType.START, null, order));
		for (int i = 0; i < 10; i++) {
			order = i;
			try {
				Thread.sleep(500);
				producerQueue.put(new PipelineEvent(PipelineEventType.TEXTUNIT, null, order));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		producerQueue.put(new PipelineEvent(PipelineEventType.FINISHED, null, ++order));
		return PipelineReturnValue.SUCCEDED;			
	}
}
