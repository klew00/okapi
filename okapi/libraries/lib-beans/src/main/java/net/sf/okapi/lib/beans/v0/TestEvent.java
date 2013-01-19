/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.beans.v0;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;

public class TestEvent extends Event{

	private String id;
	private TestEvent parent;
	private EventType type;
	
	public TestEvent(String id) {
		super(EventType.DOCUMENT_PART);
		this.setId(id);
	}

	public void setParent(TestEvent parent) {
		this.parent = parent;
	}

	public TestEvent getParent() {
		return parent;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public EventType getType() {
		return type;
	}
}
