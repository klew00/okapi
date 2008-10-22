package com.googlecode.okapi.resource;

import java.util.List;

public interface Document extends Resource<DocumentId>, ContentTypeProvider{
	
	public List<PartId> getParts();

	public void setName(String name);
	public String getName();
}
