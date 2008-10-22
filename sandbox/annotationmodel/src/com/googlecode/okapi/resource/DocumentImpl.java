package com.googlecode.okapi.resource;

import java.util.ArrayList;
import java.util.List;

public final class DocumentImpl implements Document{

	private List<PartId> parts;
	private String contentType;
	private DocumentId id;
	private String name;
	
	public DocumentImpl(DocumentId id) {
		this.id = id;
	}
	
	public DocumentId getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public List<PartId> getParts() {
		if(parts == null){
			parts = new ArrayList<PartId>();
		}
		return parts;
	}

	public <A> A getAdapter(Class<A> adapter) {
		return null;
	}
	
}
