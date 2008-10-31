package com.googlecode.okapi.resource;

import com.googlecode.okapi.dom.FlowUnit;

public interface TextFlowEvent extends IDocumentPartEvent{
	
	public boolean isStandalone();
	public void setStandalone(boolean standalone);
	
	public FlowUnit getUnit();
	public void setUnit(FlowUnit unit);

}
