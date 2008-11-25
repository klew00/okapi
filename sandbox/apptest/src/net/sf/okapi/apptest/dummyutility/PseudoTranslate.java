package net.sf.okapi.apptest.dummyutility;

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
		case START_GROUP:
			processProperties((INameable)event.getResource());
		}
	}

	private void processProperties (INameable resource) {
		Property prop = resource.getProperty("href");
		if ( prop == null ) return; // Nothing to do
		if ( !prop.isWriteable() ) return; // Can't modify it
		
		// Else: localize the href value
		Property trgProp = (Property)prop.getAnnotation(trgLang);
		if ( trgProp == null ) {
			trgProp = new Property(prop.getName(), "", prop.isWriteable());
			prop.setAnnotation(trgLang, trgProp);
		}
		trgProp.setValue(trgLang+"_"+prop.getValue());
	}
	
	private void processTU (TextUnit tu) {
		//if ( !tu.hasTarget(trgLang) ) {
		// Translate even if we have a target, just to check we use the right TU
			TextUnit trgTu = tu.getTarget(trgLang, TextUnit.CREATE_CLONE);
			TextFragment tf = trgTu.getContent();
			tf.setCodedText(trgTu.getContent().getCodedText().replace("e", "\u00CA"));
		//}
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
