package com.googlecode.okapi.resource;

import java.util.List;

public interface ContainerFragment extends ContentFragment, TextFlowProvider{
	public List<ContentFragment> getFlow();
}
