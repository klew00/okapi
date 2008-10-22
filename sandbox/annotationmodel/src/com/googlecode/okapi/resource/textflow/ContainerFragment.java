package com.googlecode.okapi.resource.textflow;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.okapi.resource.TextFlowProvider;

public final class ContainerFragment extends ContentFragmentImpl implements TextFlowProvider{
	
	private List<ContentFragment> content;
	private String id;
	
	public ContainerFragment(ContentId id) {
		super(id);
	}
	
	public List<ContentFragment> getFlow() {
		if(content == null){
			content = new ArrayList<ContentFragment>();
		}
		return content;
	}
	
}
