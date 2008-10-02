package com.googlecode.okapi.resource;

public class TextFragment extends ContentFragmentImpl{
	
	private String content;
	private String id;
	
	public String getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
