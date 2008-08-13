package net.sf.okapi.common.resource;

import java.util.List;

public class TextRootContainer extends TextContainer {

	public TextRootContainer (TextUnit parent) {
		setParent(parent);
	}
	
	public List<TextContainer> getSegments () {
		//TODO: getSegments
		return null;
	}
	
	
}
