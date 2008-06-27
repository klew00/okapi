package net.sf.okapi.lib.segmentation;

public class SegmentationRuleException extends RuntimeException {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 1L;
	
	public SegmentationRuleException (String text) {
		super(text);
	}
	
	public SegmentationRuleException (Throwable e) {
		super(e);
	}

}
