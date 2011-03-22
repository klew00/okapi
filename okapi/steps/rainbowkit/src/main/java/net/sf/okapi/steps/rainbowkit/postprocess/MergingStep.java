/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.rainbowkit.postprocess;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;

public class MergingStep extends BasePipelineStep {

	private MergingInfo info;
	private Merger merger;
	IFilterConfigurationMapper fcMapper;

	public MergingStep () {
		super();
	}

	@Override
	public String getDescription () {
		return "BETA --- Post-process a Rainbow translation kit."
			+" Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "Rainbow Translation Kit Merging";
	}

	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			return handleStartDocument(event);
		case END_BATCH:
			return handleEndBatch(event);
		default:
			if ( merger != null ) {
				merger.handleEvent(event);
			}
			return event;
		}
	}

	@Override
	protected Event handleStartBatch (Event event) {
		return event;
	}

	@Override
	protected Event handleEndBatch (Event event) {
		merger.close();
		return event;
	}
	
	@Override
	protected Event handleStartDocument (Event event) {
		// Initial document is expected to be a manifest
		StartDocument sd = event.getStartDocument();
		info = sd.getAnnotation(MergingInfo.class);
		if ( info == null ) {
			throw new OkapiBadFilterInputException("Start document is missing the merging info annotation.");
		}
		Manifest manifest = sd.getAnnotation(Manifest.class);
		if ( manifest == null ) {
			throw new OkapiBadFilterInputException("Start document is missing the manifest annotation.");
		}
		
		// Create the merger if needed
		if ( merger == null ) {
			merger = new Merger(manifest, fcMapper);
		}
		// And trigger the merging
		merger.startMerging(info);
		
		return event;
	}

}
