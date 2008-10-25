package net.sf.okapi.applications.rainbow.utilities;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilterWriter;
import net.sf.okapi.common.threadedpipeline.BasePipelineStep;
import net.sf.okapi.common.threadedpipeline.IConsumer;
import net.sf.okapi.common.threadedpipeline.PipelineReturnValue;

public class FilterWriterStep  extends BasePipelineStep implements IConsumer {

	private BlockingQueue<FilterEvent> consumerQueue;
	private IFilterWriter filterWriter;

	public FilterWriterStep (IFilterWriter filterWriter) {
		this.filterWriter = filterWriter;
	}
	
	public String getName() {
		return filterWriter.getName();
	}

	public void setOutput (String outputPath) {
		filterWriter.setOutput(outputPath);
	}
	
	public void setOutput (OutputStream outputStream) {
		filterWriter.setOutput(outputStream);
	}
	
	public IFilterWriter getFilterWriter () {
		return filterWriter;
	}
	
	public void setConsumerQueue (BlockingQueue<FilterEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void finish () throws InterruptedException {
		if ( filterWriter != null ) filterWriter.close();
	}

	public void initialize () throws InterruptedException {
	}

	public PipelineReturnValue process () throws InterruptedException {
		// Get the event from the queue
		FilterEvent event = (FilterEvent)consumerQueue.take();
		// Feed it to the output filter
		filterWriter.handleEvent(event);
		// Stop if it's the end of the document
//		if ( event.getEventType() == IFilterWriter.END_DOCUMENT ) {
//			return PipelineReturnValue.SUCCEDED;
//		}
		return PipelineReturnValue.RUNNING;
	}

}
