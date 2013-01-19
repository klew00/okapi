package net.sf.okapi.common.resource;

/**
 * {@link Segment} alignmment status.
 * 
 * @author HargraveJE
 *
 */
public enum AlignmentStatus {
	// TODO: what other statuses?	
	ALIGNED, // Means aligned via the api so is guaranteed - better name?
	NOT_ALIGNED // we can't depend on the alignment - segments added to both source and target independently.
}
