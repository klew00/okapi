package com.googlecode.okapi.resource.pipeline;

import com.googlecode.okapi.pipeline.AbstractPipelineStep;
import com.googlecode.okapi.pipeline.IPullParser;
import com.googlecode.okapi.resource.AnnotationDataEvent;
import com.googlecode.okapi.resource.AnnotationEvent;
import com.googlecode.okapi.resource.ContainerEvent;
import com.googlecode.okapi.resource.ContainerFragmentEvent;
import com.googlecode.okapi.resource.DataPartEvent;
import com.googlecode.okapi.resource.DocumentEvent;
import com.googlecode.okapi.resource.Event;
import com.googlecode.okapi.resource.IContentFragmentEvent;
import com.googlecode.okapi.resource.IDocumentPartEvent;
import com.googlecode.okapi.resource.ReferenceEvent;
import com.googlecode.okapi.resource.ResourceFragmentEvent;
import com.googlecode.okapi.resource.TextFlowEvent;
import com.googlecode.okapi.resource.TextFragmentEvent;

public abstract class BaseEventHandler extends AbstractPipelineStep<Event>{
	
	public BaseEventHandler(IPullParser<Event> input) {
		super(input);
	}
	
	public BaseEventHandler() {
	}
	
	
	@Override
	public void handleEvent(Event event) {
		switch(event.getEventType()){
		case EndAnnotation:
			handleEndAnnotation();
			break;
		case EndDocument:
			handleEndDocument();
			break;
		case StartProperties:
			handleStartProperties();
			break;
		case EndProperties:
			handleEndProperties();
			break;
		case StartTextFlowContent:
			handleStartTextFlowContent();
			break;
		case EndTextFlowContent:
			handleEndTextFlowContent();
			break;
		case StartChildren:
			handleStartChildren();
			break;
		case EndChildren:
			handleEndChildren();
			break;
		case EndContainer:
			handleEndDocumentPart();
			handleEndContainer();
			break;
		case EndDataPart:
			handleEndDocumentPart();
			handleEndDataPart();
			break;
		case EndReference:
			handleEndDocumentPart();
			handleEndReference();
			break;
		case EndTextFlow:
			handleEndDocumentPart();
			handleEndTextFlow();
			break;
		case EndContainerFragment:
			handleEndContentFragment();
			handleEndContainerFragment();
			break;
		case EndResourceFragment:
			handleEndContentFragment();
			handleEndResourceFragment();
			break;
		case EndTextFragment:
			handleEndContentFragment();
			handleEndTextFragment();
			break;
		case StartAnnotation:
			handleStartAnnotation( (AnnotationEvent) event);
		case StartDocument:
			handleStartDocument( (DocumentEvent) event );
			break;
		case StartTextFlow:
			handleStartDocumentPart( (IDocumentPartEvent) event );
			handleStartTextFlow( (TextFlowEvent) event );
			break;
		case StartContainer:
			handleStartDocumentPart( (IDocumentPartEvent) event );
			handleStartContainer( (ContainerEvent) event );
			break;
		case StartDataPart:
			handleStartDocumentPart( (IDocumentPartEvent) event );
			handleStartDataPart( (DataPartEvent) event );
			break;
		case StartReference:
			handleStartDocumentPart( (IDocumentPartEvent) event );
			handleStartReference( (ReferenceEvent) event );
			break;
		case StartResourceFragment:
			handleStartContentFragment( (IContentFragmentEvent) event );
			handlStartResourceFragment( (ResourceFragmentEvent) event );
			break;
		case StartContainerFragment:
			handleStartContentFragment( (IContentFragmentEvent) event );
			handleStartContainerFragment( (ContainerFragmentEvent) event );
			break;
		case StartTextFragment:
			handleStartContentFragment( (IContentFragmentEvent) event );
			handleStartTextFragment( (TextFragmentEvent) event );
			break;
		case AnnotationData:
			handleAnnotationData( (AnnotationDataEvent) event);
		}
	}

	protected void handleStartDocument(DocumentEvent document) {}
	protected void handleEndDocument() {}

	protected void handleStartAnnotation(AnnotationEvent annotation) {}
	protected void handleAnnotationData(AnnotationDataEvent annotationData) {}
	protected void handleEndAnnotation() {}

	// called before the specific part...
	protected void handleStartDocumentPart(IDocumentPartEvent part) {}
	protected void handleEndDocumentPart() {} // TODO: could pass the EventType here

	// Document Parts
	
	protected void handleStartReference(ReferenceEvent reference) {}
	protected void handleEndReference() {}

	protected void handleStartDataPart(DataPartEvent dataPart) {}
	protected void handleEndDataPart() {}

	protected void handleStartContainer(ContainerEvent container) {}
	protected void handleEndContainer() {}

	protected void handleStartTextFlow(TextFlowEvent textFlow) {}
	protected void handleEndTextFlow() {}

	protected void handleStartTextFlowContent() {}
	protected void handleEndTextFlowContent() {}

	protected void handleStartChildren() {}
	protected void handleEndChildren() {}

	protected void handleStartProperties() {}
	protected void handleEndProperties() {}

	// fragments
	
	protected void handleStartContentFragment(IContentFragmentEvent contentFragment) {}
	protected void handleEndContentFragment() {}

	protected void handleStartTextFragment(TextFragmentEvent textFragment) {}
	protected void handleEndTextFragment() {}

	protected void handleStartContainerFragment(ContainerFragmentEvent containerFragment) {}
	protected void handleEndContainerFragment() {}

	protected void handlStartResourceFragment(ResourceFragmentEvent resourceFragment) {}
	protected void handleEndResourceFragment() {}
	
}
