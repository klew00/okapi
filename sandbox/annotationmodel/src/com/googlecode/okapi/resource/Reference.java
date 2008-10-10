package com.googlecode.okapi.resource;

public interface Reference extends DocumentPart, ContentTypeProvider{
	
	public static enum Type{
		/**
		 * References a Resource internal to this Document
		 */
		Internal,
		
		/**
		 * References a Resource outside of this Document
		 */
		External
	}

	public Type getType();
	public void setType(Type type);

	public PartId getPart();
	public void setPart(PartId part);
	
	public DocumentId getDocument();
	public void setDocument(DocumentId document);
	
}
