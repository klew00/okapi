/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.lib.translation.QueryManager;
import net.sf.okapi.lib.translation.ResourceItem;

@UsingParameters(Parameters.class)
public class LeveragingStep extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private Parameters params;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private QueryManager qm;
	private TMXWriter tmxWriter;
	private String rootDir;
	private String inputRootDir;
	private boolean initDone;
	private int totalCount;
	private int exactCount;
	private int fuzzyCount;
	private int iQueryId;
	private ClassLoader connectorContext;

	public LeveragingStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.INPUT_ROOT_DIRECTORY)
	public void setInputRootDirectory (String inputRootDir) {
		this.inputRootDir = inputRootDir;
	}

	public String getName () {
		return "Leveraging";
	}

	public String getDescription () {
		return "Leverage existing translation into the text units content of a document."
			+ " Expects: filter events. Sends back: filter events.";
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
	protected Event handleStartBatch (Event event) {
		totalCount = 0;
		exactCount = 0;
		fuzzyCount = 0;
		initDone = false;
		return event;
	}
	
	@Override
	protected Event handleEndBatch (Event event) {
		if ( !params.getLeverage() ) return event;
		logger.info(String.format("\nTotals:\nProcesseed segments = %d", totalCount));
		logger.info(String.format("Best matches that are exact = %d", exactCount));
		logger.info(String.format("Best matches that are fuzzy = %d", fuzzyCount));
		destroy();
		return event;
	}
	
	@Override
	protected Event handleStartDocument (Event event) {
		if ( !params.getLeverage() ) return event;
		if ( !initDone ) {			
			initialize();
		}
		qm.setLanguages(sourceLocale, targetLocale);
		qm.resetCounters();
		return event;
	}

	@Override
	protected Event handleEndDocument (Event event) {
		if ( !params.getLeverage() ) return event;
		totalCount += qm.getTotalSegments();
		exactCount += qm.getExactBestMatches();
		fuzzyCount += qm.getFuzzyBestMatches();
		logger.info(String.format("Processeed segments = %d", qm.getTotalSegments()));
		logger.info(String.format("Best matches that are exact = %d", qm.getExactBestMatches()));
		logger.info(String.format("Best matches that are fuzzy = %d", qm.getFuzzyBestMatches()));
		return event;
	}

	@Override
	protected Event handleTextUnit (Event event) {
		if ( !params.getLeverage() ) return event;
		ITextUnit tu = event.getTextUnit();

		// Do not leverage non-translatable entries
		if ( !tu.isTranslatable() ) return event;

    	boolean approved = false;
    	Property prop = tu.getTargetProperty(targetLocale, Property.APPROVED);
    	if ( prop != null ) {
    		if ( "yes".equals(prop.getValue()) ) approved = true;
    	}
    	// Do not leverage pre-approved entries
    	if ( approved ) return event;
    	
    	// Leverage
    	qm.leverage(tu);
    	
    	// Optionally write out this TU
		if ( tmxWriter != null ) {
			tmxWriter.writeAlternates(tu, targetLocale);
		}
		
		return event;
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

	private void initialize () {
		// If we don't really use this step, just move on
		if ( !params.getLeverage() ) {
			initDone = true;
			return;
		}
		
		// Else: initialize the global variables
		qm = new QueryManager();
		qm.setNoQueryThreshold(params.getNoQueryThreshold());
		qm.setThreshold(params.getThreshold());
		qm.setRootDirectory(rootDir);
		
		if (connectorContext == null || connectorContext == Thread.currentThread().getContextClassLoader()) {
			iQueryId = qm.addAndInitializeResource(params.getResourceClassName(), null,
					params.getResourceParameters());
		} else {
			iQueryId = qm.addAndInitializeResource(params.getResourceClassName(), null, connectorContext,
					params.getResourceParameters());
		}
		
		ResourceItem res = qm.getResource(iQueryId);
		logger.info("Leveraging settings: "+res.name);
		logger.info(res.query.getSettingsDisplay());

		// Options
		String targetPrefix = (params.getUseTargetPrefix() ? params.getTargetPrefix() : null);
		qm.setOptions(params.getFillTarget() ? params.getFillTargetThreshold() : Integer.MAX_VALUE,
			true, true, params.getDowngradeIdenticalBestMatches(),
			targetPrefix, params.getTargetPrefixThreshold(), params.getCopySourceOnNoText());
			
		if ( params.getMakeTMX() ) {
			// Resolve the variables
			String realPath = Util.fillRootDirectoryVariable(params.getTMXPath(), rootDir);
			realPath = Util.fillInputRootDirectoryVariable(realPath, inputRootDir);
			realPath = LocaleId.replaceVariables(realPath, sourceLocale, targetLocale);
			// Create the output
			tmxWriter = new TMXWriter(realPath);
			tmxWriter.setUseMTPrefix(params.getUseMTPrefix());
			tmxWriter.writeStartDocument(sourceLocale, targetLocale,
				getClass().getName(), "1", // Version is irrelevant here
				"sentence", "undefined", "undefined");
		}
		initDone = true;
	}
	
	public void setConnectorContext(ClassLoader connectorContext) {
		this.connectorContext = connectorContext;
	}
}
