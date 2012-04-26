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

package net.sf.okapi.steps.batchtranslation;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.PipelineParameters;
import net.sf.okapi.common.resource.RawDocument;

@UsingParameters(Parameters.class)
public class BatchTranslationStep extends BasePipelineStep {

	private Parameters params;
	private BatchTranslator trans;
	private IFilterConfigurationMapper fcMapper;
	private String rootDir;
	private int batchInputCount;
	private int itemCount;
	private boolean sendTMX;

	public BatchTranslationStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.BATCH_INPUT_COUNT)
	public void setBatchInputCount (int batchInputCount) {
		this.batchInputCount = batchInputCount;
	}
	
	public String getName () {
		return "Batch Translation";
	}

	public String getDescription () {
		return "Creates translations from an external program for a given input document."
			+ " Expects: raw document. Sends back: raw document.";
	}
	
	@Override
	protected Event handleStartBatch (Event event) {
		sendTMX = params.getMakeTMX() && params.getSendTMX();
		itemCount = 0;
		trans = new BatchTranslator(fcMapper, params, rootDir);
		return event;
	}
	
	@Override
	protected Event handleStartBatchItem (Event event) {
		if ( sendTMX ) return Event.NOOP_EVENT;
		else return event;
	}

	@Override
	protected Event handleEndBatchItem (Event event) {
		if ( sendTMX ) return Event.NOOP_EVENT;
		else return event;
	}

	@Override
	protected Event handleRawDocument (Event event) {
		// Process this document
		trans.processDocument((RawDocument)event.getResource());
		
		// If this is the last document: execute the final process
		itemCount++;
		if ( itemCount >= batchInputCount ) {
			if ( sendTMX ) {
				return trans.endBatch();
			}
			else { // Don't use the multi-events, just send the input event
				trans.endBatch();
			}
		}
		
		// Else: return the event
		if ( sendTMX ) return Event.NOOP_EVENT;
		else return event;
	}

	@Override
	public IParameters getParameters () {
		return params;
	}
	
	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

}
