package com.googlecode.okapi.resource;

import com.googlecode.okapi.events.IContentFragmentEvent;


abstract class ContentFragmentImpl implements ContentFragment, IContentFragmentEvent {

	private volatile boolean immutable = false;
	
	private PartId part;
	private ContentId id;
	
	public ContentFragmentImpl(ContentId id) {
		this.id = id;
	}
	
	public ContentId getId() {
		return id;
	}
	
	public PartId getPart() {
		return part;
	}

	public void setPart(PartId part) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.part = part;
	}

	public synchronized void setImmutable(boolean immutable) {
		if(immutable == this.immutable)
			return;
		
		this.immutable = !immutable;
	}
	
	public boolean isImmutable() {
		return immutable;
	}
	
}
