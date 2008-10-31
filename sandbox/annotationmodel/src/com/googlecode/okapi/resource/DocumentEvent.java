package com.googlecode.okapi.resource;


public interface DocumentEvent extends Event{

	public DocumentId getId();
	
	public void setName(String name);
	public String getName();
	
}
