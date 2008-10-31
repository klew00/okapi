package com.googlecode.okapi.filter.po;

import com.googlecode.okapi.base.annotation.IAnnotation;
import com.googlecode.okapi.dom.Container;
import com.googlecode.okapi.dom.ContainerFragment;
import com.googlecode.okapi.dom.ContentFragment;
import com.googlecode.okapi.dom.DataPart;
import com.googlecode.okapi.dom.Document;
import com.googlecode.okapi.dom.DocumentPart;
import com.googlecode.okapi.dom.Reference;
import com.googlecode.okapi.dom.ResourceFragmentImpl;
import com.googlecode.okapi.dom.TextFlow;
import com.googlecode.okapi.dom.TextFragmentImpl;
import com.googlecode.okapi.resource.pipeline.BaseEventHandler;

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
