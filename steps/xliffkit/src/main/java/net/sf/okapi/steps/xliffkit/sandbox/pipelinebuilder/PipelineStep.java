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

package net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.pipeline.IPipelineStep;

public class PipelineStep implements IPipelineStep{

	private IPipelineStep step;
	private ParametersString parametersString = new ParametersString(); 
	
	public PipelineStep(IPipelineStep step, IParameters parameters) {	
		this.step = step;
		step.setParameters(parameters);
	}

	public PipelineStep(IPipelineStep step, Parameter... parameters) {	
		this.step = step;
		
		parametersString.reset();
		for (Parameter parameter : parameters) {
			
			Object value = parameter.getValue();
			
			if (value instanceof Integer)
				parametersString.setParameter(parameter.getName(), Integer.class.cast(value));
			
			else if (value instanceof Boolean)
				parametersString.setParameter(parameter.getName(), Boolean.class.cast(value));
			
			else if (value instanceof String)
				parametersString.setParameter(parameter.getName(), String.class.cast(value));
		}
		IParameters params = step.getParameters();
		params.fromString(parametersString.toString());
	}
	
	public PipelineStep(Class<? extends IPipelineStep> stepClass, IParameters parameters) {		
		step = instantiateStep(stepClass);
		step.setParameters(parameters);
	}
	
	public PipelineStep(Class<? extends IPipelineStep> stepClass, Parameter... parameters) {		
		this(instantiateStep(stepClass), parameters);
	}
	
	private static IPipelineStep instantiateStep(Class<? extends IPipelineStep> stepClass) {
		IPipelineStep res = null;
		
		try {
			res = ClassUtil.instantiateClass(stepClass);
			
		} catch (InstantiationException e) {
			// TODO Handle exception

		} catch (IllegalAccessException e) {			
			// TODO Handle exception
		}	
		return res;
	}

	public PipelineStep(IPipelineStep step, URI parametersURI, boolean ignoreErrors) {		
		this.step = step;
		IParameters params = step.getParameters();
		params.load(parametersURI, ignoreErrors);
	}
	
	public PipelineStep(Class<? extends IPipelineStep> stepClass, URI parametersURI, boolean ignoreErrors) {	
		this.step = instantiateStep(stepClass);
		IParameters params = step.getParameters();
		params.load(parametersURI, ignoreErrors);			
	}
	
	public PipelineStep(Class<? extends IPipelineStep> stepClass, URL parametersURL, boolean ignoreErrors){		
		try {
			URI parametersURI = parametersURL.toURI();
			step = instantiateStep(stepClass);
			IParameters params = step.getParameters();
			params.load(parametersURI, ignoreErrors);
			
		} catch (URISyntaxException e) {
			// TODO Handle exception
		}				
	}

	public String getDescription() {
		return step.getDescription();
	}

	public String getName() {
		return step.getName();
	}

	public void destroy() {
		step.destroy();
	}

	public String getHelpLocation() {
		return step.getHelpLocation();
	}

	public IParameters getParameters() {
		return step.getParameters();
	}

	public Event handleEvent(Event event) {
		return step.handleEvent(event);
	}

	public boolean isDone() {
		return step.isDone();
	}

	public boolean isLastOutputStep() {	
		return step.isLastOutputStep();
	}

	public void setLastOutputStep(boolean isLastStep) {		
		step.setLastOutputStep(isLastStep);
	}

	public void setParameters(IParameters params) {		
		step.setParameters(params);
	}

	public IPipelineStep getStep() {
		return step;
	}
	
}
