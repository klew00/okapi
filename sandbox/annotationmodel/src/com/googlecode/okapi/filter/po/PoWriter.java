package com.googlecode.okapi.filter.po;

import com.googlecode.okapi.base.annotation.IAnnotation;
import com.googlecode.okapi.pipeline.BaseEventHandler;
import com.googlecode.okapi.resource.Container;
import com.googlecode.okapi.resource.ContainerFragment;
import com.googlecode.okapi.resource.ContentFragment;
import com.googlecode.okapi.resource.DataPart;
import com.googlecode.okapi.resource.Document;
import com.googlecode.okapi.resource.DocumentPart;
import com.googlecode.okapi.resource.Reference;
import com.googlecode.okapi.resource.ResourceFragmentImpl;
import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.TextFragmentImpl;

public class PoWriter extends BaseEventHandler{

	public PoWriter() {
		super();
		
		// combine identical units?
	}
	
	protected void handleStartDocument(Document document) {
		// write header
	}
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
	protected void handleEndTextFlow(TextFlow textFlow) {
		// write PO entry
	}

	protected void handleStartTextFlowContent() {}
	protected void handleEndTextFlowContent() {
		// write content
	}

	protected void handleStartChildren() {}
	protected void handleEndChildren() {}

	protected void handleStartProperties() {}
	protected void handleEndProperties() {}

	// fragments
	
	protected void handleStartContentFragment(ContentFragment contentFragment) {}
	protected void handleEndContentFragment(ContentFragment contentFragment) {}

	protected void handleStartTextFragment(TextFragmentImpl textFragment) {}
	protected void handleEndTextFragment(TextFragmentImpl textFragment) {}

	protected void handleStartContainerFragment(ContainerFragment containerFragment) {}
	protected void handleEndContainerFragment(ContainerFragment containerFragment) {}

	protected void handlStartResourceFragment(ResourceFragmentImpl resourceFragment) {}
	protected void handleEndResourceFragment(ResourceFragmentImpl resourceFragment) {}
	
}
