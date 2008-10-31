package com.googlecode.okapi.dom;

import com.googlecode.okapi.resource.ContentId;
import com.googlecode.okapi.resource.EventType;
import com.googlecode.okapi.resource.TextFragmentEvent;

public final class TextFragmentImpl extends ContentFragmentImpl implements TextFragment, TextFragmentEvent{
	
	private String content;

	public TextFragmentImpl(ContentId id) {
		super(id);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.content = content;
	}

	public final EventType getEventType() {
		return EventType.StartReference;
	}

	public final boolean isEmptyEvent() {
		return false;
	}

	public <A> A getAdapter(Class<A> adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
