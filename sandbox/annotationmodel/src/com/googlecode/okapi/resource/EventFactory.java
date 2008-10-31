package com.googlecode.okapi.resource;

public interface EventFactory {

	public Event create(EventType type);
	
	public DocumentEvent createStartDocumentEvent(); 
	public Event createEndDocumentEvent();
	
	public ContainerEvent createStartContainerEvent();
	public Event createEndContainerEvent();

	public TextFlowEvent createStartTextFlowEvent();
	public Event createEndTextFlowEvent();
	
	public DataPartEvent createStartDataPartEvent();
	public Event createEndDataPartEvent();
	
	public ReferenceEvent createStartReferenceEvent();
	public Event createEndReferenceEvent();
	
	// DocumentPart body events
	public Event createStartPropertiesEvent();
	public Event createEndPropertiesEvent();
	
	// Document and Container body events
	public Event createStartChildrenEvent();
	public Event createEndChildrenEvent();
	
	// TextFlow body events
	public Event createStartTextFlowContentEvent();
	public Event createEndTextFlowContentEvent();

	// TextFlow content events
	public ContainerFragmentEvent createStartContainerFragmentEvent();
	public Event createEndContainerFragmentEvent();
	
	public TextFragmentEvent createStartTextFragmentEvent();
	public Event createEndTextFragmentEvent();
	
	public ResourceFragmentEvent createStartResourceFragmentEvent();
	public Event createEndResourceFragmentEvent();
	
	// Resource Annotations
	public AnnotationEvent createStartAnnotationEvent();
	public AnnotationDataEvent createAnnotationDataEvent();
	public Event createEndAnnotationEvent();

}
