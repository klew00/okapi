package com.googlecode.okapi.events;

import com.googlecode.okapi.resource.FlowUnit;

public interface TextFlowEvent extends IDocumentPartEvent{
	
	public boolean isStandalone();
	public void setStandalone(boolean standalone);
	
	public FlowUnit getUnit();
	public void setUnit(FlowUnit unit);

}
