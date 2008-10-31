package com.googlecode.okapi.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.okapi.resource.IDocumentPartEvent;
import com.googlecode.okapi.resource.PartId;



abstract class DocumentPartImpl implements DocumentPart, IDocumentPartEvent{

	private List<PartId> properties;
	
	private volatile boolean immutable = false;

	private PartId id;
	private String name;
	private long version;
	private String structuralFeature;
	private String semanticFeature;

	public DocumentPartImpl(PartId id) {
		this.id = id;
		this.properties = new ArrayList<PartId>();
	}
	
	public PartId getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.name = name;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.version = version;
	}
	public String getStructuralFeature() {
		return structuralFeature;
	}
	public void setStructuralFeature(String structuralFeature) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.structuralFeature = structuralFeature;
	}
	public String getSemanticFeature() {
		return semanticFeature;
	}
	public void setSemanticFeature(String semanticFeature) {
		if(isImmutable()){
			throw new UnsupportedOperationException();
		}
		this.semanticFeature = semanticFeature;
	}

	@SuppressWarnings("unchecked")
	public <A> A getAdapter(Class<A> adapter) {
		return (A) this;
	}
	
	List<PartId> immutableProperties = null;
	
	public List<PartId> getProperties() {
		return properties;
	}
	
	public synchronized void setImmutable(boolean immutable) {
		if(immutable == this.immutable)
			return;
		
		if(this.immutable){
			properties = new ArrayList<PartId>(properties);
		}
		else{
			properties = Collections.unmodifiableList(properties);
		}
		this.immutable = !immutable;
	}
	
	public boolean isImmutable() {
		return immutable;
	}
	
	
}
