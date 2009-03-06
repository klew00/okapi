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

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;

public class Pipeline implements IPipeline {
	List<IPipelineStep> steps;
	IPipelineStep initialStep;
	volatile boolean cancel = false;
	boolean first = true;
	boolean startEventSent = false;
	private boolean done = false;

	public Pipeline() {
		steps = new ArrayList<IPipelineStep>();
		initialize();
	}

	private void initialize() {
		cancel = false;
		first = true;
	}

	public void addStep(IPipelineStep step) {
		if (first) {
			if (!(step instanceof IInitialStep)) {
				throw new RuntimeException("Intial step must implement IInitialStep");
			}
			initialStep = step;
			first = false;
		} else {
			steps.add(step);
		}
	}

	public void cancel() {
		// TODO handle cancel - do we put execute on its own thread?
		cancel = true;
	}

	private void execute() {
		done = false;
		initialize();

		// loop through the events until we run out
		while (((IInitialStep) initialStep).hasNext() && !cancel) {
			Event event = initialStep.handleEvent(null);
			for (IPipelineStep step : steps) {
				step.handleEvent(event);
			}
		}
		if (cancel) {
			initialStep.cancel();
			for (IPipelineStep step : steps) {
				step.cancel();
			}
		}
		done = true;
	}

	public PipelineReturnValue getState() {
		if (cancel)
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
	public void process(URI input) {
		((IInitialStep) initialStep).setInput(input);
		preprocess();
		execute();
		postprocess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#process(java.io.InputStream)
	 */
	public void process(InputStream input) {
		preprocess();
		((IInitialStep) initialStep).setInput(input);
		execute();
		postprocess();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.sf.okapi.common.pipeline.IPipeline#process(CharSequence)
	 */
	public void process(CharSequence input) {
		((IInitialStep) initialStep).setInput(input);
		preprocess();
		execute();
		postprocess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#close()
	 */
	public void destroy() {		
		initialStep.destroy();
		for (IPipelineStep step : steps) {
			step.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#postprocess()
	 */
	private void postprocess() {
		initialStep.postprocess();
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
		initialStep.preprocess();
		for (IPipelineStep step : steps) {
			step.preprocess();
		}
	}
}
