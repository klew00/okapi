package com.googlecode.okapi.resource;

public interface ResourceFactory {
	
	public Document createDocument();
	public Resource createResource();
	public ContainerResource createGroupResource();
	public DataResource createDataResource();
	public TextResource createTextResource();
	
}
