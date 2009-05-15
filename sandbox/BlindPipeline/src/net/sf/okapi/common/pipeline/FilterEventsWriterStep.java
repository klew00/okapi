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

package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.StartDocument;

/**
 * Outputs filters events into a document.
 * This class implements the {@link IPipelineStep} interface for a step that takes 
 * filter events and creates an output document using a provided {@link IFilterWriter} 
 * implementation. Each event and its resource are passed on to the next step.
 * @see RawDocumentToFilterEventsStep
 * @see FilterEventsToRawDocumentStep 
 */
public class FilterEventsWriterStep extends BasePipelineStep {

	private IFilterWriter filterWriter;

	/**
	 * Creates a new EventsWriterStep object.
	 * This constructor is needed to be able to instantiate an object from newInstance()
	 */
	public FilterEventsWriterStep () {
	}
	
	public FilterEventsWriterStep(IFilterWriter filterWriter) {
		this.filterWriter = filterWriter;
	}

	public IFilterWriter getFilterWriter() {
		return filterWriter;
	}
	
	/**
	 * Sets the filter writer for this EventsWriterStep object.
	 * @param filterWriter the filter writer to use.
	 */
	public void setFilterWriter (IFilterWriter filterWriter) {
		this.filterWriter = filterWriter;
	}

	public String getName() {
		return filterWriter.getName();
	}

	public String getDescription () {
		return "Writes out filter events into a document.";
	}

	public boolean needsOutput (int inputIndex) {
		return (inputIndex == 0);
	}
	
	@Override
	public Event handleEvent(Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			StartDocument sd = (StartDocument)event.getResource();
			if ( sd.getFilter() != null ) {
				filterWriter = sd.getFilter().createFilterWriter();
				filterWriter.setOptions(getContext().getTargetLanguage(), getContext().getOutputEncoding(0));
				filterWriter.setParameters(sd.getFilterParameters());
				filterWriter.setOutput(getContext().getOutputPath(0));
			}
			// Fall thru
		case START_SUBDOCUMENT:
		case END_SUBDOCUMENT:
		case START_GROUP:
		case END_GROUP:
		case TEXT_UNIT:
		case DOCUMENT_PART:
		case END_DOCUMENT:
			return filterWriter.handleEvent(event);
		default:
			return event;
		}
	}	
	
	@Override
	public void destroy() {
		filterWriter.close();
	}

	public boolean hasNext() {		
		return false;
	}	
}
