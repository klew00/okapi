package com.googlecode.okapi.dom;

import com.googlecode.okapi.resource.ContentId;
import com.googlecode.okapi.resource.IContentFragmentEvent;
import com.googlecode.okapi.resource.PartId;


public abstract class ContentFragmentImpl implements ContentFragment, IContentFragmentEvent {

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
