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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.steps.xliffkit.common.persistence.FactoryBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class MultiEventBean extends PersistenceBean {
	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();
	private String id;
	private boolean propagateAsSingleEvent = false;
	private List<EventBean> events = new ArrayList<EventBean>();
	
	public MultiEventBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		if (obj instanceof MultiEvent) {
			MultiEvent mev = (MultiEvent) obj; 

			for (FactoryBean annotationBean : annotations)
				mev.setAnnotation(annotationBean.get(IAnnotation.class));
	
			mev.setId(id);
			mev.setPropagateAsSingleEvent(propagateAsSingleEvent);
			
			for (EventBean eventBean : events)
				mev.addEvent(eventBean.get(Event.class));
		}
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		return classRef.cast(get(new MultiEvent()));
	}
	
	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof MultiEvent) {
			MultiEvent mev = (MultiEvent) obj;
			
			for (IAnnotation annotation : mev.getAnnotations()) {
				FactoryBean annotationBean = new FactoryBean(getSession());
				annotations.add(annotationBean);
				annotationBean.set(annotation);
			}
			
			id = mev.getId();
			propagateAsSingleEvent = mev.isPropagateAsSingleEvent();
			
			for (Event event : mev) {
				EventBean eventBean = new EventBean(getSession());
				events.add(eventBean);
				eventBean.set(event);
			}
		}
		return this;
	}

	public List<FactoryBean> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<FactoryBean> annotations) {
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
