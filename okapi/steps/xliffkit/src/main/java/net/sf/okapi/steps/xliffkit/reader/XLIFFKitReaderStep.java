/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.reader;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.xliffkit.opc.OPCPackageReader;

@UsingParameters()
public class XLIFFKitReaderStep extends BasePipelineStep {

	private IFilter reader = new OPCPackageReader();
	private boolean isDone = true;
	
	public String getDescription () {
		return "Reads XLIFF translation kit. Expects: Raw document for T-kit. Sends back: filter events.";
	}

	public String getName () {
		return "XLIFF Kit Reader";
	}

	@Override
	public Event handleEvent(Event event) {
		switch (event.getEventType()) {
		case START_BATCH:
			isDone = true;
			break;

		case START_BATCH_ITEM:
			isDone = false;
			return event;

		case RAW_DOCUMENT:
			isDone = false;
			reader.open((RawDocument)event.getResource());
			return reader.next();
		}

		if (isDone) {
			return event;
		} else {
			Event e = reader.next();
			isDone = !reader.hasNext();
			return e;
		}
	}
	
	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public void destroy() {
		reader.close();
	}

	@Override
	public void cancel() {
		reader.cancel();
	}
}
