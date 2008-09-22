package net.sf.okapi.common.pipeline2;

import java.util.HashMap;

public interface IPipelineEvent {
	Enum<?> getEventType();
		
	Object getData();
	
	HashMap<String, Object> getMetadata();
	
	int getOrder();	
}
