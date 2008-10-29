package net.sf.okapi.common.threadedpipeline;

public class RuntimeInterruptedException extends RuntimeException {	
	private static final long serialVersionUID = 8175096454212744354L;

	public RuntimeInterruptedException(InterruptedException e) {
		super(e);
	}

}
