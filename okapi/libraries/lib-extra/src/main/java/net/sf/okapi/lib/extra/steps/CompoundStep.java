/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.extra.steps;

import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.MultiEvent;

public abstract class CompoundStep extends AbstractPipelineStep {

	protected LinkedList<IPipelineStep> steps = new LinkedList<IPipelineStep>();

	protected abstract void addSteps(LinkedList<IPipelineStep> list);
	
	public CompoundStep() {
		super();
		addSteps(steps);
	}

	@Override
	protected void component_init() {
	}

	private Event expandEvent(Event event, IPipelineStep currentStep) {
		if (event.getEventType() == EventType.MULTI_EVENT
				&& !(((MultiEvent) event.getResource()).isPropagateAsSingleEvent())) {

			// add the remaining steps to a temp list - these are the steps that will receive the expanded
			// MULT_EVENTS
			List<IPipelineStep> remainingSteps = steps.subList(steps.indexOf(currentStep) + 1,
					steps.size());
			for (Event me : ((MultiEvent)event.getResource())) {
				event = me;
				// send the current event from MULTI_EVENT down the remaining steps in the pipeline
				for (IPipelineStep remainingStep : remainingSteps) {
					event = remainingStep.handleEvent(event);
					event = expandEvent(event, remainingStep);
				}					
			}
		}
		
		return event;
	}
	
	@Override
	public Event handleEvent(Event event) {		
		for (IPipelineStep step : steps) {
			event = step.handleEvent(event);
			// Recursively expand the event if needed
			event = expandEvent(event, step);
		}
		
		return super.handleEvent(event);
	}
}
