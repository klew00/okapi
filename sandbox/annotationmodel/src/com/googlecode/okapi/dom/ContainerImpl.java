package com.googlecode.okapi.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.okapi.resource.ContainerEvent;
import com.googlecode.okapi.resource.EventType;
import com.googlecode.okapi.resource.PartId;


public class ContainerImpl extends DocumentPartImpl implements Container, ContainerEvent{

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
