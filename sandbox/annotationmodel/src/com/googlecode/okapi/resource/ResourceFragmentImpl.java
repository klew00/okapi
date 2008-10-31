package com.googlecode.okapi.resource;

import com.googlecode.okapi.events.EventType;
import com.googlecode.okapi.events.ResourceFragmentEvent;

public final class ResourceFragmentImpl extends ContentFragmentImpl implements ResourceFragment, ResourceFragmentEvent{

	public ResourceFragmentImpl(ContentId id) {
		super(id);
	}

	public <A> A getAdapter(Class<A> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public final EventType getEventType() {
		return EventType.StartResourceFragment;
	}

	public final boolean isEmptyEvent() {
		return false;
	}
	
	
}
