package com.googlecode.okapi.resource;

import java.util.List;

public class TextResource extends Resource implements ContentProvider{
	
	public static enum Unit{
		Paragraph,
		Sentence,
		Other
	};

	private boolean standalone;
	private Unit unit;

	// TODO change to TextContent implements List<CF>
	private List<ContentFragment> content;
	
	
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

	public List<ContentFragment> getContent() {
		return content;
	}
	
	
	
}
