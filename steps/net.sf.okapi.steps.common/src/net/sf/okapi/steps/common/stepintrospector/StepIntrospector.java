/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.common.stepintrospector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.pipeline.IPipelineStep;

public final class StepIntrospector {

	public static HashMap<String, StepParameter> getStepParameters(IPipelineStep step) {
		HashMap<String, StepParameter> parameters = new HashMap<String, StepParameter>();

		// get all the declared fields (skipping any inherited ones) of the
		// object including protected and private
		Field[] fields = step.getClass().getDeclaredFields();

		// iterate over the fields and pull out the ones that have
		// StepConfigurationParameter or StepExternalParameter annotations
		for (Field f : fields) {
			// if not set to true we cannot access private or protected fields
			f.setAccessible(true);
			if (f.isAnnotationPresent(StepExternalParameter.class)
					|| f.isAnnotationPresent(StepConfigurationParameter.class)) {
				// a step parameter should only have one of these annotations,
				// if it has more than one throw an exception
				if (f.isAnnotationPresent(StepExternalParameter.class)
						&& f.isAnnotationPresent(StepConfigurationParameter.class)) {
					throw new OkapiBadStepInputException(
							"A step parameter cannot have both StepExternalParameter and StepConfigurationParameter annotations");
				}
				try {
					if (f.get(step) == null) {
						throw new OkapiBadStepInputException("A step parameter must have a default value");
					}

					StepParameter p = new StepParameter(f.getName(), f.get(step));
					if (f.isAnnotationPresent(StepExternalParameter.class)) {
						StepExternalParameter ep = f.getAnnotation(StepExternalParameter.class);
						p.setDescription(ep.description());
						p.setLongDescription(ep.longDescription());
						p.setRequiredStep(ep.requiredStep());
						p.setType(StepParameterType.EXTERNAL);
						p.setAccessType(ep.accessType());
					} else if (f.isAnnotationPresent(StepConfigurationParameter.class)) {
						StepConfigurationParameter scp = f.getAnnotation(StepConfigurationParameter.class);
						p.setDescription(scp.description());
						p.setLongDescription(scp.longDescription());
						p.setType(StepParameterType.STEP_CONFIGURATION);
						p.setAccessType(StepParameterAccessType.INPUT_ONLY);
					}
					parameters.put(p.getName(), p);
				} catch (IllegalArgumentException e) {
					throw new OkapiBadStepInputException("Step parameter does not exist: " + f.getName(), e);
				} catch (IllegalAccessException e) {
					throw new OkapiBadStepInputException("Error accessing step parameter: " + f.getName(), e);
				}
			}
		}
				
		return parameters;
	}

	public static List<String> getStepEventHandlers(IPipelineStep step) {
		List<String> eventsHandled = new LinkedList<String>();
		Method[] methods = step.getClass().getDeclaredMethods();
		for (Method m : methods) {
			// only look at methods with a single Event 
			// parameter and return type
			if (m.getName().startsWith("handle")
					&& m.getParameterTypes().length == 1
					&& Arrays.asList(m.getParameterTypes()).contains(Event.class)
					&& m.getReturnType() == void.class) {
				eventsHandled.add(m.getName());
			}
		}

		if (eventsHandled.size() <= 0) {
			throw new OkapiBadStepInputException("Steps must implement at least one handler method");
		}

		return eventsHandled;
	}
}
