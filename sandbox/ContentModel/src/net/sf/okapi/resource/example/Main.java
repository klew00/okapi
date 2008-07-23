package net.sf.okapi.resource.example;

import java.io.PrintWriter;

import net.sf.okapi.resource.EndPairedContentMarker;
import net.sf.okapi.resource.MarkupContainer;
import net.sf.okapi.resource.RootContainer;
import net.sf.okapi.resource.StartPairedContentMarker;
import net.sf.okapi.resource.TextFragment;
import net.sf.okapi.resource.serializer.DummyXmlSerializer;

public class Main {

	public static void main(String[] args) {
		
		// Hello <b>big</b> world!
		
		RootContainer root = new RootContainer();

		// 'Hello '
		TextFragment fragment = new TextFragment("He");
		fragment.append("llo ");
		root.add(fragment);
		
		// '<b>big</b>'
		MarkupContainer markup = new MarkupContainer();
		markup.add(new TextFragment("big"));
		MarkupContainer markup2 = new MarkupContainer();
		markup.add(markup2);
		root.add(markup);
		
		// ' world!'
		root.add(new TextFragment(" world!"));

		
		// let's add a marker for 'ig wo'
		TextFragment big = (TextFragment)markup.get(0);
		TextFragment ig = big.splitAt(1);
		StartPairedContentMarker startMarker = new StartPairedContentMarker();
		markup.add(startMarker);
		markup.add(ig);

		TextFragment world = (TextFragment) root.get(root.size()-1);
		TextFragment rld = world.splitAt(3);
		EndPairedContentMarker endMarker = new EndPairedContentMarker();
		root.add(endMarker);
		root.add(rld);
		
		// let's serialize it...
		
		DummyXmlSerializer serializer = new DummyXmlSerializer(new PrintWriter(System.out));
		
		System.out.println(root.getCodedText());
		System.out.println(markup.getCodedText());
		System.out.println(markup.getCodedText(1));
		//serializer.serialize(root);
		serializer.flush();
	}
	
}
