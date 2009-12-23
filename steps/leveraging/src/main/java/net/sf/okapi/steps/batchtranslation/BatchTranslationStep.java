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
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;

public class BatchTranslationStep extends BasePipelineStep {

	private Parameters params;
	private boolean isDone;
	private BatchTranslator trans;
	private IFilterConfigurationMapper fcMapper;

	public BatchTranslationStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	public String getName () {
		return "Batch Translation";
	}

	public String getDescription () {
		return "Creates translations from an external program for a given input document.";
	}
	
	@Override
	public boolean isDone () {
		return isDone;
	}

	@Override
	protected void handleStartBatch (Event event) {
		isDone = true;
		trans = new BatchTranslator(fcMapper, params);
	}
	
	@Override
	protected void handleEndBatch (Event event) {
		if ( trans != null ) {
			trans.endBatch();
		}
	}
	
	@Override
	protected void handleStartBatchItem (Event event) {
		// To get the raw document
		isDone = false;
	}

	@Override
	protected void handleRawDocument (Event event) {
		trans.processDocument((RawDocument)event.getResource());
		// Can move on to the next step
		isDone = true;
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
