package com.googlecode.okapi.pipeline;

import java.util.ArrayList;
import java.util.List;

public class PipelineDriver<T> {

	private IPullParser<T> input;
	List<IInputProcessor<T>> steps;
	
	public PipelineDriver() {
		steps = new ArrayList<IInputProcessor<T>>();
	}

	public void setInput(IPullParser<T> input) {
		this.input = input;
	}
	
	public void addStep(IInputProcessor<T> input){
		steps.add(input);
	}
	
	public void run(){
		IPullParser<T> previous = input;
		for(IInputProcessor<T> processor : steps){
			processor.setInput(previous);
			previous = processor;
		}
		
		while(previous.hasNext()){
			previous.next();
		}
		previous.close();
	}
	
	
}
