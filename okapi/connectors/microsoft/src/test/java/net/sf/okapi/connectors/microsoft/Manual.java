package net.sf.okapi.connectors.microsoft;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.TextFragment;

public class Manual {

	public static void main (String[] args) {
		
		MicrosoftMTConnector conn = new MicrosoftMTConnector();
		((Parameters)conn.getParameters()).setAppId("AppId");
		conn.open();
		conn.setLanguages(LocaleId.ENGLISH, LocaleId.FRENCH);
		
//		conn.query("The big red and blue car. This is a simple test");
//		if ( conn.hasNext() ) {
//			QueryResult qr = conn.next();
//			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText());
//		}
//		
//		conn.query("The <span id='1'>big</span> red and blue car. This <span id='2'>is a simple</span> test");
//		while ( conn.hasNext() ) {
//			QueryResult qr = conn.next();
//			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText());
//		}
//		
//		TextFragment tf = new TextFragment("The ");
//		tf.append(TagType.OPENING, "b", "<b>");
//		tf.append("bolded");
//		tf.append(TagType.CLOSING, "b", "</b>");
//		tf.append(" text.");
//		
//		conn.query(tf);
//		while ( conn.hasNext() ) {
//			QueryResult qr = conn.next();
//			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText());
//		}
//
//		tf = new TextFragment("The ");
//		tf.append(TagType.OPENING, "b", "<b>");
//		tf.append("red");
//		tf.append(TagType.CLOSING, "b", "</b>");
//		tf.append(" car has the markers & and < on its roof.");
//		
//		conn.query(tf);
//		while ( conn.hasNext() ) {
//			QueryResult qr = conn.next();
//			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText());
//		}
//		
//		
		conn.setMaximumHits(3);
		TextFragment tf = new TextFragment("Test to see if fuzzy matches are working.");
		conn.query(tf);
		while ( conn.hasNext() ) {
			QueryResult qr = conn.next();
			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText()+"\nscore="+qr.score);
		}
		
//		TextFragment tf = new TextFragment("Test to see how fuzzy matches are working.");
//		TextFragment trg = new TextFragment("Essai pour voir comment les fuzzy matches fonctionnent.");
//		conn.addTranslation(tf, trg, 6);
//		
//		tf = new TextFragment("Test to see if fuzzy matches are working.");
//		trg = new TextFragment("Essai pour voir comment les fuzzy matches fonctionnent.");
//		conn.addTranslation(tf, trg, 6);
		
	
	}

}
