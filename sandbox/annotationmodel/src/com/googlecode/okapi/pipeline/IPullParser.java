package com.googlecode.okapi.pipeline;

public interface IPullParser<T> {
	public boolean hasNext();
	public T next();
	public void close();
}
