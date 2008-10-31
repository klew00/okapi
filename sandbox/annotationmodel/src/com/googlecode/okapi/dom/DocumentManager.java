package com.googlecode.okapi.dom;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.okapi.base.annotation.IAnnotationManager;
import com.googlecode.okapi.resource.DocumentId;
import com.googlecode.okapi.resource.PartId;

public class DocumentManager {
	
	private Document document;
	private Map<PartId, DocumentPart> parts;
	private Map<String, IAnnotationManager<?>> annotationManagers;

	private DocumentManager(Document document) {
		this.document = document;
		parts = new HashMap<PartId, DocumentPart>();
		annotationManagers = new HashMap<String, IAnnotationManager<?>>();
	}

	public static DocumentManager create(String docId){
		return new DocumentManager( 
				new DocumentImpl(new DocumentId(docId)	));
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
