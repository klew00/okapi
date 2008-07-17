package net.sf.okapi.resource;

public class CodedTextException extends Exception {

	public CodedTextException(String message) {
		super(message);
	}
	
	public CodedTextException(Throwable cause) {
		super(cause);
	}
	
	public CodedTextException(String message, Throwable cause) {
		super(message,cause);
	}
}
