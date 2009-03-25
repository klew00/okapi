/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.pipeline;

import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.FileResource;

public class Pipeline implements IPipeline {
	List<IPipelineStep> steps;
	List<IPipelineStep> finishedSteps;
	volatile boolean cancel = false;
	private boolean done = false;
	private boolean destroyed = false;
	private Event event;

	public Pipeline() {
		steps = new LinkedList<IPipelineStep>();
		finishedSteps = new LinkedList<IPipelineStep>();
		initialize();
	}

	private void initialize() {
		cancel = false;
		done = false;
		destroyed = false;
	}

	public void addStep(IPipelineStep step) {
		if (destroyed) {
			throw new RuntimeException(
					"Pipeline has been destroyed and must be reinitialized");
		}
		steps.add(step);
	}

	public void cancel() {
		cancel = true;
	}

	private void execute(FileResource input) {
		initialize();

		// first event is always the FileResource event we use it to prime the
		// pipeline
		event = new Event(EventType.FILE_RESOURCE, input);

		// loop through the events until we run out
		while (!steps.isEmpty() && !cancel) {
			while (steps.get(0).hasNext() && !cancel) {				
				for (IPipelineStep step : steps) {
					event = step.handleEvent(event);
				}
			}
			// as each step exhausts its events remove it from the list and move
			// on to the next
			finishedSteps.add(steps.remove(0));
		}

		if (cancel) {
			for (IPipelineStep step : finishedSteps) {
				step.cancel();
			}
			for (IPipelineStep step : steps) {
				step.cancel();
			}
		}
	}

	public PipelineReturnValue getState() {
		if (destroyed)
			return PipelineReturnValue.DESTROYED;
		else if (cancel)
			return PipelineReturnValue.CANCELLED;
		else if (done)
			return PipelineReturnValue.SUCCEDED;
		else
			return PipelineReturnValue.RUNNING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#process(java.net.URI)
	 */
	public FileResource process(FileResource input) {
		preprocess();
		execute(input);
		postprocess();
		done = true;

		return null; // TODO: How to get pipeline to always return a FileResource?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#postprocess()
	 */
	private void postprocess() {
		for (IPipelineStep step : finishedSteps) {
			step.postprocess();
		}
		for (IPipelineStep step : steps) {
			step.postprocess();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#preprocess()
	 */
	private void preprocess() {
		for (IPipelineStep step : finishedSteps) {
			step.preprocess();
		}
		for (IPipelineStep step : steps) {
			step.preprocess();
		}
	}

	/*
	 * Destroy this pipeline and call destroy on each pipeline step. Cleanup
	 * code should go here.
	 */
	public void destroy() {
		for (IPipelineStep step : finishedSteps) {
			step.destroy();
		}
		for (IPipelineStep step : steps) {
			step.destroy();
		}
		destroyed = true;
	}
}
