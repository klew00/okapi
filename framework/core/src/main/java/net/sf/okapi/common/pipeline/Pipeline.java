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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.observer.BaseObservable;
import net.sf.okapi.common.observer.IObservable;
import net.sf.okapi.common.observer.IObserver;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Default implementations of the {@link IPipeline} interface.
 */
public class Pipeline implements IPipeline, IObservable, IObserver {

	private LinkedList<IPipelineStep> steps;
	private LinkedList<IPipelineStep> finishedSteps;
	private IContext context;
	private volatile PipelineReturnValue state;

	/**
	 * Creates a new Pipeline object.
	 */
	public Pipeline() {
		steps = new LinkedList<IPipelineStep>();
		finishedSteps = new LinkedList<IPipelineStep>();
		state = PipelineReturnValue.PAUSED;
	}

	private void initialize() {
		// Copy all the finished steps from previous run
		for (IPipelineStep step : finishedSteps) {
			steps.add(step);
		}
		finishedSteps.clear();

		// Initialize steps for this run
		for (IPipelineStep step : steps) {
			step.setLastStep(false);
			step.setContext(getContext());
		}
		steps.getLast().setLastStep(true);
	}

	public void startBatch() {
		state = PipelineReturnValue.RUNNING;
		
		initialize();		
		
		Event event = new Event(EventType.START_BATCH);
		for (IPipelineStep step : steps) {
			step.handleEvent(event);
		}		
		notifyObservers(event);
	}

	public void endBatch() {
		// Non-terminal steps will return END_BATCH after receiving END_BATCH
		// Terminal steps return an event which may be anything,
		// including a CUSTOM event. The pipeline returns this final event.
		// We run this on finishedSteps since steps is empty by the time we get
		// here
		Event event = Event.END_BATCH_EVENT;
		for (IPipelineStep step : finishedSteps) {
			step.handleEvent(Event.END_BATCH_EVENT);
		}				
		notifyObservers(event);
		
		state = PipelineReturnValue.SUCCEDED;		
	}

	public void addStep(IPipelineStep step) {
		steps.add(step);
	}

	public List<IPipelineStep> getSteps() {
		return new ArrayList<IPipelineStep>(steps);
	}

	public void cancel() {	
		state = PipelineReturnValue.CANCELLED;
	}

	private Event execute(Event event) {
		state = PipelineReturnValue.RUNNING;
		
		// loop through the events until we run out of steps or hit cancel
		while (!steps.isEmpty() && !(state == PipelineReturnValue.CANCELLED)) {
			// cycle through the steps in order, pulling off steps that run out
			// of events.
			while (!steps.getFirst().isDone() && !(state == PipelineReturnValue.CANCELLED)) {
				// go to each active step and call handleEvent
				// the event returned is used as input to the next pass
				for (IPipelineStep step : steps) {
					event = step.handleEvent(event);
				}				
				// notify observers that the final step has sent an Event
				notifyObservers(event);
			}
			// As each step exhausts its events remove it from the list and move
			// on to the next
			finishedSteps.add(steps.remove());
		}
		
		return event;
	}

	public PipelineReturnValue getState() {
		return state;
	}

	public Event process(RawDocument input) {		
		return process(new Event(EventType.RAW_DOCUMENT, input));		
	}

	public Event process(Event input) {
		state = PipelineReturnValue.RUNNING;
		initialize();

		// Pre-process for this batch-item
		Event e = new Event(EventType.START_BATCH_ITEM);
		for (IPipelineStep step : steps) {
			step.handleEvent(e);
		}
		notifyObservers(e);

		// Prime the pipeline with the input Event and run it to completion.
		Event finalEvent = execute(input);
		
		// Copy any remaining steps into finishedSteps - makes initialization
		// process easier down the road if we use the pipeline again
		for (IPipelineStep step : steps) {
			finishedSteps.add(step);
		}
		steps.clear();

		// Post-process for this batch-item
		e = new Event(EventType.END_BATCH_ITEM);
		for (IPipelineStep step : finishedSteps) {
			step.handleEvent(e);
		}		
		notifyObservers(e);
		
		return finalEvent;
	}

	public void destroy() {
		for (IPipelineStep step : finishedSteps) {
			step.destroy();
		}
		state = PipelineReturnValue.DESTROYED;
	}

	@Deprecated
	public int inputCountRequested() {
		int max = 0;
		for (IPipelineStep step : steps) {
			if (step.inputCountRequested() > max) {
				max = step.inputCountRequested();
			}
		}
		return max;
	}

	@Deprecated
	public boolean needsOutput(int inputIndex) {
		for (IPipelineStep step : steps) {
			if (step.needsOutput(inputIndex)) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public IContext getContext() {
		return context;
	}

	@Deprecated
	public void setContext(IContext context) {
		this.context = context;
	}

	public void clearSteps() {
		destroy();
		steps.clear();
		finishedSteps.clear();
	}
	
	//
	// implements IObserver interface
	//

	public void update(IObservable o, Object arg) {
		notifyObservers();
	}
	
	//
	// implements IObservable interface
	//
	
	/**
	 * Implements multiple inheritance via delegate pattern to an inner class
	 * 
	 * @see IObservable
	 * @see BaseObservable
	 */
	private IObservable delegatedObservable = new BaseObservable(this);

	public void addObserver(IObserver observer) {
		delegatedObservable.addObserver(observer);
	}

	public int countObservers() {
		return delegatedObservable.countObservers();
	}

	public void deleteObserver(IObserver observer) {
		delegatedObservable.deleteObserver(observer);
	}

	public void notifyObservers() {
		delegatedObservable.notifyObservers();
	}

	public void notifyObservers(Object arg) {
		delegatedObservable.notifyObservers(arg);
	}

	public void deleteObservers() {
		delegatedObservable.deleteObservers();
	}

	public List<IObserver> getObservers() {
		return delegatedObservable.getObservers();
	}
}
