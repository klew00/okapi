package net.sf.okapi.common.test;

import net.sf.okapi.common.resource.Group;
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
		System.out.println("--- tu:");
		writeTU(tu, true);
		System.out.println("\n--- end");

		tu = new TextUnit("t1", "Main text");
		tu.addChild(new SkeletonUnit("s1", "<p title'"));
		tu.addChild(new TextUnit("t2", "Text of title"));
		tu.addChild(new SkeletonUnit("s2", "'>"));
		tu.addChild(new SkeletonUnit(SkeletonUnit.MAINTEXT, ""));
		tu.addChild(new SkeletonUnit("s3", "</p>"));
		System.out.println("--- tu:");
		writeTU(tu, true);
		System.out.println("\n--- end");
		
		tu = new TextUnit("t1", "Main text");
		tu.getSource().setProperty("dir", "ltr");
		tu.addChild(new SkeletonUnit("s1", "<p title'"));
		tu.addChild(new TextUnit("t2", "Text of title"));
		tu.addChild(new SkeletonUnit("s2", "' dir='$P#dir@t1#'>"));
		tu.addChild(new SkeletonUnit(SkeletonUnit.MAINTEXT, ""));
		tu.addChild(new SkeletonUnit("s3", "</p>"));
		System.out.println("--- tu:");
		writeTU(tu, true);
		System.out.println("\n--- end");
		
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
		System.out.println("--- tu:");
		writeTU(tu, true);
		System.out.println("\n--- end");
		
		Group grp = new Group(); // Assumes this is a 'TextualGroup'
		grp.setID("g1");
		grp.getSource().setProperty("dir", "ltr");
		grp.add(new SkeletonUnit("s1", "<p title'"));
		grp.add(new TextUnit("t2", "Text of title"));
		grp.add(new SkeletonUnit("s2", "' dir='$P#dir@g1#'>"));
		tu = new TextUnit("t1", "Main text with image: ");
		tu.getSourceContent().append(TagType.PLACEHOLDER, "img",
			"<img alt=\"{@#$t3}\"/>").setHasSubflow(true);
		tu.addChild(new TextUnit("t3", "Text of alt"));
		grp.add(tu);
		grp.add(new SkeletonUnit("s3", "</p>"));
		System.out.println("--- grp:");
		writeGroup(grp, true);
		System.out.println("\n--- end");
	}
	
	private static void writeGroup (Group grp,
		boolean useSource)
	{
		for ( IContainable item : grp.getChildren() ) {
			if ( item instanceof TextUnit ) {
				writeTU((TextUnit)item, useSource);
			}
			else if ( item instanceof Group ) {
				writeGroup((Group)item, useSource);
			}
			else { // Skeleton
				System.out.print(((SkeletonUnit)item).toResolvedString(useSource));
			}
		}
	}
	
	private static void writeTU (TextUnit tu,
		boolean useSource)
	{
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
						System.out.print(((SkeletonUnit)part).toResolvedString(useSource));
					}
				}
			}
		}
		else {
			System.out.print("["+tu.toString()+"]");
		}
	}
	
}
