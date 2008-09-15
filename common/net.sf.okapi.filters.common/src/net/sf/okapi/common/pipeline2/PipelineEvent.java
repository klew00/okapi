/**
 * 
 */
package net.sf.okapi.common.pipeline2;

import java.util.Hashtable;

/**
 * @author HargraveJE
 *
 */
public class PipelineEvent {
	
	public static enum PipelineEventType {START_RESOURCE, END_RESOURCE, TEXTUNIT, SKELETON, START_GROUP, END_GROUP, FINISHED};
	
	public int order;
	public PipelineEventType pipelineEvent; 
	public Object data; // TextUnit, Skeleton, Group or other data object
	public Hashtable<String, Object> metadata;  // annotations
}
