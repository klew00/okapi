package com.googlecode.okapi.resource;


public class ResourceFactoryImpl implements ResourceFactory{

	private long nextResourceId = 1l;
	private DocumentId docId;
	
	public ResourceFactoryImpl(DocumentId docId) {
		this.docId = docId;
	}

	private PartId nextPartId(){
		return new PartId( String.valueOf(nextResourceId++) );
	}
	
	private ContentId nextContentId(){
		return new ContentId( String.valueOf(nextResourceId++) );
	}
	
	public TextFlow createTextFlow() {
		return new TextFlowImpl( nextPartId());
	}

	public Container createContainer() {
		return new ContainerImpl( nextPartId());
	}

	public DataPart createDataPart() {
		return new DataPartImpl( nextPartId());
	}

	public Reference createReference() {
		return new ReferenceImpl( nextPartId());
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
		return new DocumentImpl(docId);
	}

	

}
