package com.googlecode.okapi.resource;

import com.googlecode.okapi.dom.ReferenceType;

public interface ReferenceEvent extends IDocumentPartEvent{

	public ReferenceType getType();
	public void setType(ReferenceType type);

	public PartId getPart();
	public void setPart(PartId part);
	
	public DocumentId getDocument();
	public void setDocument(DocumentId document);
	
}
