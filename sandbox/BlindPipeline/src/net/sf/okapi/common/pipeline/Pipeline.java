/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.common.pipeline;

import java.util.LinkedList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Implements the {@link IPipeline} interface.
 */
public class Pipeline implements IPipeline {

	private LinkedList<IPipelineStep> steps;
	private LinkedList<IPipelineStep> finishedSteps;
	private volatile boolean cancel = false;
	private boolean done = false;
	private boolean destroyed = false;
	private PipelineContext context;

	public Pipeline() {
		steps = new LinkedList<IPipelineStep>();
		finishedSteps = new LinkedList<IPipelineStep>();
		context = new PipelineContext();
	}

	private void initialize () {
		// Copy all the finished steps from previous run
		for (IPipelineStep step : finishedSteps) {
			steps.add(step);
		}
		finishedSteps.clear();
	}

	public void startBatch () {
		initialize();
		cancel = false;
		done = false;
		destroyed = false;

		Event event = new Event(EventType.START_BATCH);
		for ( IPipelineStep step : steps ) {
			event = step.handleEvent(event);
		}
	}

	public Event endBatch () {		
		// Non-terminal steps will return END_BATCH after receiving END_BATCH
		// Terminal steps return an event which may be anything,
		// including a CUSTOM event. The pipeline returns this final event.
		Event event = Event.END_BATCH_EVENT;
		for ( IPipelineStep step : steps ) {
			event = step.handleEvent(Event.END_BATCH_EVENT);
		}
		done = true;
		return event;
	}

	public void addStep (IPipelineStep step) {
		step.setPipeline(this);
		steps.add(step);
	}

	public void cancel() {
		cancel = true;
	}

	private void execute (Event event) {
		// loop through the events until we run out of steps or hit cancel
		while ( !steps.isEmpty() && !cancel ) {
			// cycle through the steps in order, pulling off steps that run out
			// of events.
			while ( steps.getFirst().hasNext() && !cancel ) {
				// go to each active step and call handleEvent
				// the event returned is used as input to the next pass
				for ( IPipelineStep step : steps ) {
					event = step.handleEvent(event);
				}
				// Get ready for another pass down the pipeline
				// overwrite the terminal steps event
				event = Event.NOOP_EVENT;
			}
			// As each step exhausts its events remove it from the list and move
			// on to the next
			finishedSteps.add(steps.remove());
		}
	}

	public PipelineReturnValue getState() {
		if ( destroyed )
			return PipelineReturnValue.DESTROYED;
		else if ( cancel )
			return PipelineReturnValue.CANCELLED;
		else if ( done )
			return PipelineReturnValue.SUCCEDED;
		else
			return PipelineReturnValue.RUNNING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#process(FileResource)
	 */
	public Event process (RawDocument input) {
		return process(new Event(EventType.RAW_DOCUMENT, input));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.pipeline.IPipeline#process(Event)
	 */
	public Event process (Event input) {
		initialize();

		// Pre-process for this batch-item
		Event e = new Event(EventType.START_BATCH_ITEM);
		for ( IPipelineStep step : steps ) {
			step.handleEvent(e);
		}
		
		// Prime the pipeline with the input Event and run it to completion.
		execute(input);
		// Copy any remaining steps into finishedSteps - makes initialization
		// process easier down the road if we use the pipeline again
		for (IPipelineStep step : steps) {
			finishedSteps.add(step);
		}
		steps.clear();

		// Post-process for this batch-item
		e = new Event(EventType.END_BATCH_ITEM);
		for ( IPipelineStep step : steps ) {
			step.handleEvent(e);
		}
		return e;
	}

	/*
	 * Destroy this pipeline and call destroy on each pipeline step. Cleanup
	 * code should go here.
	 */
	public void destroy () {
		for (IPipelineStep step : finishedSteps) {
			step.destroy();
		}
		destroyed = true;
	}

	public int inputCountRequested () {
		int max = 0;
		for ( IPipelineStep step : steps ) {
			if ( step.inputCountRequested() > max ) {
				max = step.inputCountRequested();
			}
		}
		return max;
	}

	public boolean needsOutput (int inputIndex) {
		for ( IPipelineStep step : steps ) {
			if ( step.needsOutput(inputIndex) ) {
				return true;
			}
		}
		return false;
	}

	public PipelineContext getContext () {
		return context;
	}

	public void setContext (PipelineContext context) {
		this.context = context;
	}

}
