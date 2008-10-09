package com.googlecode.okapi.resource;

import java.util.List;

import com.googlecode.okapi.resource.textflow.ContentFragment;

public interface TextFlowProvider {

	public List<ContentFragment> getFlow();
}
