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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.steps.xliffkit.common.persistence.FactoryBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class EventBean extends PersistenceBean {

	private EventType type;
	private FactoryBean resource = new FactoryBean();

	@Override
	protected Object createObject(IPersistenceSession session) {
		return new Event(type);
	}

	@Override
	protected void fromObject(Object obj, IPersistenceSession session) {
		if (obj instanceof Event) {
			Event e = (Event) obj;
			
			type = e.getEventType();
			resource.set(e.getResource(), session);
		}
	}

	@Override
	protected void setObject(Object obj, IPersistenceSession session) {
		if (obj instanceof Event) {
			Event e = (Event) obj;
			e.setResource(resource.get(IResource.class, session));		
		}		
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public EventType getType() {
		return type;
	}

	public void setResource(FactoryBean resource) {
		this.resource = resource;
	}

	public FactoryBean getResource() {
		return resource;
	}
}
