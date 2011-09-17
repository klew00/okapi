package net.sf.okapi.connectors.microsoft;

import java.util.ArrayList;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.TextFragment;

public class Manual {

	public static void main (String[] args) {
		
		MicrosoftMTConnector conn = new MicrosoftMTConnector();
		((Parameters)conn.getParameters()).setAppId("7286B45B8C4816BDF75DC007C1952DDC11C646C1");
		conn.open();
		conn.setLanguages(LocaleId.ENGLISH, LocaleId.FRENCH);
		conn.setMaximumHits(5);
		conn.setThreshold(30);

		// Add list
		ArrayList<TextFragment> sources = new ArrayList<TextFragment>();
		ArrayList<TextFragment> targets = new ArrayList<TextFragment>();
		ArrayList<Integer> ratings = new ArrayList<Integer>();
		
//		TextFragment tf = new TextFragment("This is a ");
//		tf.append(TagType.OPENING, "b", "<b>");
//		tf.append("test");
//		tf.append(TagType.CLOSING, "b", "</b>");
//		sources.add(tf);
//		tf = new TextFragment("Ceci est un");
//		tf.append(TagType.OPENING, "b", "<b>");
//		tf.append("test");
//		tf.append(TagType.CLOSING, "b", "</b>");
//		targets.add(tf);
//		ratings.add(-1);
		
		sources.add(new TextFragment("This is a test"));
		targets.add(new TextFragment("1"));
		ratings.add(-10);
		conn.addTranslationList(sources, targets, ratings);
		
		
//		// Query list
//		ArrayList<TextFragment> frags = new ArrayList<TextFragment>();
//		frags.add(new TextFragment("This is a test"));
//		TextFragment tf = new TextFragment("This car is black & ");
//		tf.append(TagType.OPENING, "bold", "[b]");
//		tf.append("blue");
//		tf.append(TagType.CLOSING, "bold", "[/b]");
//		frags.add(tf);
//		frags.add(new TextFragment("We can do several queries at once."));
//		List<List<QueryResult>> list = conn.queryList(frags);
//		int i = 1;
//		for ( List<QueryResult> resList : list ) {
//			int j = 1;
//			for ( QueryResult res : resList ) {
//				System.out.println(String.format("%d-%d (%d):", i, j, res.score));
//				System.out.println("src="+res.source.toText());
//				System.out.println("trg="+res.target.toText());
//				j++;
//			}
//			i++;
//		}
		
		conn.setThreshold(10);
		conn.setMaximumHits(20);
		conn.query("This is a test");
		while ( conn.hasNext() ) {
			QueryResult qr = conn.next();
			System.out.println("\nScore = "+String.valueOf(qr.getCombinedScore())+"\n"+qr.source.toText()+"\n"+qr.target.toText());
		}
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
//		conn.setMaximumHits(3);
//		TextFragment tf = new TextFragment("Test to see if fuzzy matches are working.");
//		conn.query(tf);
//		while ( conn.hasNext() ) {
//			QueryResult qr = conn.next();
//			System.out.println("\n"+qr.source.toText()+"\n"+qr.target.toText()+"\nscore="+qr.score);
//		}
		
//		TextFragment tf = new TextFragment("Test to see how fuzzy matches are working.");
//		TextFragment trg = new TextFragment("Essai pour voir comment les fuzzy matches fonctionnent.");
//		conn.addTranslation(tf, trg, 6);
//		
//		tf = new TextFragment("Test to see if fuzzy matches are working.");
//		trg = new TextFragment("Essai pour voir comment les fuzzy matches fonctionnent.");
//		conn.addTranslation(tf, trg, 6);
		
	
	}

}
