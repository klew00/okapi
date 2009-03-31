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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.FileResource;

public class Pipeline implements IPipeline {
	LinkedList<IPipelineStep> steps;
	LinkedList<IPipelineStep> finishedSteps;
	volatile boolean cancel = false;
	private boolean done = false;
	private boolean destroyed = false;
	private Event event;

	public Pipeline() {
		steps = new LinkedList<IPipelineStep>();
		finishedSteps = new LinkedList<IPipelineStep>();
	}

	private void initialize() {
		// copy all the finished steps from previous run
		for (IPipelineStep step : finishedSteps) {
			steps.add(step);
		}
		finishedSteps.clear();

		cancel = false;
		done = false;
		destroyed = false;
	}

	public void addStep(IPipelineStep step) {
		if (destroyed) {
			throw new RuntimeException("Pipeline has been destroyed and must be reinitialized");
		}
		steps.add(step);
	}

	public void cancel() {
		cancel = true;
	}

	private Event execute(Event input) {
		event = input;
		
		// loop through the events until we run out
		while (!steps.isEmpty() && !cancel) {
			while (steps.getFirst().hasNext() && !cancel) {
				for (IPipelineStep step : steps) {
					event = step.handleEvent(event);
				}
			}
			// as each step exhausts its events remove it from the list and move
			// on to the next
			finishedSteps.add(steps.remove());
		}

		// copy any remaining steps into finishedSteps - makes initialization
		// process easier down the road if we reuse the pipeline
		for (IPipelineStep step : steps) {
			finishedSteps.add(step);
		}
		steps.clear();

		if (cancel) {
			for (IPipelineStep step : finishedSteps) {
				step.cancel();
			}
		}

		return event;
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
	 * @see net.sf.okapi.common.pipeline.IPipeline#process(FileResource)
	 */
	public FileResource process(FileResource input) {
		initialize();
		preprocess();
		Event e = execute(new Event(EventType.FILE_RESOURCE, input));
		postprocess();
		done = true;

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#process(Event)
	 */
	public Event process(Event input) {
		initialize();
		preprocess();
		Event e = execute(input);
		postprocess();
		done = true;

		return e;
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#preprocess()
	 */
	private void preprocess() {
		// finishedSteps is empty - we preprocess on the steps waiting to be
		// processed.
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
		destroyed = true;
	}
}
