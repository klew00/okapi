package net.sf.okapi.apptest.utilities;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.BasePipelineStep;
import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IProducer;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

public class UtilityStep extends BasePipelineStep implements IConsumer, IProducer {

	private BlockingQueue<IPipelineEvent> producerQueue;
	private BlockingQueue<IPipelineEvent> consumerQueue;
	private IUtility2 utility;

	public void setUtility (IUtility2 utility) {
		this.utility = utility;
	}
	
	public void setConsumerQueue (BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void setProducerQueue (BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public void finish () throws InterruptedException {
	}

	public String getName() {
		return "UtilityStep";
	}

	public void initialize () throws InterruptedException {
	}

	public PipelineReturnValue process() throws InterruptedException {
		IPipelineEvent event = consumerQueue.take();
		
		utility.process(event);
		
		producerQueue.add(event);
		if ( event.getEventType() == PipelineEventType.FINISHED ) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}

}
