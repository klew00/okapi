package com.googlecode.okapi.filter.odf;

import java.io.File;
import java.io.IOException;

import com.googlecode.okapi.pipeline.IDocumentParser;
import com.googlecode.okapi.pipeline.ResourceEvent;
import com.googlecode.okapi.pipeline.ResourceEvent.ResourceEventType;
import com.googlecode.okapi.resource.DocumentManager;
import com.googlecode.okapi.resource.Reference;

public class OdfDocumentParser implements IDocumentParser{
	
	private OdfZipDocumentParser zipParser;
	Reference currentReference = null;
	
	public OdfDocumentParser(DocumentManager documentManager, File inputFile) throws IOException{
		zipParser = new OdfZipDocumentParser(documentManager, inputFile);
	}

	public void close() {
		zipParser.close();
	}

	public boolean hasNext() {
		return zipParser.hasNext();
	}

	public ResourceEvent next() {
		ResourceEvent event = zipParser.next();
		
		currentReference = event.type == ResourceEventType.StartReference ?
				(Reference) event.value : null;

		return event;
	}

	public IDocumentParser getParser() throws IOException{
		return getParser(currentReference);
	}

	public IDocumentParser getParser(Reference reference) throws IOException{
		if(reference == null){
			throw new IllegalStateException("No child-parser available");
		}
		
		if( "content.xml".equals(reference.getName()) ){
			ContentXmlParser contentParser = new ContentXmlParser(null, zipParser.getInputStreamForPart(currentReference.getName()));
			return contentParser;
		}
		else{
			return null;
		}
	}
	
}
