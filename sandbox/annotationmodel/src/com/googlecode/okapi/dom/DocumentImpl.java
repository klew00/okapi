package com.googlecode.okapi.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.okapi.resource.DocumentEvent;
import com.googlecode.okapi.resource.DocumentId;
import com.googlecode.okapi.resource.EventType;
import com.googlecode.okapi.resource.PartId;

public final class DocumentImpl implements Document, DocumentEvent{

	private List<PartId> parts;
	private volatile boolean immutable = false;
	private String contentType;
	private DocumentId id;
	private String name;
	
	public DocumentImpl(DocumentId id) {
		this.id = id;
		parts = new ArrayList<PartId>();
	}
	
	public DocumentId getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.contentType = contentType;
	}

	public List<PartId> getParts() {
		return parts;
	}

	public final EventType getEventType() {
		return EventType.StartDocument;
	}

	public final boolean isEmptyEvent() {
		return false;
	}

	public <A> A getAdapter(Class<A> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized void setImmutable(boolean immutable) {
		if(immutable == this.immutable)
			return;
		
		if(this.immutable){
			parts = new ArrayList<PartId>(parts);
		}
		else{
			parts = Collections.unmodifiableList(parts);
		}
		this.immutable = !immutable;
	}
	
	public boolean isImmutable() {
		return immutable;
	}
	
	
}
