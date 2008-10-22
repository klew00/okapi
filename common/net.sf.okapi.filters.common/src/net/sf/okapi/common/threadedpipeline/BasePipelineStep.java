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

package net.sf.okapi.common.threadedpipeline;

import java.util.concurrent.BlockingQueue;

public abstract class BasePipelineStep implements IPipelineStep {
	private PipelineReturnValue result;
	private volatile boolean pause;
	
	public void addToQueue(BlockingQueue<IPipelineEvent> producerQueue, IPipelineEvent event) throws InterruptedException {
		producerQueue.put(event);
	}

	public IPipelineEvent takeFromQueue(BlockingQueue<IPipelineEvent> consumerQueue) throws InterruptedException {
		return consumerQueue.take(); 
	}

	public void pause() {
		pause = true;
	}

	public synchronized void resume() {
		pause = false;
		notify();
	}

	public PipelineReturnValue call() throws Exception {
		result = PipelineReturnValue.RUNNING;
		try {
			initialize();
			while (result == PipelineReturnValue.RUNNING) {

				result = process();

				if (pause) {
					synchronized (this) {
						while (pause)
							wait();
					}
				}

				// Interrupted exception only thrown in waiting mode
				if (Thread.currentThread().isInterrupted()) {
					return PipelineReturnValue.INTERRUPTED;
				}
			}
		} catch (InterruptedException e) {
			return PipelineReturnValue.INTERRUPTED;
		} finally {
			finish();
		}

		return result;
	}
}
