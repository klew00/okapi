package net.sf.okapi.common.exceptions;

public class OkapiMisAlignmentException extends RuntimeException {
	private static final long serialVersionUID = 5096971342499232176L;

	/**
	 * Creates an empty new OkapiEditorCreationException object.
	 */
	public OkapiMisAlignmentException() {
		super();
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given message.
	 * 
	 * @param message
	 *            text of the message.
	 */
	public OkapiMisAlignmentException(String message) {
		super(message);
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given parent
	 * exception cause.
	 * 
	 * @param cause
	 *            the parent exception cause.
	 */
	public OkapiMisAlignmentException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given message
	 * and a given parent exception cause.
	 * 
	 * @param message
	 *            the message.
	 * @param cause
	 *            the cause.
	 */
	public OkapiMisAlignmentException(String message, Throwable cause) {
		super(message, cause);
	}
}
