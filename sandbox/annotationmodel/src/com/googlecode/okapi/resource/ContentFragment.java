package com.googlecode.okapi.resource;


public abstract interface ContentFragment extends Resource<ContentId>{

	public PartId getPart();
	public void setPart(PartId partId);

}
