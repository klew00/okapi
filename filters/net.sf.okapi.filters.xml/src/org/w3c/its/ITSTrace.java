package org.w3c.its;

class ITSTrace {
	
	boolean isChildDone;
	boolean translate;
	int dir;
	int withinText;
	boolean term;

	ITSTrace () {
	}
	
	ITSTrace (ITSTrace initialTrace,
		boolean isChildDone)
	{
		translate = initialTrace.translate;
		dir = initialTrace.dir;
		withinText = initialTrace.withinText;
		term = initialTrace.term;
		this.isChildDone = isChildDone;
	}

}
