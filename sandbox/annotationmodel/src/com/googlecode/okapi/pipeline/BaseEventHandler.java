package com.googlecode.okapi.pipeline;

import com.googlecode.okapi.base.annotation.IAnnotation;
import com.googlecode.okapi.resource.Container;
import com.googlecode.okapi.resource.DataPart;
import com.googlecode.okapi.resource.Document;
import com.googlecode.okapi.resource.DocumentPart;
import com.googlecode.okapi.resource.Reference;
import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.textflow.ContainerFragment;
import com.googlecode.okapi.resource.textflow.ContentFragment;
import com.googlecode.okapi.resource.textflow.ResourceFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

public abstract class BaseEventHandler extends AbstractPipelineStep{
	
	@Override
	public final void handleEvent(ResourceEvent event) {
		switch(event.type){
		case Annotation:
			handleAnnotation( (IAnnotation<?>) event.value );
			break;
		case EndDocument:
			handleEndDocument( (Document) event.value );
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
			handleEndDocumentPart( (DocumentPart) event.value );
			handleEndContainer( (Container) event.value );
			break;
		case EndDataPart:
			handleEndDocumentPart( (DocumentPart) event.value );
			handleEndDataPart( (DataPart) event.value );
			break;
		case EndReference:
			handleEndDocumentPart( (DocumentPart) event.value );
			handleEndReference( (Reference) event.value );
			break;
		case EndTextFlow:
			handleEndDocumentPart( (DocumentPart) event.value );
			handleEndTextFlow( (TextFlow) event.value );
			break;
		case EndContainerFragment:
			handleEndContentFragment( (ContentFragment) event.value );
			handleEndContainerFragment( (ContainerFragment) event.value );
			break;
		case EndResourceFragment:
			handleEndContentFragment( (ContentFragment) event.value );
			handleEndResourceFragment( (ResourceFragment) event.value );
			break;
		case EndTextFragment:
			handleEndContentFragment( (ContentFragment) event.value );
			handleEndTextFragment( (TextFragment) event.value );
			break;
		case StartDocument:
			handleStartDocument( (Document) event.value );
			break;
		case StartTextFlow:
			handleStartDocumentPart( (DocumentPart) event.value );
			handleStartTextFlow( (TextFlow) event.value );
			break;
		case StartContainer:
			handleStartDocumentPart( (DocumentPart) event.value );
			handleStartContainer( (Container) event.value );
			break;
		case StartDataPart:
			handleStartDocumentPart( (DocumentPart) event.value );
			handleStartDataPart( (DataPart) event.value );
			break;
		case StartReference:
			handleStartDocumentPart( (DocumentPart) event.value );
			handleStartReference( (Reference) event.value );
			break;
		case StartResourceFragment:
			handleStartContentFragment( (ContentFragment) event.value );
			handlStartResourceFragment( (ResourceFragment) event.value );
			break;
		case StartContainerFragment:
			handleStartContentFragment( (ContentFragment) event.value );
			handleStartContainerFragment( (ContainerFragment) event.value );
			break;
		case StartTextFragment:
			handleStartContentFragment( (ContentFragment) event.value );
			handleStartTextFragment( (TextFragment) event.value );
			break;
		}
	}

	protected void handleStartDocument(Document document) {}
	protected void handleEndDocument(Document document) {}

	protected void handleAnnotation(IAnnotation<?> annotation) {}

	// called before the specific part...
	protected void handleStartDocumentPart(DocumentPart part) {}
	protected void handleEndDocumentPart(DocumentPart part) {}

	// Document Parts
	
	protected void handleStartReference(Reference reference) {}
	protected void handleEndReference(Reference reference) {}

	protected void handleStartDataPart(DataPart dataPart) {}
	protected void handleEndDataPart(DataPart dataPart) {}

	protected void handleStartContainer(Container container) {}
	protected void handleEndContainer(Container container) {}

	protected void handleStartTextFlow(TextFlow textFlow) {}
	protected void handleEndTextFlow(TextFlow textFlow) {}

	protected void handleStartTextFlowContent() {}
	protected void handleEndTextFlowContent() {}

	protected void handleStartChildren() {}
	protected void handleEndChildren() {}

	protected void handleStartProperties() {}
	protected void handleEndProperties() {}

	// fragments
	
	protected void handleStartContentFragment(ContentFragment contentFragment) {}
	protected void handleEndContentFragment(ContentFragment contentFragment) {}

	protected void handleStartTextFragment(TextFragment textFragment) {}
	protected void handleEndTextFragment(TextFragment textFragment) {}

	protected void handleStartContainerFragment(ContainerFragment containerFragment) {}
	protected void handleEndContainerFragment(ContainerFragment containerFragment) {}

	protected void handlStartResourceFragment(ResourceFragment resourceFragment) {}
	protected void handleEndResourceFragment(ResourceFragment resourceFragment) {}
	
}
