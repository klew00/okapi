package net.sf.okapi.apptest.dummyutility;

import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.apptest.utilities.IUtility;

public class PseudoTranslate implements IUtility {

	public String getName () {
		return "PseudoTranlate";
	}
	
	public void handleEvent (FilterEvent event)
	{
		switch ( event.getEventType() ) {
		case TEXT_UNIT:
			processTU((TextUnit)event.getResource());
			break;
		case START_DOCUMENT:
			break;
		case END_DOCUMENT:
			break;
		}
	}
	
	private void processTU (TextUnit tu) {
		if ( !tu.isTranslatable() ) return;
		if ( !tu.hasTarget() ) {
			tu.setTargetContent(tu.getSourceContent().clone());
		}
		tu.getTargetContent().setCodedText(
			tu.getTargetContent().getCodedText().replace("e", "Z"));
	}

	public void doEpilog () {
		// Nothing to do in this utility
		System.out.println("PseudoTranlate: doEpilog() called");
	}

	public void doProlog () {
		// Nothing to do in this utility
		System.out.println("PseudoTranlate: doProlog() called");
	}

}
