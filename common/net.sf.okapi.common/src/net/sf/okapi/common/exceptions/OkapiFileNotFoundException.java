package net.sf.okapi.common.exceptions;

public class OkapiFileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3560815361871071813L;

	public OkapiFileNotFoundException() {
	}

	public OkapiFileNotFoundException(String message) {
		super(message);
	}

	public OkapiFileNotFoundException(Throwable cause) {
		super(cause);
	}

	public OkapiFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
