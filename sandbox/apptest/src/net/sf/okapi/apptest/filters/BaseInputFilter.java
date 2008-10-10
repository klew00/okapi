package net.sf.okapi.apptest.filters;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.IPipelineStep;
import net.sf.okapi.common.pipeline2.IProducer;
import net.sf.okapi.common.pipeline2.PipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

public abstract class BaseInputFilter implements IPipelineStep, IProducer {

	protected BlockingQueue<IPipelineEvent> producerQueue;
	protected int order;
	protected volatile boolean pause;
	protected PipelineReturnValue result;

	public void finish () throws InterruptedException {
		producerQueue.put(new PipelineEvent(PipelineEventType.FINISHED, null, ++order));
	}

	public abstract String getName();

	public void initialize () throws InterruptedException {
		order = -1;
		producerQueue.put(new PipelineEvent(PipelineEventType.START, null, ++order));
	}

	public void pause () {
		pause = true;
	}

	public abstract PipelineReturnValue process () throws InterruptedException;

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

}
