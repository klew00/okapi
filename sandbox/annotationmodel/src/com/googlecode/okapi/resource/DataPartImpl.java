package com.googlecode.okapi.resource;

import com.googlecode.okapi.events.DataPartEvent;
import com.googlecode.okapi.events.EventType;


final class DataPartImpl extends DocumentPartImpl implements DataPart, DataPartEvent{

	public DataPartImpl(PartId id, DocumentManager documentManager) {
		super(id, documentManager);
	}
	
	public final EventType getEventType() {
		return EventType.StartDataPart;
	}

	public final boolean isEmptyEvent() {
		return false;
	}
	
}
