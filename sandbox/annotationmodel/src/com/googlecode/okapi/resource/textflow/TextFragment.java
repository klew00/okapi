package com.googlecode.okapi.resource.textflow;

public final class TextFragment extends ContentFragmentImpl{
	
	private String content;

	public TextFragment(ContentId id) {
		super(id);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
