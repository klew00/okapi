package net.sf.okapi.applications.rainbow.utilities;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.threadedpipeline.BasePipelineStep;
import net.sf.okapi.common.threadedpipeline.IProducer;
import net.sf.okapi.common.threadedpipeline.PipelineReturnValue;

public class FilterStep extends BasePipelineStep implements IProducer {

	protected BlockingQueue<FilterEvent> producerQueue;
	protected int order;
	protected IFilter filter;
	protected String inputPath;

	public FilterStep (IFilter filter) {
		this.filter = filter;
	}
	
	public String getName() {
		return filter.getName();
	}

	public void setInput (String inputPath) {
		this.inputPath = inputPath;
	}
	
	public IFilter getFilter () {
		return filter;
	}
	
	public void finish () throws InterruptedException {
		if ( filter != null ) filter.close();
	}

	public void initialize () throws InterruptedException {
		order = -1;
		// Should probably be done in process() 
		// I suppose the START event is for this???
		filter.open(inputPath);
	}

	public PipelineReturnValue process () throws InterruptedException {
		if ( !filter.hasNext() ) {
			return PipelineReturnValue.SUCCEDED; // Done
		}
		producerQueue.put(filter.next());
		return PipelineReturnValue.RUNNING;
	}

}
