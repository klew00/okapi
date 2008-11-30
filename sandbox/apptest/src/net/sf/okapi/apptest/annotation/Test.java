package net.sf.okapi.apptest.annotation;

import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.TextContainer;
import net.sf.okapi.apptest.resource.TextUnit;

public class Test {

	public static void main(String[] args) {
		Annotations annotations = new Annotations();

		CommentsAnnotation ca = new CommentsAnnotation();
		ca.add("Test comment 1");
		ca.add("Test comment 2");

		TargetsAnnotation ta = new TargetsAnnotation();
		ta.set("es", new TextContainer("Test text-unit spanish"));
		ta.set("jp", new TextContainer("Test text-unit japanese"));

		annotations.set(ca);
		annotations.set(ta);
		
		// Cannot to this:
		// TextUnit trgTu = annotations.get(TargetsAnnotation.class).get("es");
		TextContainer tt = ((TargetsAnnotation)annotations.get(TargetsAnnotation.class)).get("es");
		System.out.println(tt.toString());

		ca = annotations.get(CommentsAnnotation.class);
		for (String s : ca) {
			System.out.println(s);
		}
		
		TextContainer en = (ta = annotations.get(TargetsAnnotation.class)).get("es");
		System.out.println(en.toString());
		for (String s : ta.getLanguages() ) {
			System.out.print(s + " = ");
			System.out.println(ta.get(s).toString());
		}

		System.out.println(ca.toString());
		System.out.println(ta.toString());

		System.out.println("------");

		TextUnit tu = new TextUnit("t1", "Source text of t1.");
		tu.setProperty(new Property("prop1", "Source value of prop1", false));
		tu.setTarget("fr", new TextContainer("FR text of t1."));
		tu.setTarget("es", new TextContainer("ES text of t1."));
		tu.setTargetProperty("fr", new Property("prop1", "FR value of prop1", false));
		tu.setTargetProperty("fr", new Property("prop2", "FR value of prop2", false));
		tu.setTargetProperty("es", new Property("prop1", "ES value of prop1", false));

		System.out.println("src text="+tu.toString());
		System.out.println("src prop1="+tu.getProperty("prop1").toString());
		System.out.println("trg text="+tu.getTarget("fr").toString());
		System.out.println("trg prop1="+tu.getTargetProperty("fr", "prop1").toString());

		System.out.println("------");
		for ( String lang : tu.getTargetLanguages() ) {
			System.out.println("target = " + lang);
			for ( String name : tu.getTargetPropertyNames(lang) ) {
				System.out.println(" prop(" + name + ") = "
					+ tu.getTargetProperty(lang, name).getValue());
			}
		}
		
	}

}
