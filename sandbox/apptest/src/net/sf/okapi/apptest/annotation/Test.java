package net.sf.okapi.apptest.annotation;

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

		ca = annotations.get(CommentsAnnotation.class);
		for (String s : ca) {
			System.out.println(s);
		}

		ta = annotations.get(TargetsAnnotation.class);
		for (String s : ta) {
			System.out.print(s + " = ");
			System.out.println(ta.get(s).toString());
		}

		System.out.println(ca.toString());
		System.out.println(ta.toString());

	}

}
