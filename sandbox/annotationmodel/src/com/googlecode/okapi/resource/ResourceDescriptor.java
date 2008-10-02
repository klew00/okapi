package com.googlecode.okapi.resource;

public interface ResourceDescriptor extends IdProvider{
	public String getId();
	public void setId(String id);

	public String getName();
	public void setName(String name);
	
	public long getVersion();
	public void setVersion(long version);
	
	public String getStructuralFeature();
	public void setStructuralFeature(String structuralFeature);
	
	public String getSemanticFeature();
	public void setSemanticFeature(String semanticFeature);
	
	public DocumentManager getDocumentManager();

	
}
