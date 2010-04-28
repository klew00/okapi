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
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;

public class EventLogger extends BasePipelineStep {

	private int indent = 0;
	private boolean increasing = true;
	
	@Override
	public String getDescription() {
		return "Logs events going through a pipeline.";
	}

	@Override
	public String getName() {
		return "Event logger";
	}

	private String getEventDescr(Event event) {
		String res = ""; 
		if (event.getResource() != null)
			res = String.format("  [%s]", event.getResource().getId());
		
		switch ( event.getEventType() ) {
			case START_DOCUMENT:
				res +=  "  " + ((StartDocument) event.getResource()).getName();
				break;
			case START_SUBDOCUMENT:
				res +=  "  " + ((StartSubDocument) event.getResource()).getName();
				break;
		}
		return res;
	}
	
	private void printEvent(Event event) {
		String indentStr = "";
		for (int i = 0; i < indent; i++) 
			indentStr += "  ";
		
		System.out.print(indentStr);
		System.out.print(event.getEventType() + getEventDescr(event));
		System.out.println();
	}
	
	@Override
	public Event handleEvent(Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
		case START_SUBDOCUMENT:
		case START_GROUP:
		case START_BATCH:
		case START_BATCH_ITEM:
			if (!increasing) System.out.println();
			printEvent(event);
			indent++; 
			increasing = true;
			break;

		case END_DOCUMENT:
		case END_SUBDOCUMENT:
		case END_GROUP:
		case END_BATCH:
		case END_BATCH_ITEM:
			if (indent > 0) indent--;
			increasing = false;
			printEvent(event);
			break;
			
		default:
			if (!increasing) System.out.println();
			printEvent(event);
		}
		
		
		return super.handleEvent(event);
	}

}
