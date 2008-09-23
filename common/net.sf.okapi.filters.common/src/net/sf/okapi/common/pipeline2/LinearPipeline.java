package net.sf.okapi.common.pipeline2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LinearPipeline implements ILinearPipeline {
	private static final int DEFAULT_THREADPOOL_SIZE = 10;
	private static final int DEFAULT_BLOCKING_QUEUE_SIZE = 10;

	private final PausableThreadPoolExecutor executor;
	private final CompletionService<PipelineReturnValue> pipelineSteps;
	private final int blockingQueueSize;
	private int totalThreads;
	private boolean pause = false;

	private BlockingQueue<IPipelineEvent> previousQueue;

	public LinearPipeline() {		
		this(PausableThreadPoolExecutor.newFixedThreadPool(DEFAULT_THREADPOOL_SIZE), DEFAULT_BLOCKING_QUEUE_SIZE);		
	}

	public LinearPipeline(PausableThreadPoolExecutor executor, int blockingQueueSize) {
		totalThreads = 0;
		this.executor = executor;
		this.blockingQueueSize = blockingQueueSize;
		this.pipelineSteps = new ExecutorCompletionService<PipelineReturnValue>(this.executor);
		pause = false;
	}

	public void addPipleLineStep(IPipelineStep step) {
		BlockingQueue<IPipelineEvent> queue = null;
		if (step instanceof IConsumer && step instanceof IProducer) {
			if (previousQueue == null) {
				// TODO: wrap exception
				throw new RuntimeException("Previous queue should not be null");
			}

			queue = new ArrayBlockingQueue<IPipelineEvent>(blockingQueueSize, false);

			((IProducer) step).setProducerQueue(queue);
			((IConsumer) step).setConsumerQueue(previousQueue);
		} else if (step instanceof IProducer) {
			queue = new ArrayBlockingQueue<IPipelineEvent>(blockingQueueSize, false);

			((IProducer) step).setProducerQueue(queue);
		} else if (step instanceof IConsumer) {
			if (previousQueue == null) {
				// TODO: wrap exception
				throw new RuntimeException("Previous queue should not be null");
			}

			((IConsumer) step).setConsumerQueue(previousQueue);

		} else {
			// TODO: wrap exception
			throw new RuntimeException();
		}

		pipelineSteps.submit(step);
		totalThreads += 1;

		previousQueue = queue;
	}

	public void addPipleLineStep(IPipelineStep step, int numThreads) {

	}

	public void cancel() {		
	}

	public void pause() {
		executor.pause();		
	}

	public void resume() {
		executor.resume();
	}

	public boolean execute() {
		try {
			Future<PipelineReturnValue> f = pipelineSteps.poll(100, TimeUnit.MILLISECONDS);	
			if (f != null) {
				--totalThreads;
			}
			if (totalThreads <= 0) {
				return false;
			}
			return true;
//			for (int t = 0, n = totalThreads; t < n; t++) {
//				Future<PipelineReturnValue> f = pipelineSteps.take();
//				PipelineReturnValue result = f.get();
//			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
//		} catch (ExecutionException e) {
//			return true;
//		}		
	}
}
