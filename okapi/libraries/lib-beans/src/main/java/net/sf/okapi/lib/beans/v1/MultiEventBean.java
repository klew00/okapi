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

package net.sf.okapi.lib.beans.v1;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class MultiEventBean extends PersistenceBean<MultiEvent> {
	private AnnotationsBean annotations = new AnnotationsBean();
	private String id;
	private boolean propagateAsSingleEvent = false;
	private List<EventBean> events = new ArrayList<EventBean>();

	@Override
	protected MultiEvent createObject(IPersistenceSession session) {
		return new MultiEvent();
	}

	@Override
	protected void fromObject(MultiEvent obj, IPersistenceSession session) {
		annotations.set(obj.getAnnotations(), session);
		
		id = obj.getId();
		propagateAsSingleEvent = obj.isPropagateAsSingleEvent();
		
		for (Event event : obj) {
			EventBean eventBean = new EventBean();
			events.add(eventBean);
			eventBean.set(event, session);
		}
	}

	@Override
	protected void setObject(MultiEvent obj, IPersistenceSession session) {
		for (FactoryBean annotationBean : annotations.getItems())
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));

		obj.setId(id);
		obj.setPropagateAsSingleEvent(propagateAsSingleEvent);
		
		for (EventBean eventBean : events)
			obj.addEvent(eventBean.get(Event.class, session));
	}

	public AnnotationsBean getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationsBean annotations) {
		this.annotations = annotations;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isPropagateAsSingleEvent() {
		return propagateAsSingleEvent;
	}

	public void setPropagateAsSingleEvent(boolean propagateAsSingleEvent) {
		this.propagateAsSingleEvent = propagateAsSingleEvent;
	}

	public List<EventBean> getEvents() {
		return events;
	}

	public void setEvents(List<EventBean> events) {
		this.events = events;
	}
}
