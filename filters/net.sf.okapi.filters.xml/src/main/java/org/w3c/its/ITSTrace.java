package org.w3c.its;

class ITSTrace {
	
	boolean isChildDone;
	boolean translate;
	int dir;
	int withinText;
	boolean term;
	String termInfo;
	String locNote;
	boolean preserveWS;
	String language;
	String targetPointer;
	String idPointer;

	ITSTrace () {
	}
	
	ITSTrace (ITSTrace initialTrace,
		boolean isChildDone)
	{
		// translate: Inheritance for child elements but not attributes
		translate = initialTrace.translate;
		
		// dir: Inheritance for child element including attributes
		dir = initialTrace.dir;
		
		// withinText: No inheritance
		
		// term : No inheritance
		
		// target: No inheritance
		
		// locnote: Inheritance for child elements but not attributes
		locNote = initialTrace.locNote;
		
		// preserveWS: Inheritance for child elements but not attributes
		preserveWS = initialTrace.preserveWS;
	
		// language: Inheritance for child element including attributes 
		language = initialTrace.language;
		
		this.isChildDone = isChildDone;
	}

}
