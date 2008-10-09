package com.googlecode.okapi.resource;


public interface TextFlow extends DocumentPart, TextFlowProvider{

	public static enum Unit{
		Paragraph,
		Sentence,
		Other
	};
	
	public boolean isStandalone();

	public void setStandalone(boolean standalone);
	
	public Unit getUnit();
	
	public void setUnit(Unit unit);
	
}
