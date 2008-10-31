package com.googlecode.okapi.dom;

import java.util.List;

public interface ContainerFragment extends ContentFragment, TextFlowProvider{
	public List<ContentFragment> getFlow();
}
