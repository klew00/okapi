package com.googlecode.okapi.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.okapi.events.ContainerEvent;
import com.googlecode.okapi.events.EventType;


final class ContainerImpl extends DocumentPartImpl implements Container, ContainerEvent{

	private List<PartId> children;
	
	public ContainerImpl(PartId id) {
		super(id);
		children = new ArrayList<PartId>();
	}

	public List<PartId> getParts() {
		return children;
	}
	
	@Override
	public synchronized void setImmutable(boolean immutable) {
		if(immutable != isImmutable()){
			if(immutable){
				children = Collections.unmodifiableList(children);
			}
			else{
				children = new ArrayList<PartId>(children);
			}
		}
		super.setImmutable(immutable);
	}
	

	public final EventType getEventType() {
		return EventType.StartContainer;
	}

	public final boolean isEmptyEvent() {
		return false;
	}
	
}
