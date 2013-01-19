package net.sf.okapi.common;

public class SynchronizedValue<T> {
	private T value;
	
	public SynchronizedValue(T initialValue) {
		super();
		set(initialValue);
	}
	
	public synchronized T set(T value) {
		this.value = value;
		return value;
	}
	
	public synchronized T get() {
		return this.value;
	}
}
