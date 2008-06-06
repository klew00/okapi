package org.w3c.its;

public class ITSTrace {
	boolean             isChildDone;
	boolean             translate;
	int                 dir;
	int                 withinText;

	ITSTrace () {
	}
	
	ITSTrace (ITSTrace initialTrace,
		boolean isChildDone)
	{
		translate = initialTrace.translate;
		dir = initialTrace.dir;
		withinText = initialTrace.withinText;
		this.isChildDone = isChildDone;
	}
}
