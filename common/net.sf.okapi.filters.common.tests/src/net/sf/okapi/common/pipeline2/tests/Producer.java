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
		try {
			producerQueue.put(new PipelineEvent(PipelineEventType.START, null,
					order));
			for (int i = 0; i < 10; i++) {
				order = i;

				Thread.sleep(2000);
				producerQueue.put(new PipelineEvent(PipelineEventType.TEXTUNIT,
						null, order));
			}
			producerQueue.put(new PipelineEvent(PipelineEventType.FINISHED,
					null, ++order));
		} catch (InterruptedException e) {
			return PipelineReturnValue.INTERRUPTED;
		}
		return PipelineReturnValue.SUCCEDED;
	}
}
