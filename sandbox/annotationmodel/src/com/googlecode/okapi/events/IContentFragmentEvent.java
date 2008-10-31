package com.googlecode.okapi.events;

import com.googlecode.okapi.resource.ContentId;

public interface IContentFragmentEvent extends Event{

	public ContentId getId();

	
}
