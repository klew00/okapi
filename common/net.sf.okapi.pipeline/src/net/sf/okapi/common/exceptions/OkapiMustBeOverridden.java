package net.sf.okapi.common.exceptions;

public class OkapiMustBeOverridden extends RuntimeException {	
	private static final long serialVersionUID = 1900422196037624022L;

	public OkapiMustBeOverridden(String message) {
		super(message);
	}

	public OkapiMustBeOverridden(Throwable cause) {
		super(cause);
	}

	public OkapiMustBeOverridden(String message, Throwable cause) {
		super(message, cause);
	}
}
