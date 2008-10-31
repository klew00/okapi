package com.googlecode.okapi.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.okapi.events.ContainerFragmentEvent;
import com.googlecode.okapi.events.EventType;


public final class ContainerFragmentImpl extends ContentFragmentImpl implements ContainerFragment, ContainerFragmentEvent{
	
	private List<ContentFragment> content;
	private String id;
	
	public ContainerFragmentImpl(ContentId id) {
		super(id);
		content = new ArrayList<ContentFragment>(); 
	}
	
	public List<ContentFragment> getFlow() {
		return content;
	}

	public final EventType getEventType() {
		return EventType.StartContainerFragment;
	}

	public final boolean isEmptyEvent() {
		return false;
	}

	public <A> A getAdapter(Class<A> adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public synchronized void setImmutable(boolean immutable) {
		if(immutable != isImmutable()){
			if(immutable){
				content = Collections.unmodifiableList(content);
			}
			else{
				content = new ArrayList<ContentFragment>(content);
			}
		}
		super.setImmutable(immutable);
	}
	
	
	
}
