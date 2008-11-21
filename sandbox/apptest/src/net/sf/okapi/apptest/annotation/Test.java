package net.sf.okapi.apptest.annotation;

public class Test {

	public static void main(String[] args) {
		Annotations annotations = new Annotations();

		CommentsAnnotation ca = new CommentsAnnotation();
		ca.add("Test comment 1");
		ca.add("Test comment 2");

		TargetsAnnotation ta = new TargetsAnnotation();
		ta.add("es", "Test segment spanish");
		ta.add("jp", "Test segment japanese");

		annotations.add(ca);
		annotations.add(ta);
		// annotations.add(new String()); // note the compiler doesn't like
		// this, must implement IAnnotation

		ca = annotations.get(CommentsAnnotation.class);
		for (String s : ca) {
			System.out.println(s);
		}

		ta = annotations.get(TargetsAnnotation.class);
		for (String s : ta) {
			System.out.println(s);
		}

		// String sa = annotations.get(String.class); // note the compiler
		// doesn't like this, must implement IAnnotation

		System.out.println(ca.toString());
		System.out.println(ta.toString());
		// System.out.println(sa.toString());
	}

}
