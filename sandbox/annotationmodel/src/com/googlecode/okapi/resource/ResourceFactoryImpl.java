package com.googlecode.okapi.resource;

import com.googlecode.okapi.resource.textflow.ContainerFragment;
import com.googlecode.okapi.resource.textflow.ContentId;
import com.googlecode.okapi.resource.textflow.ResourceFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

public class ResourceFactoryImpl implements ResourceFactory{

	private long nextResourceId = 1l;

	private PartId nextPartId(){
		return new PartId( String.valueOf(nextResourceId++) );
	}
	
	private ContentId nextContentId(){
		return new ContentId( String.valueOf(nextResourceId++) );
	}
	
	public ResourceFactoryImpl() {
	}
	
	public Container createContainer() {
		return new ContainerImpl( nextPartId() );
	}

	public ContainerFragment createContainerFragment() {
		return new ContainerFragment( nextContentId() );
	}

	public DataPart createDataPart() {
		return new DataPartImpl( nextPartId() );
	}

	public Reference createReference() {
		return new ReferenceImpl( nextPartId() );
	}

	public ResourceFragment createResourceFragment() {
		return new ResourceFragment( nextContentId() );
	}

	public TextFlow createTextFlow() {
		return new TextFlowImpl( nextPartId() );
	}

	public TextFragment createTextFragment() {
		return new TextFragment( nextContentId() );
	}
	

}
