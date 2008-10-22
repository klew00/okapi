package net.sf.okapi.common.filters;

public class FilterEvent {
	public static enum FilterEventType {
		START_DOCUMENT, END_DOCUMENT, START_SUBDOCUMENT, END_SUBDOCUMENT, START_GROUP, END_GROUP, TEXT_UNIT, TEXT_GROUP, SKELETON_UNIT
	};
	
	private Enum<?> filterEventType;
	private Object resource; // TextUnit, Skeleton, Group or other data object
	
	public FilterEvent(Enum<?> filterEventType, Object resource) {
		this.filterEventType = filterEventType;
		this.resource = resource;
	}	
	
	public Enum<?> getFilterEventType() {
		return filterEventType;
	}
	
	public Object getFilterResource() {
		return resource;
	}
}
