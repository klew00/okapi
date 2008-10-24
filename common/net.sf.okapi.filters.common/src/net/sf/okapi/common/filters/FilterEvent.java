package net.sf.okapi.common.filters;

import net.sf.okapi.common.resource.IResource;

public class FilterEvent {
	
	private FilterEventType filterEventType;
	private IResource resource; // TextUnit, Skeleton, Group or other data object
	
	public FilterEvent(FilterEventType filterEventType, IResource resource) {
		this.filterEventType = filterEventType;
		this.resource = resource;
	}	
	
	public IResource getData() {
		return resource;
	}

	public FilterEventType getEventType() {
		return filterEventType;
	}
}
