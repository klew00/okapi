package net.sf.okapi.connectors.opentran;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.query.QueryResult;

public class ManualTry {

	static public void main (String[] args ) {
		OpenTranTMConnector conn = null;
		try {
			conn = new OpenTranTMConnector();
			conn.open();
			conn.setThreshold(60);
			conn.setMaximumHits(20);
			conn.setLanguages(LocaleId.ENGLISH, LocaleId.FRENCH);
			
			conn.query("Open the application");
			showResults(conn, "No codes, not exact");
			
			conn.query("Open the application.");
			showResults(conn, "No codes, exact");

			TextFragment tf = new TextFragment("Open the ");
			tf.append(TagType.OPENING, "b", "<b>");
			tf.append("application");
			tf.append(TagType.CLOSING, "b", "</b>");
			conn.query(tf);
			showResults(conn, "Codes, not exact");
			
			tf.append('.');
			conn.query(tf);
			showResults(conn, "Codes, exact");
		}
		finally {
			if ( conn != null ) conn.close();
		}
	}
	
	static void showResults (OpenTranTMConnector conn,
		String caption)
	{
		QueryResult qr;
		System.out.println("--- Results for "+ caption + ":");
		while ( conn.hasNext() ) {
			qr = conn.next();
			System.out.println("-- S="+qr.source.toText());
			System.out.println("   T="+qr.target.toText());
			System.out.println("   O="+qr.origin);
			System.out.println("   score="+qr.getFuzzyScore());
		}
		System.out.println("--- end.");
	}

}
