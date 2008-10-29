package net.sf.okapi.common.threadedpipeline;

public class RuntimeInterruptedException extends RuntimeException {

	public RuntimeInterruptedException(InterruptedException e) {
		super(e);
	}

}
