package net.sf.okapi.common.test;

import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class Main {

	public static void main(String[] args) {

		TextUnit tu = new TextUnit("t1", "Main text");
		tu.addChild(new SkeletonUnit("s1", "<p>"));
		tu.addChild(new SkeletonUnit(SkeletonUnit.MAINTEXT, ""));
		tu.addChild(new SkeletonUnit("s2", "</p>"));
		writeTU(tu, true);

		tu = new TextUnit("t1", "Main text");
		tu.addChild(new SkeletonUnit("s1", "<p title'"));
		tu.addChild(new TextUnit("t2", "Text of title"));
		tu.addChild(new SkeletonUnit("s2", "'>"));
		tu.addChild(new SkeletonUnit(SkeletonUnit.MAINTEXT, ""));
		tu.addChild(new SkeletonUnit("s3", "</p>"));
		writeTU(tu, true);
		
		tu = new TextUnit("t1", "Main text");
		tu.getSource().setProperty("dir", "ltr");
		tu.addChild(new SkeletonUnit("s1", "<p title'"));
		tu.addChild(new TextUnit("t2", "Text of title"));
		tu.addChild(new SkeletonUnit("s2", "' dir='$P#dir@t1#'>"));
		tu.addChild(new SkeletonUnit(SkeletonUnit.MAINTEXT, ""));
		tu.addChild(new SkeletonUnit("s3", "</p>"));
		writeTU(tu, true);
		
		tu = new TextUnit("t1", "Main text with image: ");
		tu.getSourceContent().append(TagType.PLACEHOLDER, "img",
			"<img alt=\"{@#$t3}\"/>").setHasSubflow(true);
		tu.getSource().setProperty("dir", "ltr");
		tu.addChild(new SkeletonUnit("s1", "<p title'"));
		tu.addChild(new TextUnit("t2", "Text of title"));
		tu.addChild(new SkeletonUnit("s2", "' dir='$P#dir@t1#'>"));
		tu.addChild(new SkeletonUnit(SkeletonUnit.MAINTEXT, ""));
		tu.addChild(new TextUnit("t3", "Text of alt"));
		tu.addChild(new SkeletonUnit("s3", "</p>"));
		writeTU(tu, true);
		
	}
	
	private static void writeTU (TextUnit tu,
		boolean useSource)
	{
		System.out.println("--- tu:");
		if ( tu.hasChild() ) {
			for ( IContainable part : tu.childUnitIterator() ) {
				if ( part == null ) {
					System.out.print("["+tu.toString()+"]");
				}
				else if ( part instanceof TextUnit ) {
					System.out.print("["+part.toString()+"]");
				}
				else if ( part instanceof SkeletonUnit ) {
					if ( part.getID().equals(SkeletonUnit.MAINTEXT) ) {
						System.out.print("["+tu.toString()+"]");
					}
					else {
						System.out.print(((SkeletonUnit)part).toResolvedString(true));
					}
				}
			}
			System.out.println();
		}
		else {
			System.out.println("["+tu.toString()+"]");
		}
		System.out.println("--- end");
	}
	
}
