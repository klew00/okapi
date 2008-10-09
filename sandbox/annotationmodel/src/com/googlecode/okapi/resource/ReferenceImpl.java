package com.googlecode.okapi.resource;


final class ReferenceImpl extends DocumentPartImpl implements Reference{

	private Type type;
	private String contentType;
	private PartId resource;
	private PartId document;
	
	public ReferenceImpl(PartId id) {
		super(id);
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public PartId getResource() {
		return resource;
	}

	public void setResource(PartId resource) {
		this.resource = resource;
	}

	public PartId getDocument() {
		return document;
	}

	public void setDocument(PartId document) {
		this.document = document;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
	
}