package com.googlecode.okapi.resource;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.okapi.resource.textflow.ContentFragment;

final class TextFlowImpl extends DocumentPartImpl implements TextFlow{

	private boolean standalone;
	private Unit unit;

	private List<ContentFragment> content;
	
	public TextFlowImpl(PartId id) {
		super(id);
	}
	
	public boolean isStandalone() {
		return standalone;
	}

	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public List<ContentFragment> getFlow() {
		if(content == null){
			content = new ArrayList<ContentFragment>();
		}
		return content;
	}
	
}
