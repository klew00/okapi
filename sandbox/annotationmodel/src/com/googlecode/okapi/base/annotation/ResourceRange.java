package com.googlecode.okapi.base.annotation;

public class ResourceRange {

	private ResourcePointer start;
	private ResourcePointer end;
	
	public ResourcePointer getStart() {
		return start;
	}
	public void setStart(ResourcePointer start) {
		this.start = start;
	}
	public ResourcePointer getEnd() {
		return end;
	}
	public void setEnd(ResourcePointer end) {
		this.end = end;
	}
	
}
