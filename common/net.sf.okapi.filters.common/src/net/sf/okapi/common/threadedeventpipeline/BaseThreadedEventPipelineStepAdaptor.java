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

package net.sf.okapi.common.threadedeventpipeline;

import java.util.concurrent.Callable;

import net.sf.okapi.common.eventpipeline.BaseEventPipelineStep;
import net.sf.okapi.common.eventpipeline.IEventPipelineStep;
import net.sf.okapi.common.pipeline.PipelineReturnValue;

public abstract class BaseThreadedEventPipelineStepAdaptor extends BaseEventPipelineStep implements Callable<PipelineReturnValue> {
	protected IEventPipelineStep step;
	private PipelineReturnValue result;
	private volatile boolean pause;

	public BaseThreadedEventPipelineStepAdaptor(IEventPipelineStep step) {
		this.step = step;
	}

	public String getName() {
		return step.getName();
	}

	public void postprocess() {
		step.postprocess();
	}

	public void preprocess() {
		step.preprocess();
	}

	public void cancel() {
		step.cancel();
	}

	public void pause() {
		pause = true;
	}

	public synchronized void resume() {
		pause = false;
		notify();
	}

	protected abstract PipelineReturnValue processBlockingQueue();

	public PipelineReturnValue call() throws Exception {
		result = PipelineReturnValue.RUNNING;
		try {
			preprocess();
			while (result == PipelineReturnValue.RUNNING) {

				PipelineReturnValue result = processBlockingQueue();

				if (result != PipelineReturnValue.RUNNING && result != PipelineReturnValue.PAUSED) {
					return result;
				}

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
		} catch (RuntimeInterruptedException e) {
			return PipelineReturnValue.INTERRUPTED;
		} finally {
			postprocess();
		}

		return result;
	}
}
