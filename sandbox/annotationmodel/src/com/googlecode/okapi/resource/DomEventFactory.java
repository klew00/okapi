package com.googlecode.okapi.resource;

import com.googlecode.okapi.events.AbstractEventFactory;
import com.googlecode.okapi.events.AnnotationDataEvent;
import com.googlecode.okapi.events.AnnotationEvent;
import com.googlecode.okapi.events.ContainerEvent;
import com.googlecode.okapi.events.ContainerFragmentEvent;
import com.googlecode.okapi.events.DataPartEvent;
import com.googlecode.okapi.events.DocumentEvent;
import com.googlecode.okapi.events.ReferenceEvent;
import com.googlecode.okapi.events.ResourceFragmentEvent;
import com.googlecode.okapi.events.TextFlowEvent;
import com.googlecode.okapi.events.TextFragmentEvent;

public class DomEventFactory extends AbstractEventFactory{

	private ResourceFactory factory;
	
	public DomEventFactory(ResourceFactory factory) {
		this.factory = factory;
	}
	
	public AnnotationDataEvent createAnnotationDataEvent() {
		// TODO handle annotations
		throw new NoSuchMethodError();
	}

	public AnnotationEvent createStartAnnotationEvent() {
		// TODO handle annotations
		throw new NoSuchMethodError();
	}

	public ContainerEvent createStartContainerEvent() {
		return (ContainerEvent) factory.createContainer();
	}

	public ContainerFragmentEvent createStartContainerFragmentEvent() {
		return (ContainerFragmentEvent) factory.createContainerFragment();
	}

	public DataPartEvent createStartDataPartEvent() {
		return (DataPartEvent) factory.createDataPart();
	}

	public DocumentEvent createStartDocumentEvent() {
		return (DocumentEvent) factory.createDocument();
	}

	public ReferenceEvent createStartReferenceEvent() {
		return (ReferenceEvent) factory.createReference();
	}

	public ResourceFragmentEvent createStartResourceFragmentEvent() {
		return (ResourceFragmentEvent) factory.createResourceFragment();
	}

	public TextFlowEvent createStartTextFlowEvent() {
		return (TextFlowEvent) factory.createTextFlow();
	}

	public TextFragmentEvent createStartTextFragmentEvent() {
		return (TextFragmentEvent) factory.createTextFragment();
	}

}
