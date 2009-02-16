package org.w3c.its;

class ITSTrace {
	
	boolean isChildDone;
	boolean translate;
	int dir;
	int withinText;
	boolean term;
	String termInfo;
	String locNote;

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
		
		// locnote: Inheritance for child elements but not attributes
		locNote = initialTrace.locNote;
		
		this.isChildDone = isChildDone;
	}

}
