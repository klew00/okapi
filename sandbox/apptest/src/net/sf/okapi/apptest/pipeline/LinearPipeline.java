/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.apptest.pipeline;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class LinearPipeline<T> implements ILinearPipeline {
	private static final int DEFAULT_BLOCKING_QUEUE_SIZE = 10;

	private final PausableThreadPoolExecutor executor;
	private final CompletionService<PipelineReturnValue> completionService;
	private final int blockingQueueSize;
	private int totalThreads;
	private PipelineReturnValue state;
	private LinkedList<IPipelineStep> steps;

	private BlockingQueue<T> previousQueue;

	public LinearPipeline() {
		this(PausableThreadPoolExecutor.newCachedThreadPool(), DEFAULT_BLOCKING_QUEUE_SIZE);
	}

	public LinearPipeline(PausableThreadPoolExecutor executor, int blockingQueueSize) {
		totalThreads = 0;
		this.executor = executor;
		this.blockingQueueSize = blockingQueueSize;
		this.completionService = new ExecutorCompletionService<PipelineReturnValue>(this.executor);
		this.executor.pause();
		state = PipelineReturnValue.PAUSED;
		steps = new LinkedList<IPipelineStep>();
	}

	@SuppressWarnings("unchecked")
	public void addPipleLineStep(IPipelineStep step) {
		BlockingQueue<T> queue = null;
		if (step instanceof IConsumer && step instanceof IProducer) {
			if (previousQueue == null) {
				// TODO: wrap exception
				throw new RuntimeException("Previous queue should not be null");
			}

			queue = new ArrayBlockingQueue<T>(blockingQueueSize, false);
			((IProducer<T>)step).setProducerQueue(queue);
			((IConsumer<T>) step).setConsumerQueue(previousQueue);
		} else if (step instanceof IProducer) {
			queue = new ArrayBlockingQueue<T>(blockingQueueSize, false);
			((IProducer<T>) step).setProducerQueue(queue);
		} else if (step instanceof IConsumer) {
			if (previousQueue == null) {
				// TODO: wrap exception
				throw new RuntimeException("Previous queue should not be null");
			}
			((IConsumer<T>)step).setConsumerQueue(previousQueue);

		} else {
			// TODO: wrap exception
			throw new RuntimeException();
		}

		completionService.submit(step);
		steps.add(step);
		totalThreads += 1;
		previousQueue = queue;
	}

	public void addPipleLineStep(IPipelineStep step, int numThreads) {
		throw new RuntimeException("Not implemented");
	}

	public void cancel() {
		pause(); // pause all threads to make sure they are a good stopping point for destruction
		//TODO : how do we wait for pause to happen for all tasks? Does shutdown throw a interrupted exception?
		executor.shutdownNow();
		state = PipelineReturnValue.CANCELLED;
	}

	public void pause() {
		executor.pause();
		for (IPipelineStep step : steps) {
			step.pause();
		}
		state = PipelineReturnValue.PAUSED;
	}

	public void resume() {
		executor.resume();
		for (IPipelineStep step : steps) {
			step.resume();
		}
		state = PipelineReturnValue.RUNNING;
	}

	public void execute() {
		executor.resume();
		state = PipelineReturnValue.RUNNING;
	}

	public PipelineReturnValue getState() {
		if (state == PipelineReturnValue.CANCELLED) {
			return PipelineReturnValue.CANCELLED;
		}

		if (state == PipelineReturnValue.PAUSED) {
			//YS: assumes this try block was needed just for test, otherwise
			// getState() prevent any real UI operation
			/*try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				return PipelineReturnValue.INTERRUPTED;
			}*/
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
}
