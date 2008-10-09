package com.googlecode.okapi.resource;

import java.util.List;

public abstract interface DocumentPart extends Resource<PartId>{

	public String getName();
	public void setName(String name);
	
	public long getVersion();
	public void setVersion(long version);
	
	public String getStructuralFeature();
	public void setStructuralFeature(String structuralFeature);
	
	public String getSemanticFeature();
	public void setSemanticFeature(String semanticFeature);
	
	public List<PartId> getProperties();
	
	
}
