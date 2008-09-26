package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.BasePipelineStep;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IProducer;
import net.sf.okapi.common.pipeline2.PipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

public class Producer extends BasePipelineStep implements IProducer {
	private BlockingQueue<IPipelineEvent> producerQueue;
	private int order = -1;

	public void setProducerQueue(BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public String getName() {
		return "Producer";
	}

	public void finish() throws InterruptedException {
		producerQueue.put(new PipelineEvent(PipelineEventType.FINISHED, null, ++order));
	}

	public void initialize() throws InterruptedException {
		producerQueue.put(new PipelineEvent(PipelineEventType.START, null, ++order));
	}

	public PipelineReturnValue process() throws InterruptedException {
		if (order >= 10) {
			return PipelineReturnValue.SUCCEDED;
		}

		Thread.sleep(2000);
		producerQueue.put(new PipelineEvent(PipelineEventType.TEXTUNIT, null, ++order));

		return PipelineReturnValue.RUNNING;
	}
}
