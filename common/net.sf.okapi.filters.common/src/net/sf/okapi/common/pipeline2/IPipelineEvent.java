package net.sf.okapi.common.pipeline2;

import java.util.concurrent.ConcurrentHashMap;

public interface IPipelineEvent {
	Enum<?> getEventType();
		
	Object getData();
	
	ConcurrentHashMap<String, Object> getMetadata();
	
	int getOrder();	
}
