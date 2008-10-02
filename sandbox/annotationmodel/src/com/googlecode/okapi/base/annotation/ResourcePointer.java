package com.googlecode.okapi.base.annotation;

import com.googlecode.okapi.resource.IdProvider;

public class ResourcePointer {

	private IdProvider id;
	private int offset;
	
	public IdProvider getId() {
		return id;
	}
	
	public void setId(IdProvider id) {
		this.id = id;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	
	
	
}
