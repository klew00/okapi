package net.sf.okapi.common.pipeline2;

import java.util.concurrent.ConcurrentHashMap;

public class PipelineEvent implements IPipelineEvent {

	public static enum PipelineEventType {
		START_RESOURCE, END_RESOURCE, TEXTUNIT, SKELETON, START_GROUP, END_GROUP, START, FINISHED
	};

	private final int order;
	private final PipelineEventType pipelineEventType;
	private final Object data; // TextUnit, Skeleton, Group or other data object
	private final ConcurrentHashMap<String, Object> metadata; // annotations
	
	public PipelineEvent(PipelineEventType pipelineEventType, Object data, int order) {
		this.pipelineEventType = pipelineEventType;
		this.data = data;
		this.order = order;
		this.metadata = new ConcurrentHashMap<String, Object>();
	}
	
	public Object getData() {	
		return data;
	}
	
	public Enum<?> getEventType() {		
		return pipelineEventType;
	}
	
	public ConcurrentHashMap<String, Object> getMetadata() {		
		return metadata;
	}
	
	public int getOrder() {
		return order;
	}	
}
