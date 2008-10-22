package com.googlecode.okapi.pipeline;

import java.util.ArrayList;
import java.util.List;


public class PipelineDriver {

	private IDocumentParser input;
	List<IResourceProcessor> steps;
	
	public PipelineDriver() {
		steps = new ArrayList<IResourceProcessor>();
	}

	public void setInput(IDocumentParser input) {
		this.input = input;
	}
	
	public void addStep(IResourceProcessor input){
		steps.add(input);
	}
	
	public void run(){
		IDocumentParser previous = input;
		for(IResourceProcessor processor : steps){
			processor.setInput(previous);
			previous = processor;
		}
		
		while(previous.hasNext()){
			previous.next();
		}
		previous.close();
	}
	
	
}
