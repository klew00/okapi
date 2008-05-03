package net.sf.okapi.its;

public class ITSTrace {
	boolean             isChildDone;
	boolean             translate;
	int                 dir;

	ITSTrace () {
	}
	
	ITSTrace (ITSTrace initialTrace,
		boolean isChildDone) {
		translate = initialTrace.translate;
		dir = initialTrace.dir;
		this.isChildDone = isChildDone;
	}
}
