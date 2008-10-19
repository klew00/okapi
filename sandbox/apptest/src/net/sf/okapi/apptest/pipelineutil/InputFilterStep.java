package net.sf.okapi.apptest.pipelineutil;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.apptest.filters.IInputFilter;
import net.sf.okapi.apptest.pipeline.PipelineEvent;
import net.sf.okapi.apptest.pipeline.IPipelineEvent;
import net.sf.okapi.apptest.pipeline.IPipelineStep;
import net.sf.okapi.apptest.pipeline.IProducer;
import net.sf.okapi.apptest.pipeline.PipelineReturnValue;

public class InputFilterStep implements IPipelineStep, IProducer {

	protected BlockingQueue<IPipelineEvent> producerQueue;
	protected int order;
	protected volatile boolean pause;
	protected PipelineReturnValue result;
	protected IInputFilter inputFilter;
	protected String inputPath;

	public String getName() {
		return inputFilter.getName();
	}

	public void setInput (String inputPath) {
		this.inputPath = inputPath;
	}
	
	public void setInputFilter (IInputFilter inputFilter) {
		this.inputFilter = inputFilter;
	}
	
	public IInputFilter getInputFilter () {
		return inputFilter;
	}
	
	public void finish () throws InterruptedException {
		if ( inputFilter != null ) inputFilter.close();
	}

	public void initialize () throws InterruptedException {
		order = -1;
		inputFilter.open(inputPath);
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

	public void setProducerQueue (BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public PipelineReturnValue process () throws InterruptedException {
		if ( !inputFilter.hasNext() ) {
			return PipelineReturnValue.SUCCEDED; // Done
		}
		int event = inputFilter.next();
		producerQueue.put(new PipelineEvent(event, inputFilter.getResource(), ++order));
		return PipelineReturnValue.RUNNING;
	}

}
