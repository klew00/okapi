package com.googlecode.okapi.resource;

import com.googlecode.okapi.events.DataPartEvent;
import com.googlecode.okapi.events.EventType;


final class DataPartImpl extends DocumentPartImpl implements DataPart, DataPartEvent{

	public DataPartImpl(PartId id) {
		super(id);
	}
	
	public final EventType getEventType() {
		return EventType.StartDataPart;
	}

	public final boolean isEmptyEvent() {
		return false;
	}
	
}
