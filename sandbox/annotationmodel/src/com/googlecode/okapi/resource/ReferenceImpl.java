package com.googlecode.okapi.resource;


final class ReferenceImpl extends DocumentPartImpl implements Reference{

	private Type type;
	private String contentType;
	
	// TODO How do we know a PartId in an external document before it is parsed?
	//      Do we need a different way of referencing external resources?
	private PartId part;
	private DocumentId document;
	
	public ReferenceImpl(PartId id, DocumentManager documentManager) {
		super(id, documentManager);
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

	public PartId getPart() {
		return part;
	}

	public void setPart(PartId resource) {
		this.part = resource;
	}

	public DocumentId getDocument() {
		return document;
	}

	public void setDocument(DocumentId document) {
		this.document = document;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
	
}