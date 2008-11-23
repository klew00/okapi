package net.sf.okapi.apptest.dummyutility;

import net.sf.okapi.apptest.annotation.TargetsAnnotation;
import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.apptest.utilities.IUtility;

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
		
		//TODO: add annotation for properties
	}
	
	private void processTU (TextUnit tu) {
		TargetsAnnotation ta = tu.getAnnotation(TargetsAnnotation.class);
		TextUnit trgTu = null;
		TextFragment tf = null;
		if ( ta != null ) {
			trgTu = ta.get(trgLang);
			if ( trgTu != null ) {
				tf = trgTu.getContent();
			}
		}
		if ( trgTu == null ) {
			tf = tu.getContent().clone();
			trgTu = new TextUnit();
			trgTu.setContent(tf);
			if ( ta == null ) {
				ta = new TargetsAnnotation();
				tu.setAnnotation(ta);
			}
			ta.set(trgLang, trgTu);
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
