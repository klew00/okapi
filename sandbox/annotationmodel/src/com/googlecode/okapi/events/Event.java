package com.googlecode.okapi.events;

public interface Event {

	public EventType getEventType();
	
	public boolean isEmptyEvent();
	
}
