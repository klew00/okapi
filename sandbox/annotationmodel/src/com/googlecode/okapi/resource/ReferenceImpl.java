package com.googlecode.okapi.resource;

import com.googlecode.okapi.events.EventType;
import com.googlecode.okapi.events.ReferenceEvent;


final class ReferenceImpl extends DocumentPartImpl implements Reference, ReferenceEvent{

	private ReferenceType type;
	private String contentType;
	
	// TODO How do we know a PartId in an external document before it is parsed?
	//      Do we need a different way of referencing external resources?
	private PartId part;
	private DocumentId document;
	
	public ReferenceImpl(PartId id) {
		super(id);
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public ReferenceType getType() {
		return type;
	}
	
	public void setType(ReferenceType type) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.type = type;
	}

	public PartId getPart() {
		return part;
	}

	public void setPart(PartId resource) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.part = resource;
	}

	public DocumentId getDocument() {
		return document;
	}

	public void setDocument(DocumentId document) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.document = document;
	}

	public void setContentType(String contentType) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.contentType = contentType;
	}

	public final EventType getEventType() {
		return EventType.StartReference;
	}

	public final boolean isEmptyEvent() {
		return false;
	}

	@Override
	public synchronized void setImmutable(boolean immutable) {
		if(immutable != isImmutable()){
			if(immutable){
				// nothing to immute
			}
		}
		super.setImmutable(immutable);
	}
	
	
}