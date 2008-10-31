package com.googlecode.okapi.resource;


public interface IDocumentPartEvent extends Event{

	public PartId getId();

	public String getName();
	public void setName(String name);

	public String getStructuralFeature();
	public void setStructuralFeature(String structuralFeature);
	
	public String getSemanticFeature();
	public void setSemanticFeature(String semanticFeature);
	
	public long getVersion();
	public void setVersion(long version);
	
	
}
