package com.googlecode.okapi.events;

public interface TextFragmentEvent extends IContentFragmentEvent{

	public String getContent();
	public void setContent(String content);

}
