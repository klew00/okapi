package com.googlecode.okapi.resource;

import java.util.ArrayList;
import java.util.List;



abstract class DocumentPartImpl implements DocumentPart{

	private List<PartId> properties;

	private PartId id;
	private String name;
	private long version;
	private String structuralFeature;
	private String semanticFeature;

	public DocumentPartImpl(PartId id) {
		this.id = id;
	}
	
	public PartId getId() {
		return id;
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

	@SuppressWarnings("unchecked")
	public <A> A getAdapter(Class<A> adapter) {
		return (A) this;
	}
	
	public List<PartId> getProperties() {
		if(properties == null){
			properties = new ArrayList<PartId>();
		}
		return properties;
	}
	
	
}
