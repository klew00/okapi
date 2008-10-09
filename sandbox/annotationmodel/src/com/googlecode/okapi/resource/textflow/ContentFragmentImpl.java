package com.googlecode.okapi.resource.textflow;

import com.googlecode.okapi.resource.PartId;

abstract class ContentFragmentImpl implements ContentFragment {

	private PartId part;
	private ContentId id;
	
	public ContentFragmentImpl(ContentId id) {
		this.id = id;
	}
	
	public ContentId getId() {
		return id;
	}
	
	public PartId getPart() {
		return part;
	}

	public void setPart(PartId part) {
		this.part = part;
	}
	
	public <A> A getAdapter(Class<A> adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
