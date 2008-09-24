package net.sf.okapi.common.pipeline2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LinearPipeline implements ILinearPipeline {
	private static final int DEFAULT_BLOCKING_QUEUE_SIZE = 10;

	private final PausableThreadPoolExecutor executor;
	private final CompletionService<PipelineReturnValue> completionService;
	private final int blockingQueueSize;
	private int totalThreads;
	private boolean first = true;
	private PipelineReturnValue state;

	private BlockingQueue<IPipelineEvent> previousQueue;

	public LinearPipeline() {
		this(PausableThreadPoolExecutor.newCachedThreadPool(), DEFAULT_BLOCKING_QUEUE_SIZE);
	}

	public LinearPipeline(PausableThreadPoolExecutor executor, int blockingQueueSize) {
		totalThreads = 0;
		this.executor = executor;
		this.blockingQueueSize = blockingQueueSize;
		this.completionService = new ExecutorCompletionService<PipelineReturnValue>(this.executor);
		this.executor.pause();
		first = true;
		state = PipelineReturnValue.PAUSED;
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

		completionService.submit(step);
		totalThreads += 1;
		previousQueue = queue;
	}

	public void addPipleLineStep(IPipelineStep step, int numThreads) {
		throw new RuntimeException("Not implemented");
	}

	public void cancel() {
		executor.shutdownNow();
		state = PipelineReturnValue.CANCELLED;
	}

	public void pause() {
		synchronized (completionService) {
			try {
				completionService.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		state = PipelineReturnValue.PAUSED;
		executor.pause();
	}

	public void resume() {
		synchronized (completionService) {
			completionService.notifyAll();
		}
		executor.resume();
		state = PipelineReturnValue.RUNNING;
	}

	public PipelineReturnValue execute() {
		if (first) {
			first = false;
			executor.resume();
			state = PipelineReturnValue.RUNNING;
		}
		
		if (state == PipelineReturnValue.CANCELLED) {
			return PipelineReturnValue.CANCELLED;
		}

		if (state == PipelineReturnValue.PAUSED) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return PipelineReturnValue.INTERRUPTED;
			}
			return PipelineReturnValue.PAUSED;
		}

		try {
			Future<PipelineReturnValue> f = completionService.poll(100, TimeUnit.MILLISECONDS);
			if (f != null) {
				--totalThreads;
				PipelineReturnValue result = f.get();
				if (result != PipelineReturnValue.SUCCEDED) {
					return PipelineReturnValue.FAILED;
				}
				return PipelineReturnValue.RUNNING;
			}
			if (totalThreads <= 0) {
				return PipelineReturnValue.SUCCEDED;
			}
			return PipelineReturnValue.RUNNING;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return PipelineReturnValue.INTERRUPTED;
		} catch (ExecutionException e) {
			return PipelineReturnValue.FAILED;
		}
	}

	public PipelineReturnValue getState() {
		return state;
	}
}
