package com.googlecode.okapi.resource;

public abstract class ResourceId{

	private final String id;
	
	public ResourceId(String id) {
		this.id = id.intern();
	}
	
	public String get(){
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ResourceId ?
			((ResourceId)obj).id == this.id 
			: false;
	}

}
