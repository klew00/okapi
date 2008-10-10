package net.sf.okapi.apptest.filters;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.BasePipelineStep;
import net.sf.okapi.common.pipeline2.IConsumer;
import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

public class OutputFilterStep  extends BasePipelineStep implements IConsumer {

	private BlockingQueue<IPipelineEvent> consumerQueue;
	private IOutputFilter2 outputFilter;

	public void setOutputFilter (IOutputFilter2 outputFilter) {
		this.outputFilter = outputFilter;
	}
	
	public void setConsumerQueue (BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void finish () throws InterruptedException {
	}

	public String getName() {
		return "OutputFilterStep";
	}

	public void initialize () throws InterruptedException {
	}

	public PipelineReturnValue process () throws InterruptedException {
		IPipelineEvent event = consumerQueue.take();
		outputFilter.process((PipelineEvent)event);
		if ( event.getEventType() == PipelineEventType.FINISHED ) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}

}
