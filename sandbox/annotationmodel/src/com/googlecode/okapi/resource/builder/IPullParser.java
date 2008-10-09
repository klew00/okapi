package com.googlecode.okapi.resource.builder;

public interface IPullParser<T> {
	public boolean hasNext();
	public T next();
	public void close();
}
