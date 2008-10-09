package com.googlecode.okapi.resource;

import java.util.Map;
import java.util.Set;

import com.googlecode.okapi.base.annotation.IAnnotationManager;

public class DocumentManager {
	
	private Map<PartId, DocumentPart> parts;
	private Document document;
	private Map<String, IAnnotationManager<?>> annotationManagers;
	private ResourceFactory factory;
	
	public ResourceFactory getFactory(){
		return factory;
	}
	
	public Document getDocument(){
		return document;
	}

	public DocumentPart getPartById(PartId id){
		return parts.get(id);
	}
	
	public boolean hasAnnotationManager(String id){
		return annotationManagers.containsKey(id);
	}
	
	
	public void registerAnnotationManager(IAnnotationManager<?> manager){
		annotationManagers.put(manager.getId(), manager);
	}
	
	public void unregisterAnnotationManager(IAnnotationManager<?> manager){
		annotationManagers.remove(manager.getId());
	}
	
}
