package net.sf.okapi.apptest.dummyutility;

import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.apptest.utilities.IUtility;

public class PseudoTranslate implements IUtility {

	public String getName () {
		return "PseudoTranlate";
	}
	
	public void handleEvent (FilterEvent event) {
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
		TextUnit trgTu = (TextUnit)tu.getAnnotation("fr");
		TextFragment tf = null;
		if ( trgTu != null ) {
			tf = ((TextUnit)tu.getAnnotation("fr")).getContent();
		}
		if ( trgTu == null ) {
			tf = tu.getContent().clone();
			trgTu = new TextUnit();
			trgTu.setContent(tf);
			tu.setAnnotation("fr", trgTu);
		}
		tf.setCodedText(
			tu.getContent().getCodedText().replace("e", "\u00CA"));
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
