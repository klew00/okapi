package net.sf.okapi.common.pipeline2;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LinearPipeline implements ILinearPipeline {
	private LinkedList<Thread> pipelineSteps;
	private BlockingQueue<IPipelineEvent> previousQueue;
	private int queueSize;
	private boolean stop;
	
	public LinearPipeline(int queueSize) {
		pipelineSteps = new LinkedList<Thread>();
		this.queueSize = queueSize;		
	}
	
	public void addPipleLineStep(IPipelineStep step) {
		BlockingQueue<IPipelineEvent> queue = null;		
		if (step instanceof IConsumer && step instanceof IProducer) {
			queue = new ArrayBlockingQueue<IPipelineEvent>(queueSize, false);
			((IProducer)step).setProducerQueue(queue);
			((IConsumer)step).setConsumerQueue(queue);			
		} else if (step instanceof IProducer) {
			queue = new ArrayBlockingQueue<IPipelineEvent>(queueSize, false);
			((IProducer)step).setProducerQueue(queue);
		} else if (step instanceof IConsumer) {
			if (previousQueue != null) {
				((IConsumer)step).setConsumerQueue(previousQueue);
			} else {
				// TODO: wrap exception
				throw new RuntimeException();
			}
			
		} else {
			// TODO: wrap exception
			throw new RuntimeException();
		}
		pipelineSteps.add(new Thread(step));		
		previousQueue = queue;
	}

	public void addPipleLineStep(IPipelineStep step, int numThreads) {
	}

	public void cancel() {
		stop = true;
	}

	public void pause() {
	}

	public void resume() {
	}

	public void start() {		
		for (Thread step : pipelineSteps) {
			step.start();		
		}
	}

	public void run() {
		while(!stop){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
