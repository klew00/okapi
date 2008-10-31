package com.googlecode.okapi.filter.odf;

import java.io.File;
import java.io.IOException;

import com.googlecode.okapi.events.Event;
import com.googlecode.okapi.events.EventFactory;
import com.googlecode.okapi.events.EventType;
import com.googlecode.okapi.filter.zip.ZipDocumentParser;
import com.googlecode.okapi.pipeline.IDocumentParser;
import com.googlecode.okapi.resource.Reference;

public class OdfDocumentParser implements IDocumentParser{
	
	private ZipDocumentParser zipParser;
	Reference currentReference = null;
	
	public OdfDocumentParser(EventFactory factory, File inputFile) throws IOException{
		zipParser = new ZipDocumentParser(factory, inputFile);
	}

	public void close() {
		zipParser.close();
	}

	public boolean hasNext() {
		return zipParser.hasNext();
	}

	public Event next() {
		Event event = zipParser.next();
		
		currentReference = event.getEventType() == EventType.StartReference ?
				(Reference) event : null;

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
