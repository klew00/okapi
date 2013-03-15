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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities;

import java.io.File;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.resource.RawDocument;

public abstract class BaseFilterDrivenUtility extends BaseUtility
	implements IFilterDrivenUtility {
	
	protected IFilter filter = null;
	protected IFilterWriter filterWriter = null;
	protected boolean needsSelfOutput = true;

	public void processFilterInput () {
		try {
			filterWriter = null;

			// If there is no associated filter: send the file as a FILE_RESOURCE
			if (( getInputFilterSettings(0) == null ) || ( getInputFilterSettings(0).length() == 0 ) ) {
				RawDocument fr = new RawDocument(new File(getInputPath(0)).toURI(),
					getInputEncoding(0), srcLang);
				handleEvent(new Event(EventType.RAW_DOCUMENT, fr));
				return;
			}
			
			// Process as a filter-drive input
			// Load the filter if needed
			filter = mapper.createFilter(getInputFilterSettings(0), filter);
	
			// Setup the filter
			File f = new File(getInputPath(0));
			RawDocument res = new RawDocument(f.toURI(), getInputEncoding(0), srcLang, trgLang);
			filter.open(res);

			// Create the filter writer if required
			if ( needsSelfOutput ) {
				filterWriter = filter.createFilterWriter();
				filterWriter.setOptions(trgLang, getOutputEncoding(0));
				filterWriter.setOutput(getOutputPath(0));
			}

			// Process the document
			Event event;
			while ( filter.hasNext() ) {
				event = filter.next();
				handleEvent(event);
				if ( filterWriter != null ) { // Only if needed
					filterWriter.handleEvent(event);
				}
			}
		}
		finally {
			if ( filter != null ) filter.close();
			if ( filterWriter != null ) filterWriter.close();
		}
	}

	public boolean isDone() {
		return false;
	}

	public void setPipeline(IPipeline pipeline) {
		// Not used
	}

	public IPipeline getPipeline () {
		// Not used
		return null;
	}
	
	public boolean isLastOutputStep() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setLastOutputStep(boolean isLastStep) {
		// TODO Auto-generated method stub		
	}
}
