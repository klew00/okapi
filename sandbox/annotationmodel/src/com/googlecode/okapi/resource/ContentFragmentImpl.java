package com.googlecode.okapi.resource;

public abstract class ContentFragmentImpl implements ContentFragment {

	private Resource resource;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
}
