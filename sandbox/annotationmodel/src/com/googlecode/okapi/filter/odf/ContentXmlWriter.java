package com.googlecode.okapi.filter.odf;

import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator.Attribute;

import javax.xml.stream.events.StartElement;

import com.googlecode.okapi.base.annotation.IAnnotation;
import com.googlecode.okapi.pipeline.BaseEventHandler;
import com.googlecode.okapi.resource.Container;
import com.googlecode.okapi.resource.DataPart;
import com.googlecode.okapi.resource.Document;
import com.googlecode.okapi.resource.DocumentPart;
import com.googlecode.okapi.resource.Reference;
import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.builder.ResourceEvent;
import com.googlecode.okapi.resource.textflow.ContainerFragment;
import com.googlecode.okapi.resource.textflow.ContentFragment;
import com.googlecode.okapi.resource.textflow.ResourceFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

import nu.xom.Element;
import nux.xom.io.StreamingSerializer;
import nux.xom.io.StreamingSerializerFactory;

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
	protected void handleStartDocument(Document document) {
		try{
			serializer.writeXMLDeclaration();
		}
		catch(IOException e){
			handleSerializationException(e);
		}
	}

	@Override
	protected void handleEndDocument(Document document) {
		try {
			serializer.writeEndDocument();
		} catch (IOException e) {
			handleSerializationException(e);
		}
	}

	protected void handleAnnotation(IAnnotation<?> annotation) {}

	// called before the specific part...
	@Override
	protected void handleStartDocumentPart(DocumentPart part) {
		if(ContentXmlParser.FEATURE_XML_ELEMENT.equals(part.getStructuralFeature())){
			currentElement = new Element("");
		}
		else if(ContentXmlParser.FEATURE_XML_ATTRIBUTE.equals(part.getStructuralFeature())){
			
		}
	}

	@Override
	protected void handleEndDocumentPart(DocumentPart part) {}

	// Document Parts
	
	@Override
	protected void handleStartReference(Reference reference) {}
	@Override
	protected void handleEndReference(Reference reference) {}

	@Override
	protected void handleStartDataPart(DataPart dataPart) {}
	@Override
	protected void handleEndDataPart(DataPart dataPart) {}

	@Override
	protected void handleStartContainer(Container container) {
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
	protected void handleEndContainer(Container container) {}

	@Override
	protected void handleStartTextFlow(TextFlow textFlow) {}
	@Override
	protected void handleEndTextFlow(TextFlow textFlow) {}

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
	protected void handleStartContentFragment(ContentFragment contentFragment) {}
	@Override
	protected void handleEndContentFragment(ContentFragment contentFragment) {}

	@Override
	protected void handleStartTextFragment(TextFragment textFragment) {}
	@Override
	protected void handleEndTextFragment(TextFragment textFragment) {}

	@Override
	protected void handleStartContainerFragment(ContainerFragment containerFragment) {}
	@Override
	protected void handleEndContainerFragment(ContainerFragment containerFragment) {}

	@Override
	protected void handlStartResourceFragment(ResourceFragment resourceFragment) {}
	@Override
	protected void handleEndResourceFragment(ResourceFragment resourceFragment) {}

	
}
