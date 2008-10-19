package net.sf.okapi.apptest.pipelineutil;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.IInputFilter;
import net.sf.okapi.apptest.filters.IOutputFilter;
import net.sf.okapi.apptest.pipeline.BasePipelineStep;
import net.sf.okapi.apptest.pipeline.IConsumer;
import net.sf.okapi.apptest.pipeline.IPipelineEvent;
import net.sf.okapi.apptest.pipeline.PipelineReturnValue;

public class OutputFilterStep  extends BasePipelineStep implements IConsumer {

	private BlockingQueue<IPipelineEvent> consumerQueue;
	private IOutputFilter outputFilter;

	public void setOutput (String outputPath) {
		outputFilter.setOutput(outputPath);
	}
	
	public void setOutput (OutputStream outputStream) {
		outputFilter.setOutput(outputStream);
	}
	
	public IOutputFilter getOutputFilter () {
		return outputFilter;
	}
	
	public void setOutputFilter (IOutputFilter outputFilter) {
		this.outputFilter = outputFilter;
	}
	
	public void setConsumerQueue (BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void finish () throws InterruptedException {
		if ( outputFilter != null ) outputFilter.close();
	}

	public String getName() {
		return outputFilter.getName();
	}

	public void initialize () throws InterruptedException {
	}

	public PipelineReturnValue process () throws InterruptedException {
		// Get the event from the queue
		IPipelineEvent event = consumerQueue.take();
		// Feed it to the output filter
		outputFilter.handleEvent(event.getEventType(), (IResource)event.getData());
		// Stop if it's the end of the document
		if ( event.getEventType() == IInputFilter.END_DOCUMENT ) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}

}
