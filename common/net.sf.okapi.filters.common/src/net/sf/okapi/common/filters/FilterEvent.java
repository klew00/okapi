package net.sf.okapi.common.filters;

import net.sf.okapi.common.resource.IResource;

public class FilterEvent {
	public static enum FilterEventType {
		START, START_DOCUMENT, END_DOCUMENT, START_SUBDOCUMENT, END_SUBDOCUMENT, START_GROUP, END_GROUP, TEXT_UNIT, TEXT_GROUP, SKELETON_UNIT, FINISHED
	};
	
	private Enum<?> filterEventType;
	private IResource resource; // TextUnit, Skeleton, Group or other data object
	
	public FilterEvent(Enum<?> filterEventType, IResource resource) {
		this.filterEventType = filterEventType;
		this.resource = resource;
	}	
	
	public Enum<?> getFilterEventType() {
		return filterEventType;
	}
	
	public IResource getFilterResource() {
		return resource;
	}
}
