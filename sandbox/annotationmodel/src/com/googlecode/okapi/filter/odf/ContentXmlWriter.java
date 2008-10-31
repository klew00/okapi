package com.googlecode.okapi.filter.odf;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Element;
import nux.xom.io.StreamingSerializer;
import nux.xom.io.StreamingSerializerFactory;

import com.googlecode.okapi.events.ContainerEvent;
import com.googlecode.okapi.events.ContainerFragmentEvent;
import com.googlecode.okapi.events.DataPartEvent;
import com.googlecode.okapi.events.DocumentEvent;
import com.googlecode.okapi.events.IContentFragmentEvent;
import com.googlecode.okapi.events.IDocumentPartEvent;
import com.googlecode.okapi.events.ReferenceEvent;
import com.googlecode.okapi.events.ResourceFragmentEvent;
import com.googlecode.okapi.events.TextFlowEvent;
import com.googlecode.okapi.events.TextFragmentEvent;
import com.googlecode.okapi.pipeline.event.BaseEventHandler;

public class ContentXmlWriter extends BaseEventHandler{
	
	StreamingSerializer serializer;
	Element currentElement;

	public ContentXmlWriter(OutputStream output) {
		StreamingSerializerFactory factory = new StreamingSerializerFactory();
		serializer = factory.createXMLSerializer(output, "utf-8");
	}
	
	private void handleSerializationException(IOException e){
		e.printStackTrace();
	}

	@Override
	protected void handleStartDocument(DocumentEvent document) {
		try{
			serializer.writeXMLDeclaration();
		}
		catch(IOException e){
			handleSerializationException(e);
		}
	}

	@Override
	protected void handleEndDocument() {
		try {
			serializer.writeEndDocument();
		} catch (IOException e) {
			handleSerializationException(e);
		}
	}

	// called before the specific part...
	@Override
	protected void handleStartDocumentPart(IDocumentPartEvent part) {
		if(ContentXmlParser.FEATURE_XML_ELEMENT.equals(part.getStructuralFeature())){
			currentElement = new Element("");
		}
		else if(ContentXmlParser.FEATURE_XML_ATTRIBUTE.equals(part.getStructuralFeature())){
			
		}
	}

	@Override
	protected void handleEndDocumentPart() {}

	// Document Parts
	
	@Override
	protected void handleStartReference(ReferenceEvent reference) {}
	@Override
	protected void handleEndReference() {}

	@Override
	protected void handleStartDataPart(DataPartEvent dataPart) {}
	@Override
	protected void handleEndDataPart() {}

	@Override
	protected void handleStartContainer(ContainerEvent container) {
		try{
			if(ContentXmlParser.FEATURE_XML_ELEMENT.equals(container.getStructuralFeature())){
				serializer.writeEndTag();
			}
		}
		catch(IOException e){
			handleSerializationException(e);
		}
	}

	@Override
	protected void handleEndContainer() {}

	@Override
	protected void handleStartTextFlow(TextFlowEvent textFlow) {}
	@Override
	protected void handleEndTextFlow() {}

	@Override
	protected void handleStartTextFlowContent() {}
	@Override
	protected void handleEndTextFlowContent() {}

	@Override
	protected void handleStartChildren() {}
	@Override
	protected void handleEndChildren() {}

	@Override
	protected void handleStartProperties() {}
	@Override
	protected void handleEndProperties() {
		try{
			serializer.writeStartTag(currentElement);
		}
		catch(IOException e){
			handleSerializationException(e);
		}
	}

	// fragments
	
	@Override
	protected void handleStartContentFragment(IContentFragmentEvent contentFragment) {}
	@Override
	protected void handleEndContentFragment() {}

	@Override
	protected void handleStartTextFragment(TextFragmentEvent textFragment) {}
	@Override
	protected void handleEndTextFragment() {}

	@Override
	protected void handleStartContainerFragment(ContainerFragmentEvent containerFragment) {}
	@Override
	protected void handleEndContainerFragment() {}

	@Override
	protected void handlStartResourceFragment(ResourceFragmentEvent resourceFragment) {}
	@Override
	protected void handleEndResourceFragment() {}

	
}
