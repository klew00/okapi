package net.sf.okapi.apptest.filters;

import net.sf.okapi.apptest.common.IResource;

public class FilterEvent {
	
	public static enum FilterEventType {
		START, START_DOCUMENT, END_DOCUMENT, START_SUBDOCUMENT, END_SUBDOCUMENT,
		START_GROUP, END_GROUP, TEXT_UNIT, PROPERTIES_UNIT, SKELETON_UNIT, FINISHED
	};
	
	private FilterEventType filterEventType;
	private IResource resource; // TextUnit, Skeleton, Group or other data object
	
	public FilterEvent(FilterEventType filterEventType, IResource resource) {
		this.filterEventType = filterEventType;
		this.resource = resource;
	}	
	
	public FilterEventType getEventType() {
		return filterEventType;
	}
	
	public IResource getResource() {
		return resource;
	}
}
