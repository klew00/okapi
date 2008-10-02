package com.googlecode.okapi.resource;

import java.util.Map;

public class DocumentManager {
	
	private Map<String, Resource> resources;

	public Resource getResourceById(String id){
		return resources.get(id);
	}
	
	
	
}
