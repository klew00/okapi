package com.googlecode.okapi.resource;

import java.util.List;

public class ContainerFragment extends ContentFragmentImpl implements ContentProvider{
	
	private List<ContentFragment> content;
	private String id;
	
	public String getId() {
		return id;
	}
	
	public List<ContentFragment> getContent() {
		return content;
	}
	
}
