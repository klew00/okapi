package com.googlecode.okapi.resource.textflow;

import com.googlecode.okapi.resource.PartId;
import com.googlecode.okapi.resource.Resource;

public abstract interface ContentFragment extends Resource<ContentId>{

	public PartId getPart();
	public void setPart(PartId partId);

}
