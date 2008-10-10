package com.googlecode.okapi.resource;

import java.util.List;


final class ContainerImpl extends DocumentPartImpl implements Container{

	private List<PartId> children;
	
	public ContainerImpl(PartId id) {
		super(id);
	}

	public List<PartId> getParts() {
		return children;
	}
	
}
