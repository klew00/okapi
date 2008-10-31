package com.googlecode.okapi.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.okapi.resource.EventType;
import com.googlecode.okapi.resource.PartId;
import com.googlecode.okapi.resource.TextFlowEvent;


final class TextFlowImpl extends DocumentPartImpl implements TextFlow, TextFlowEvent{

	private boolean standalone;
	private FlowUnit unit;

	private List<ContentFragment> content;
	
	public TextFlowImpl(PartId id) {
		super(id);
		content = new ArrayList<ContentFragment>();
	}
	
	public boolean isStandalone() {
		return standalone;
	}

	public void setStandalone(boolean standalone) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.standalone = standalone;
	}

	public FlowUnit getUnit() {
		return unit;
	}

	public void setUnit(FlowUnit unit) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.unit = unit;
	}

	public List<ContentFragment> getFlow() {
		return content;
	}

	public final EventType getEventType() {
		return EventType.StartTextFlow;
	}

	public final boolean isEmptyEvent() {
		return false;
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
