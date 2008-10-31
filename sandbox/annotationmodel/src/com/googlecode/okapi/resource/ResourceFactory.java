package com.googlecode.okapi.resource;


public interface ResourceFactory {
	
	public Document createDocument();
	
	public Container createContainer();
	public DataPart createDataPart();
	public TextFlow createTextFlow();
	public Reference createReference();
	
	public ContainerFragment createContainerFragment();
	public ResourceFragment createResourceFragment();
	public TextFragment createTextFragment();

}
