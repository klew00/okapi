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

package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Converts a {@link RawDocument} into filter events. This class implements the {@link IPipelineStep}
 * interface for a step that takes a {@link RawDocument} and uses a provided {@link IFilter} 
 * implementation to generate its corresponding events.
 * @see FilterEventsToRawDocumentStep
 * @see FilterEventsWriterStep 
 */
public class RawDocumentToFilterEventsStep extends BasePipelineStep {
	
	private IFilter filter;
	private boolean hasEvents;

	/**
	 * Creates a new RawDocumentToEventsStep object.
	 * This constructor is needed to be able to instantiate an object from newInstance()
	 */
	public RawDocumentToFilterEventsStep () {
	}
	
	public RawDocumentToFilterEventsStep(IFilter filter) {
		this.filter = filter;
	}

	public IFilter getFilter() {
		return filter;
	}

	/**
	 * Sets the filter for this RawDocumentToEventsStep object.
	 * @param filter the filter to use.
	 */
	public void setFilter (IFilter filter) {
		this.filter = filter;
	}

	public String getName() {
		return filter.getName();
	}
	
	public String getDescription () {
		return "Converts a RawDocument into filter events.";
	}
	
	@Override
	public Event handleEvent (Event event) {
		if ( event.getEventType() == EventType.START_BATCH_ITEM ) {
			// Needed because the process() method of the pipeline expects
			// hasEvents to be set to true to prime things.
			hasEvents = true;
			return event;
		}
		
		// Initialize the filter on RAW_DOCUMENT
		if ( event.getEventType() == EventType.RAW_DOCUMENT ) {
			if ( getContext().getFilterConfiguration(0) != null ) {
				// Get the filter to use
				//TODO: This is where the filter+config lookup object would be used
				if ( getContext().getFilterConfiguration(0).equals("okf_properties") ) {
					filter = new net.sf.okapi.filters.properties.PropertiesFilter();
				}
				else if ( getContext().getFilterConfiguration(0).equals("okf_xml") ) {
					filter = new net.sf.okapi.filters.xml.XMLFilter();
				}
				else {
					throw new RuntimeException("Unsupported filter type.");
				}
				hasEvents = true;
			}
			else { // No filter configuration provided: just pass it down
				hasEvents = false;
				return event;
			}
			// Open the document
			filter.open((RawDocument)event.getResource());
			return event;
		}

		if ( hasEvents ) {
			// Get events from the filter
			Event e = filter.next();
			if ( e.getEventType() == EventType.END_DOCUMENT) {
				// END_DOCUMENT is the end of this raw document
				hasEvents = false;
			}
			return e;
		}
		else {
			// In all other cases: just pass the event through
			return event;
		}
	}

	@Override
	public boolean hasNext() {
		return hasEvents;
	}

	public void destroy() {
		filter.close();
	}

	public void cancel() {
		filter.cancel();
	}
	
}
