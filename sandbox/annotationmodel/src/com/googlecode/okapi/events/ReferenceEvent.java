package com.googlecode.okapi.events;

import com.googlecode.okapi.resource.DocumentId;
import com.googlecode.okapi.resource.PartId;
import com.googlecode.okapi.resource.ReferenceType;

public interface ReferenceEvent extends IDocumentPartEvent{

	public ReferenceType getType();
	public void setType(ReferenceType type);

	public PartId getPart();
	public void setPart(PartId part);
	
	public DocumentId getDocument();
	public void setDocument(DocumentId document);
	
}
