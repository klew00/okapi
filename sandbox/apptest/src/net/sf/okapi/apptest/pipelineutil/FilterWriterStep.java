package net.sf.okapi.apptest.pipelineutil;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.filters.FilterEvent.FilterEventType;
import net.sf.okapi.apptest.pipeline.BasePipelineStep;
import net.sf.okapi.apptest.pipeline.IConsumer;
import net.sf.okapi.apptest.pipeline.PipelineReturnValue;

public class FilterWriterStep extends BasePipelineStep implements IConsumer<FilterEvent> {

	private BlockingQueue<FilterEvent> consumerQueue;
	private IFilterWriter filterWriter;

	public FilterWriterStep (IFilterWriter outputFilter) {
		this.filterWriter = outputFilter;
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

	public String getName() {
		return filterWriter.getName();
	}

	public void initialize () throws InterruptedException {
	}

	public PipelineReturnValue process () throws InterruptedException {
		// Get the event from the queue
		FilterEvent event = consumerQueue.take();
		// Feed it to the output filter
		filterWriter.handleEvent(event);
		// Stop if it's the end of the document
		if ( event.getEventType() == FilterEventType.END_DOCUMENT ) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}

}
