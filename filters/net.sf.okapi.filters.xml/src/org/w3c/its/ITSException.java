package org.w3c.its;

/**
 * Indicates an error while processing ITS constructs.
*/
public class ITSException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception with a given text.
	 * @param text Text to go with the new exception.
	 */
	public ITSException (String text) {
		super(text);
	}
	
	/**
	 * Creates a new exception with a given parent exception.
	 * @param e The parent exception.
	 */
	public ITSException (Throwable e) {
		super(e);
	}

}
