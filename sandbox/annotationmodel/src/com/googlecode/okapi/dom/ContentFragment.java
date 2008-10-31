package com.googlecode.okapi.dom;

import com.googlecode.okapi.resource.ContentId;
import com.googlecode.okapi.resource.PartId;


public abstract interface ContentFragment extends Resource<ContentId>{

	public PartId getPart();
	public void setPart(PartId partId);

}
