package net.sf.okapi.apptest.dummyutility;

import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.apptest.utilities.IUtility;
import net.sf.okapi.common.filters.FilterEventType;

public class PseudoTranslate implements IUtility {

	private String trgLang;
	
	public String getName () {
		return "PseudoTranlate";
	}
	
	public void handleEvent (FilterEvent event) {
		switch ( event.getEventType() ) {
		case TEXT_UNIT:
			processTU((TextUnit)event.getResource());
			// Fall thru
		case DOCUMENT_PART:
		case START_DOCUMENT:
		case START_SUBDOCUMENT:
		case END_GROUP:
			processProperties((INameable)event.getResource());
		}
	}

	private void processProperties (INameable resource) {
		Property prop = resource.getProperty("href");
		if ( prop == null ) return; // Nothing to do
		if ( !prop.isWriteable() ) return; // Can't modify it
		// Else: localize the href value
		
		
		
	}
	
	private void processTU (TextUnit tu) {
		TextUnit trgTu = (TextUnit)tu.getAnnotation(trgLang);
		TextFragment tf = null;
		if ( trgTu != null ) {
			tf = ((TextUnit)tu.getAnnotation(trgLang)).getContent();
		}
		if ( trgTu == null ) {
			tf = tu.getContent().clone();
			trgTu = new TextUnit();
			trgTu.setContent(tf);
			tu.setAnnotation(trgLang, trgTu);
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

	public void setOptions (String targetLanguage) {
		trgLang = targetLanguage;
	}

}
