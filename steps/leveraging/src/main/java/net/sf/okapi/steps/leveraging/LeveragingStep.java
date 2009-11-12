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

import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.translation.QueryManager;
import net.sf.okapi.lib.translation.ResourceItem;

public class LeveragingStep extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private Parameters params;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private QueryManager qm;
	private TMXWriter tmxWriter;

	public LeveragingStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setsourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
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
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@Override
	protected void handleStartBatch (Event event) {
		qm = new QueryManager();
		qm.setLanguages(sourceLocale, targetLocale);
		qm.setThreshold(params.getThreshold());
		int id = qm.addAndInitializeResource(params.getResourceClassName(), null,
			params.getResourceParameters());
		ResourceItem res = qm.getResource(id);
		logger.info("Leveraging settings: "+res.name);
		logger.info(res.query.getSettingsDisplay());
			
		if ( params.getMakeTMX() ) {
			tmxWriter = new TMXWriter(params.getTMXPath());
			tmxWriter.writeStartDocument(sourceLocale, targetLocale,
				getClass().getName(), "", "sentence", "undefined", "undefined");
		}
	}
	
	@Override
	protected void handleEndBatch (Event event) {
		destroy();
	}
	
	@Override
	protected void handleStartDocument (Event event) {
		qm.resetCounters();
	}

	@Override
	protected void handleEndDocument (Event event) {
		logger.info(String.format("Segments with text = %d", qm.getTotalSegments()));
		logger.info(String.format("Segments leveraged = %d", qm.getLeveragedSegments()));
	}
	
	@Override
	protected void handleTextUnit (Event event) {
		TextUnit tu = (TextUnit)event.getResource();
		if ( !tu.isTranslatable() ) return;

    	boolean approved = false;
    	Property prop = tu.getTargetProperty(targetLocale, Property.APPROVED);
    	if ( prop != null ) {
    		if ( "yes".equals(prop.getValue()) ) approved = true;
    	}
    	if ( approved ) return; // Do not leverage pre-approved entries

    	// Leverage
		qm.leverage(tu, tmxWriter, params.getFillTarget());
		
	}

	@Override
	public void destroy () {
		if ( qm != null ) {
			qm.close();
			qm = null;
		}
		if ( tmxWriter != null ) {
			tmxWriter.writeEndDocument();
			tmxWriter.close();
			tmxWriter = null;
		}
	}

}
