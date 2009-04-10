package net.sf.okapi.common.exceptions;

public class OkapiIOException extends RuntimeException {
	
	private static final long serialVersionUID = 1128014152792942325L;
	
	public OkapiIOException(String message) {
		super(message);
	}
	
	public OkapiIOException(Exception e) {
		super(e);
	}
}
