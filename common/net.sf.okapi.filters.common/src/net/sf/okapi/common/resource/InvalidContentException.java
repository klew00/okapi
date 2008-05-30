package net.sf.okapi.common.resource;

public class InvalidContentException extends RuntimeException {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 1L;

	public InvalidContentException (String text) {
		super(text);
	}
	
	public InvalidContentException (Throwable e) {
		super(e);
	}
}
