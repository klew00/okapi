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
			TextUnit tu1 = (TextUnit)event.getResource();
			processTU(tu1);
			if ( tu1.hasChild() ) {
				for ( TextUnit tu : tu1.childTextUnitIterator() ) {
					processTU(tu);
				}
			}
			break;
		case START_DOCUMENT:
			break;
		case END_DOCUMENT:
			break;
		}
	}
	
	private void processTU (TextUnit tu) {
		tu.getSourceContent().setCodedText(
			tu.getSourceContent().getCodedText().replace("e", "X"));
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
