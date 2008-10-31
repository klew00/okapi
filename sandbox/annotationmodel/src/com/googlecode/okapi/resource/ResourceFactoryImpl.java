package com.googlecode.okapi.resource;


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

	public TextFragmentImpl createTextFragment() {
		return new TextFragmentImpl( nextContentId() );
	}

	public ContainerFragmentImpl createContainerFragment() {
		return new ContainerFragmentImpl( nextContentId() );
	}

	public ResourceFragmentImpl createResourceFragment() {
		return new ResourceFragmentImpl( nextContentId() );
	}

	public Document createDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
