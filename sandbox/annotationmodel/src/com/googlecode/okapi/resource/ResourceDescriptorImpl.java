package com.googlecode.okapi.resource;

public class ResourceDescriptorImpl implements ResourceDescriptor{
	
	private String id;
	private String name;
	private long version;
	private String structuralFeature;
	private String semanticFeature;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public String getStructuralFeature() {
		return structuralFeature;
	}
	public void setStructuralFeature(String structuralFeature) {
		this.structuralFeature = structuralFeature;
	}
	public String getSemanticFeature() {
		return semanticFeature;
	}
	public void setSemanticFeature(String semanticFeature) {
		this.semanticFeature = semanticFeature;
	}
	
	
	

}
