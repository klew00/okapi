package net.sf.okapi.connectors.microsoft;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.IQuery;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;


public class Manual {

	public static void main (String[] args) {
		
		IQuery conn = new MicrosoftMTConnector();
		((Parameters)conn.getParameters()).setAppId("AppId");
		conn.open();
		conn.setLanguages(LocaleId.ENGLISH, LocaleId.FRENCH);
		
		conn.query("The big red and blue car. This is a simple test");
		if ( conn.hasNext() ) {
			QueryResult qr = conn.next();
			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText());
		}
		
		conn.query("The <span id='1'>big</span> red and blue car. This <span id='2'>is a simple</span> test");
		if ( conn.hasNext() ) {
			QueryResult qr = conn.next();
			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText());
		}
		
		TextFragment tf = new TextFragment("The ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("bolded");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" text.");
		
		conn.query(tf);
		if ( conn.hasNext() ) {
			QueryResult qr = conn.next();
			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText());
		}

		tf = new TextFragment("The ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("red");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" car has the markers & and < on its roof.");
		
		conn.query(tf);
		if ( conn.hasNext() ) {
			QueryResult qr = conn.next();
			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText());
		}
	}

}
