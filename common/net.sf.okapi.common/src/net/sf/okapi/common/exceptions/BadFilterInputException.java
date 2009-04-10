package net.sf.okapi.common.exceptions;

public class BadFilterInputException extends RuntimeException {

	private static final long serialVersionUID = 1122090108070908960L;

	public BadFilterInputException() {
	}

	public BadFilterInputException(String message) {
		super(message);		
	}

	public BadFilterInputException(Throwable cause) {
		super(cause);
	}

	public BadFilterInputException(String message, Throwable cause) {
		super(message, cause);
	}
}
