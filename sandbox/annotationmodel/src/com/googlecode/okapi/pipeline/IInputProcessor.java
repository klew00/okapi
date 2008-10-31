package com.googlecode.okapi.pipeline;

public interface IInputProcessor<T> extends IPullParser<T>{

	public void setInput(IPullParser<T> input);

}
