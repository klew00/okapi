package net.sf.okapi.apptest.annotation;

import java.util.Iterator;

import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.TextUnit;

public class Test {

	public static void main(String[] args) {
		Annotations annotations = new Annotations();

		CommentsAnnotation ca = new CommentsAnnotation();
		ca.add("Test comment 1");
		ca.add("Test comment 2");

		TargetsAnnotation ta = new TargetsAnnotation();
		ta.set("es", new TextUnit("t1", "Test text-unit spanish"));
		ta.set("jp", new TextUnit("t1", "Test text-unit japanese"));

		annotations.set(ca);
		annotations.set(ta);
		
		// Cannot to this:
		// TextUnit trgTu = annotations.get(TargetsAnnotation.class).get("es");
		TextUnit trgTu = ((TargetsAnnotation)annotations.get(TargetsAnnotation.class)).get("es");
		System.out.println(trgTu.toString());

		ca = annotations.get(CommentsAnnotation.class);
		for (String s : ca) {
			System.out.println(s);
		}
		
		TextUnit en = (ta = annotations.get(TargetsAnnotation.class)).get("es");
		System.out.println(en.toString());
		for (String s : ta) {
			System.out.print(s + " = ");
			System.out.println(ta.get(s).toString());
		}

		System.out.println(ca.toString());
		System.out.println(ta.toString());

		System.out.println("------");

		TextUnit tu = new TextUnit("t1", "Source text of t1.");
		tu.setProperty(new Property("prop1", "Source value of prop1", true));
		tu.setTarget("fr", new TextUnit("t1", "FR text of t1."));
		tu.setTarget("es", new TextUnit("t1", "ES text of t1."));
		tu.setTargetProperty("fr", new Property("prop1", "FR value of prop1", true));
		tu.setTargetProperty("fr", new Property("prop2", "FR value of prop2", true));
		tu.setTargetProperty("es", new Property("prop1", "ES value of prop1", true));

		System.out.println("src text="+tu.toString());
		System.out.println("src prop1="+tu.getProperty("prop1").toString());
		System.out.println("trg text="+tu.getTarget("fr").toString());
		System.out.println("trg prop1="+tu.getTargetProperty("fr", "prop1").toString());

		System.out.println("------");
		Iterator<String> targetNames = tu.targetLanguages();
		while ( targetNames.hasNext() ) {
			String lang = targetNames.next();
			System.out.println("target = " + lang);
			Iterator<String> propNames = tu.targetPropertyNames(lang);
			while ( propNames.hasNext() ) {
				String name = propNames.next();
				System.out.println(" prop(" + name + ") = "
					+ tu.getTargetProperty(lang, name).getValue());
			}
		}
		
	}

}
