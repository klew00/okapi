package net.sf.okapi.apptest.pipelineutil;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.IInputFilter;
import net.sf.okapi.apptest.pipeline.BasePipelineStep;
import net.sf.okapi.apptest.pipeline.IConsumer;
import net.sf.okapi.apptest.pipeline.IPipelineEvent;
import net.sf.okapi.apptest.pipeline.IProducer;
import net.sf.okapi.apptest.pipeline.PipelineReturnValue;
import net.sf.okapi.apptest.utilities.IUtility;

public class UtilityStep extends BasePipelineStep implements IConsumer, IProducer {

	private BlockingQueue<IPipelineEvent> producerQueue;
	private BlockingQueue<IPipelineEvent> consumerQueue;
	private IUtility utility;

	public void setUtility (IUtility utility) {
		this.utility = utility;
	}
	
	public void setConsumerQueue (BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void setProducerQueue (BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public void finish () throws InterruptedException {
		if ( utility != null ) utility.doEpilog();
	}

	public String getName() {
		return utility.getName();
	}

	public void initialize () throws InterruptedException {
	}

	public PipelineReturnValue process() throws InterruptedException {
		// Get the event from the queue
		IPipelineEvent event = consumerQueue.take();
		// Feed it to the utility
		utility.handleEvent(event.getEventType(), (IResource)event.getData());
		// Pass it to the next step
		producerQueue.add(event);
		// End the process if it's the end of the document
		if ( event.getEventType() == IInputFilter.END_DOCUMENT ) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}

}
