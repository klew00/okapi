/*===========================================================================
Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.pipeline.annotations;

import java.lang.reflect.Method;

import net.sf.okapi.common.pipeline.IPipelineStep;

/**
 * Java annotation item for runtime configuration parameters used in {@link IPipelineStep}.
 */
public class ConfigurationParameter {
	private Method method;
	private StepParameterType parameterType;
	private IPipelineStep step;

	/**
	 * Creates a new ConfigurationParameter object.
	 * @param method method for this parameter.
	 * @param parameterType type of parameter.
	 * @param step step where the parameter is set.
	 */
	ConfigurationParameter (Method method,
		StepParameterType parameterType,
		IPipelineStep step)
	{
		this.setMethod(method);
		this.setParameterType(parameterType);
		this.setStep(step);
	}

	/**
	 * Sets the method for this parameter.
	 * @param method the method for this parameter.
	 */
	public void setMethod (Method method) {
		this.method = method;
	}

	/**
	 * Gets the method for this parameter.
	 * @return the method for this parameter.
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Sets the type of parameter for this parameter.
	 * @param parameterType the type of parameter for this parameter.
	 */
	public void setParameterType (StepParameterType parameterType) {
		this.parameterType = parameterType;
	}

	/**
	 * Gets the type of parameter for this parameter.
	 * @return the type of parameter for this parameter.
	 */
	public StepParameterType getParameterType () {
		return parameterType;
	}

	/**
	 * Sets the step for this parameter.
	 * @param step the step for this parameter.
	 */
	public void setStep (IPipelineStep step) {
		this.step = step;
	}

	/**
	 * Gets the step for this parameter.
	 * @return the step for this parameter.
	 */
	public IPipelineStep getStep() {
		return step;
	}
}
