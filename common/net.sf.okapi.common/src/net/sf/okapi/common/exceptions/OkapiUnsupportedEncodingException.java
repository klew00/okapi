package net.sf.okapi.common.exceptions;

public class OkapiUnsupportedEncodingException extends RuntimeException {
	
	private static final long serialVersionUID = -4301626737744375525L;

	public OkapiUnsupportedEncodingException(String message) {
		super(message);
	}

	public OkapiUnsupportedEncodingException(Throwable cause) {
		super(cause);
	}

	public OkapiUnsupportedEncodingException(String message, Throwable cause) {
		super(message, cause);
	}
}
