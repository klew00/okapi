package com.googlecode.okapi.resource;

public abstract class AbstractEventFactory implements EventFactory{

	public final Event create(EventType type) {
		switch(type){
		case AnnotationData:
			return createAnnotationDataEvent();
		case EndAnnotation:
			return createEndAnnotationEvent();
		case EndChildren:
			return createEndChildrenEvent();
		case EndContainer:
			return createEndContainerEvent();
		case EndContainerFragment:
			return createEndContainerFragmentEvent();
		case EndDataPart:
			return createEndDataPartEvent();
		case EndDocument:
			return createEndDocumentEvent();
		case EndProperties:
			return createEndPropertiesEvent();
		case EndReference:
			return createEndReferenceEvent();
		case EndResourceFragment:
			return createEndResourceFragmentEvent();
		case EndTextFlow:
			return createEndTextFlowEvent();
		case EndTextFlowContent:
			return createEndTextFlowContentEvent();
		case EndTextFragment:
			return createEndTextFragmentEvent();
		case StartAnnotation:
			return createStartAnnotationEvent();
		case StartChildren:
			return createStartChildrenEvent();
		case StartContainer:
			return createStartContainerEvent();
		case StartContainerFragment:
			return createStartContainerFragmentEvent();
		case StartDataPart:
			return createStartDataPartEvent();
		case StartDocument:
			return createStartDocumentEvent();
		case StartProperties:
			return createStartPropertiesEvent();
		case StartReference:
			return createStartReferenceEvent();
		case StartResourceFragment:
			return createStartResourceFragmentEvent();
		case StartTextFlow:
			return createStartTextFlowEvent();
		case StartTextFlowContent:
			return createStartTextFlowContentEvent();
		case StartTextFragment:
			return createStartTextFragmentEvent();
		default:
			throw new RuntimeException("not implemented");
		}
	}


	public final Event createEndAnnotationEvent() {
		return EmptyEvent.EndAnnotation;
	}

	public final Event createEndChildrenEvent() {
		return EmptyEvent.EndChildren;
	}

	public final Event createEndContainerEvent() {
		return EmptyEvent.EndContainer;
	}

	public final Event createEndContainerFragmentEvent() {
		return EmptyEvent.EndContainerFragment;
	}

	public final Event createEndDataPartEvent() {
		return EmptyEvent.EndDataPart;
	}

	public final Event createEndDocumentEvent() {
		return EmptyEvent.EndDocument;
	}

	public final Event createEndPropertiesEvent() {
		return EmptyEvent.EndProperties;
	}

	public final Event createEndReferenceEvent() {
		return EmptyEvent.EndReference;
	}

	public final Event createEndResourceFragmentEvent() {
		return EmptyEvent.EndResourceFragment;
	}

	public final Event createEndTextFlowContentEvent() {
		return EmptyEvent.EndTextFlowContent;
	}

	public final Event createEndTextFlowEvent() {
		return EmptyEvent.EndTextFlow;
	}

	public final Event createEndTextFragmentEvent() {
		return EmptyEvent.EndTextFragment;
	}

	public final Event createStartChildrenEvent() {
		return EmptyEvent.StartChildren;
	}

	public final Event createStartPropertiesEvent() {
		return EmptyEvent.StartProperties;
	}

	public final Event createStartTextFlowContentEvent() {
		return EmptyEvent.StartTextFlowContent;
	}

}
