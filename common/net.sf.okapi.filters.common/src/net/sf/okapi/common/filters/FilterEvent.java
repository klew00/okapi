package net.sf.okapi.common.filters;

import net.sf.okapi.common.resource.IResource;

public class FilterEvent {
	
	private FilterEventType filterEventType;
	private IResource resource;
	
	public FilterEvent (FilterEventType filterEventType,
		IResource resource)
	{
		this.filterEventType = filterEventType;
		this.resource = resource;
	}	
	
	public FilterEvent (FilterEventType filterEventType,
		IResource resource,
		ISkeleton skeleton)
	{
		this.filterEventType = filterEventType;
		this.resource = resource;
		this.resource.setSkeleton(skeleton);
	}	
	
	public FilterEventType getEventType () {
		return filterEventType;
	}
	
	public IResource getResource () {
		return resource;
	}
	
}
