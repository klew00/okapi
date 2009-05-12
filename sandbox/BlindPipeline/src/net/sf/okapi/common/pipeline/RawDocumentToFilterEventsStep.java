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

import java.util.List;

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
		return "Filters a RawDocument into filter events.";
	}
	
	@Override
	public void preprocess (List<DocumentData> inputs) {
		super.preprocess(inputs);
		if ( inputs.get(0).filterConfig != null ) {
			//TODO: This is where the filter+config lookup object would be used
			if ( inputs.get(0).filterConfig.equals("okf_properties") ) {
				filter = new net.sf.okapi.filters.properties.PropertiesFilter();
			}
			else {
				throw new RuntimeException("Unsupported filter type.");
			}
			hasEvents = true;
		}
		else { // No filter configuration provided: just pass it down
			hasEvents = false;
		}
	}
	
	@Override
	public Event handleEvent (Event event) {
		if (( event != null ) && ( event.getEventType() == EventType.RAW_DOCUMENT )) {
			if ( filter == null ) { // No filter assigned
				return event;
			}
			filter.open((RawDocument)event.getResource());
		}
		Event e = filter.next();
		if (e != null && e.getEventType() == EventType.END_DOCUMENT) {
			hasEvents = false;
		}
		
		return e;		
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
