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
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.filters.rainbowkit.RainbowKitFilter;

@UsingParameters() // No parameters
public class RainbowKitReaderStep extends BasePipelineStep {

	private IFilter filter;
	private boolean isDone;
//	private IFilterConfigurationMapper fcMapper;

	public RainbowKitReaderStep () {
	}
	
//	@StepParameterMapping (parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
//	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
//		this.fcMapper = fcMapper;
//	}

	@Override
	public String getName () {
		return "Simple Kit Reader";
	}

	@Override
	public String getDescription () {
		return "Convert a simple kit into filter events."
			+ " Expects: raw document. Sends back: filter events.";
	}

	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_BATCH:
			isDone = true;
			break;
		
		case START_BATCH_ITEM:
			// Needed because the process() method of the pipeline expects
			// hasEvents to be set to true to prime things.
			isDone = false;
			return event;

		case RAW_DOCUMENT:
			filter = new RainbowKitFilter();
			filter.open(event.getRawDocument());
			// Return the first event from the filter
			isDone = false;
			return filter.next();
		}

		if ( isDone ) {
			return event;
		}
		else {
			// Get events from the filter
			Event e = event;
			if ( filter != null ) {
				if ( filter.hasNext() ) {
					e = filter.next();
				}
				else { // We are done
					e = Event.NOOP_EVENT;
					isDone = true;
				}
			}
			return e;
		}
	}

	@Override
	public boolean isDone () {
		return isDone;
	}

	@Override
	public void destroy () {
		filter.close();
	}

	@Override
	public void cancel () {
		filter.cancel ();
	}

}
