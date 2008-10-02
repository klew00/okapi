package com.googlecode.okapi.resource;

import java.util.List;

public class ContainerResource extends Resource{

	private List<Resource> children;

	public List<Resource> getChildren() {
		return children;
	}

}
