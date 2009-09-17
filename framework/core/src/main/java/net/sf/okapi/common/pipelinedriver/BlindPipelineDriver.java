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

package net.sf.okapi.common.pipelinedriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.annotations.ConfigurationParameter;
import net.sf.okapi.common.pipeline.annotations.StepIntrospector;
import net.sf.okapi.common.resource.RawDocument;

public class BlindPipelineDriver {

	private Pipeline pipeline;
	private IPipelineStep lastOutputStep;
	private LinkedList<List<ConfigurationParameter>> paramList;
	private IFilterConfigurationMapper fcMapper;

	public BlindPipelineDriver() {
		pipeline = new Pipeline();
		paramList = new LinkedList<List<ConfigurationParameter>>();
	}

	public void setContext(IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	public void addStep (IPipelineStep step) {
		pipeline.addStep(step);
		List<ConfigurationParameter> pList = StepIntrospector.getStepParameters(step);
		paramList.add(pList);

		for ( ConfigurationParameter p : pList ) {
			String methodName = p.getMethod().getName();
			if (( methodName != null ) && (methodName).equals("setOutputURI") ) {
				if ( lastOutputStep != null ) {
					lastOutputStep.setLastStep(false);
				}
				lastOutputStep = step;
				lastOutputStep.setLastStep(true);
			}
		}
	}

	public void execute (RawDocument input, URI outputURI) {
		try {
			// Set the runtime parameters using the method annotations
			// For each step
			for (List<ConfigurationParameter> pList : paramList) {
				// For each parameter exposed
				for (ConfigurationParameter p : pList) {
					Method method = p.getMethod();
					if ( method == null ) continue;
					switch ( p.getParameterType() ) {
					case OUTPUT_URI:
						if ( lastOutputStep == p.getStep() ) {
							method.invoke(p.getStep(), outputURI);
						}
						break;
					case SOURCE_LANGUAGE:
						method.invoke(p.getStep(), input.getSourceLanguage());
						break;
					case TARGET_LANGUAGE:
						method.invoke(p.getStep(), input.getTargetLanguage());
						break;
					case FILTER_CONFIGURATION_ID:
						method.invoke(p.getStep(), input.getFilterConfigId());
						break;
					case FILTER_CONFIGURATION_MAPPER:
						method.invoke(p.getStep(), fcMapper);
						break;
					}
				}
			}
			
			// execute the pipeline
			pipeline.process(input);
		}
		catch ( IllegalArgumentException e ) {
			throw new RuntimeException(e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		}
		catch ( InvocationTargetException e ) {
			throw new RuntimeException(e);
		}
	}

}
