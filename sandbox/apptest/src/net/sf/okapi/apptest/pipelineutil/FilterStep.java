package net.sf.okapi.apptest.pipelineutil;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilter;
import net.sf.okapi.apptest.pipeline.IPipelineStep;
import net.sf.okapi.apptest.pipeline.IProducer;
import net.sf.okapi.apptest.pipeline.PipelineReturnValue;

public class FilterStep implements IPipelineStep, IProducer<FilterEvent> {

	protected BlockingQueue<FilterEvent> producerQueue;
	protected int order;
	protected volatile boolean pause;
	protected PipelineReturnValue result;
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
		filter.open(inputPath);
	}

	public void pause () {
		pause = true;
	}

	public synchronized void resume () {
		pause = false;
		notify();
	}

	public PipelineReturnValue call() throws Exception {
		result = PipelineReturnValue.RUNNING;
		try {
			initialize();
			while ( result == PipelineReturnValue.RUNNING ) {
				result = process();
				if ( pause ) {
					synchronized(this) {
						while ( pause ) {
							wait();
						}
					}
				}
				// Interrupted exception only thrown in waiting mode
				if ( Thread.currentThread().isInterrupted() ) {
					return PipelineReturnValue.INTERRUPTED;
				}
			}
		}
		catch ( InterruptedException e ) {
			return PipelineReturnValue.INTERRUPTED;
		}
		finally {
			finish();
		}

		return result;
	}

	public void setProducerQueue (BlockingQueue<FilterEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public PipelineReturnValue process () throws InterruptedException {
		if ( !filter.hasNext() ) {
			return PipelineReturnValue.SUCCEDED; // Done
		}
		producerQueue.put(filter.next());
		return PipelineReturnValue.RUNNING;
	}

}
