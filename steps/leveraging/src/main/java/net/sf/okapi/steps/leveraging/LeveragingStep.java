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

package net.sf.okapi.steps.leveraging;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryManager;

public class LeveragingStep extends BasePipelineStep {

	private Parameters params;
	private String sourceLanguage;
	private String targetLanguage;
	private QueryManager qm;

	public LeveragingStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LANGUAGE)
	public void setSourceLanguage (String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LANGUAGE)
	public void setTargetLanguage (String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}
	
	public String getName () {
		return "Leveraging";
	}

	public String getDescription () {
		return "Leverage existing translation into the text units content of a document.";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	protected void handleStartBatch (Event event) {
		try {
			qm = new QueryManager();
			qm.setLanguages(sourceLanguage, targetLanguage);
			qm.setThreshold(params.getThreshold());
			
			IQuery connector = (IQuery)Class.forName(params.getResourceClassName()).newInstance();
			IParameters tmp = connector.getParameters();
			if ( tmp != null ) {
				tmp.fromString(params.getResourceParameters());
			}
			qm.addAndInitializeResource(connector, connector.getName(), tmp);
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException("Error creating a connector.", e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException("Error creating a connector.", e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException("Error creating a connector.", e);
		}
	}
	
	@Override
	protected void handleEndBatch (Event event) {
		destroy();
	}
	
	@Override
	protected void handleTextUnit (Event event) {
		TextUnit tu = (TextUnit)event.getResource();
		if ( !tu.isTranslatable() ) return;

    	boolean approved = false;
    	Property prop = tu.getTargetProperty(targetLanguage, Property.APPROVED);
    	if ( prop != null ) {
    		if ( "yes".equals(prop.getValue()) ) approved = true;
    	}
    	if ( approved ) return; // Do not translate pre-approved entries

    	// Leverage
		qm.leverage(tu);
	}

	@Override
	public void destroy () {
		if ( qm != null ) {
			qm.close();
			qm = null;
		}
	}

}
