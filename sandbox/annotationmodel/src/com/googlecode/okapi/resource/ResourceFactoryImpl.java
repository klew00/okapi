package com.googlecode.okapi.resource;

import com.googlecode.okapi.resource.textflow.ContainerFragment;
import com.googlecode.okapi.resource.textflow.ContentId;
import com.googlecode.okapi.resource.textflow.ResourceFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

public class ResourceFactoryImpl implements ResourceFactory{

	private long nextResourceId = 1l;

	DocumentManager documentManager;
	
	public ResourceFactoryImpl(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	private PartId nextPartId(){
		return new PartId( String.valueOf(nextResourceId++) );
	}
	
	private ContentId nextContentId(){
		return new ContentId( String.valueOf(nextResourceId++) );
	}
	
	public TextFlow createTextFlow() {
		return new TextFlowImpl( nextPartId(), documentManager );
	}

	public Container createContainer() {
		return new ContainerImpl( nextPartId(), documentManager );
	}

	public DataPart createDataPart() {
		return new DataPartImpl( nextPartId(), documentManager );
	}

	public Reference createReference() {
		return new ReferenceImpl( nextPartId(), documentManager );
	}

	public TextFragment createTextFragment() {
		return new TextFragment( nextContentId() );
	}

	public ContainerFragment createContainerFragment() {
		return new ContainerFragment( nextContentId() );
	}

	public ResourceFragment createResourceFragment() {
		return new ResourceFragment( nextContentId() );
	}

	

}
