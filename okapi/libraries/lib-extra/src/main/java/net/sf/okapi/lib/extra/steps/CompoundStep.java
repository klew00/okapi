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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.annotations.ConfigurationParameter;
import net.sf.okapi.common.pipeline.annotations.StepIntrospector;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipelineStep;

public abstract class CompoundStep extends AbstractPipelineStep {

	protected LinkedList<IPipelineStep> steps = new LinkedList<IPipelineStep>();
	private LinkedList<List<ConfigurationParameter>> paramList;

	protected abstract void addSteps(List<IPipelineStep> list);
	
	public CompoundStep() {
		super();
		paramList = new LinkedList<List<ConfigurationParameter>>();
		addSteps(steps);
		for (IPipelineStep step : steps) {
			List<ConfigurationParameter> pList = null;
			if (step instanceof XPipelineStep)
				pList = StepIntrospector.getStepParameters(((XPipelineStep)step).getStep());
			else
				pList = StepIntrospector.getStepParameters(step);
			paramList.add(pList);
		}		
	}

	@Override
	protected void component_init() {
		// Stub not to implement in subclasses as would've been required otherwise
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
	
	private void invokeParameterMethods(StepParameterType type, Object value) {
		for ( List<ConfigurationParameter> pList : paramList ) {
			// For each exposed parameter
			for ( ConfigurationParameter p : pList ) {
				Method method = p.getMethod();
				if ( method == null ) continue;
				if ( p.getParameterType() == type) {
					try {
						method.invoke(p.getStep(), value);
					} 
					catch ( IllegalArgumentException e ) {
						throw new RuntimeException("Error when assigning runtime parameters.", e);
					}
					catch ( IllegalAccessException e ) {
						throw new RuntimeException("Error when assigning runtime parameters.", e);
					}
					catch ( InvocationTargetException e ) {
						throw new RuntimeException("Error when assigning runtime parameters.", e);
					}
				}
			}
		}
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	@Override
	public void setTargetLocale (LocaleId targetLocale) {
		super.setTargetLocale(targetLocale);
		invokeParameterMethods(StepParameterType.TARGET_LOCALE, targetLocale);		
	}
}
