package com.googlecode.okapi.pipeline;

public abstract class AbstractPipelineStep<T> implements IInputProcessor<T>{
	
	private IPullParser<T> input;
	
	public AbstractPipelineStep(IPullParser<T> input){
		this.input = input;
	}
	
	public AbstractPipelineStep(){}
	
	public final boolean hasNext() {
		return input.hasNext();
	}
	
	public final T next() {
		T event = input.next();
		handleEvent(event);
		return event;
	}
	
	public abstract void handleEvent(T event);
	
	public final void close() {
		input.close();
	}

	public void setInput(IPullParser<T> input){
		this.input = input;
	}
}
