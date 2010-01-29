package net.sf.okapi.connectors.microsoft;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class ManualTest {

	public static void main (String[] args) {
		
		IQuery conn = new MicrosoftMTConnector();
		conn.open();
		conn.setLanguages(LocaleId.ENGLISH, LocaleId.FRENCH);
		
		conn.query("The big red and blue car. This <span translate=\"no\">is a</span> test");
		if ( conn.hasNext() ) {
			QueryResult qr = conn.next();
			System.out.println("\n"+qr.source.toString()+"\n"+qr.target.toString());
		}
		
		TextFragment tf = new TextFragment("The ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("big");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" red car.");
		
		conn.query(tf);
		if ( conn.hasNext() ) {
			QueryResult qr = conn.next();
			System.out.println("\n"+qr.source.toString()+"\n"+qr.target.toString());
		}
	}

}
