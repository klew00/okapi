package com.googlecode.okapi.events;

import com.googlecode.okapi.resource.DocumentId;

public interface DocumentEvent extends Event{

	public DocumentId getId();
	
	public void setName(String name);
	public String getName();
	
}
