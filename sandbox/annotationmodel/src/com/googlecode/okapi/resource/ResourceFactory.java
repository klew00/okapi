package com.googlecode.okapi.resource;

import com.googlecode.okapi.resource.textflow.ContainerFragment;
import com.googlecode.okapi.resource.textflow.ResourceFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

public interface ResourceFactory {
	
	//public Document createDocument();
	
	public Container createContainer();
	public DataPart createDataPart();
	public TextFlow createTextFlow();
	public Reference createReference();
	
	public ContainerFragment createContainerFragment();
	public ResourceFragment createResourceFragment();
	public TextFragment createTextFragment();
	
}
