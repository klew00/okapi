package com.googlecode.okapi.dom;

import com.googlecode.okapi.resource.AbstractEventFactory;
import com.googlecode.okapi.resource.AnnotationDataEvent;
import com.googlecode.okapi.resource.AnnotationEvent;
import com.googlecode.okapi.resource.ContainerEvent;
import com.googlecode.okapi.resource.ContainerFragmentEvent;
import com.googlecode.okapi.resource.DataPartEvent;
import com.googlecode.okapi.resource.DocumentEvent;
import com.googlecode.okapi.resource.ReferenceEvent;
import com.googlecode.okapi.resource.ResourceFragmentEvent;
import com.googlecode.okapi.resource.TextFlowEvent;
import com.googlecode.okapi.resource.TextFragmentEvent;

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
