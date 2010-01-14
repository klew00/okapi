package net.sf.okapi.common.exceptions;

@SuppressWarnings("serial")
public class OkapiUnexpectedResourceTypeException extends RuntimeException {
	/**
	 * Creates an empty new OkapiEditorCreationException object.
	 */
	public OkapiUnexpectedResourceTypeException () {
		super();
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given message.
	 * @param message text of the message.
	 */
	public OkapiUnexpectedResourceTypeException (String message) {
		super(message);
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given parent 
	 * exception cause.
	 * @param cause the parent exception cause.
	 */
	public OkapiUnexpectedResourceTypeException (Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given message and 
	 * a given parent exception cause.
	 * @param message the message.
	 * @param cause the cause.
	 */
	public OkapiUnexpectedResourceTypeException (String message, Throwable cause) {
		super(message, cause);
	}
}
