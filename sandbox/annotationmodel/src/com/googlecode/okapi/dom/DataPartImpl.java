package com.googlecode.okapi.dom;

import com.googlecode.okapi.resource.DataPartEvent;
import com.googlecode.okapi.resource.EventType;
import com.googlecode.okapi.resource.PartId;


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
