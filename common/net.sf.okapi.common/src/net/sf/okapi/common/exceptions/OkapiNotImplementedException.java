package net.sf.okapi.common.exceptions;

public class OkapiNotImplementedException extends RuntimeException {

	private static final long serialVersionUID = -1943082812163691869L;

	public OkapiNotImplementedException() {
	}

	public OkapiNotImplementedException(String message) {
		super(message);
	}

	public OkapiNotImplementedException(Throwable cause) {
		super(cause);
	}

	public OkapiNotImplementedException(String message, Throwable cause) {
		super(message, cause);	
	}
}
