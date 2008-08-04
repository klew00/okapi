package net.sf.okapi.common.resource2;

public class Main {

	public static void main(String[] args) {

		Document doc = new Document();
		
		doc.add(new SkeletonUnit("s1", "<doc>"));
		
		TextUnit tu = new TextUnit("t1", "text unit 1");
		tu.setSkeletonBefore(new SkeletonUnit("s2", "<p>"));
		tu.setSkeletonAfter(new SkeletonUnit("s3", "</p>"));
		doc.add(tu);
		
		Group group1 = new Group();
		group1.setID("g1");
		group1.add(new TextUnit("t2", "text unit 2"));
		Group group2 = new Group();
		tu = new TextUnit("t3", "text unit 3");
		tu.setSkeletonBefore(new SkeletonUnit("s4", "<p>"));
		tu.setSkeletonAfter(new SkeletonUnit("s5", "</p>"));
		
		group2.add(tu);
		group1.add(group2);
		doc.add(group1);

		doc.add(new SkeletonUnit("s6", "</doc>"));

		show(doc, 0);
	}
	
	private static void show (IResourceContainer container,
		int level)
	{
		if ( container instanceof Document ) {
			for ( int i=0; i<level; i++ ) System.out.print('-'); 
			System.out.println("document");
		}
		else if ( container instanceof Group ) {
			for ( int i=0; i<level; i++ ) System.out.print('-'); 
			System.out.println("group");
		}

		for ( IContainable unit : container ) {
			if ( unit instanceof Group ) {
				show((IResourceContainer)unit, level+1);
			}
			else if ( unit instanceof TextUnit ) {
				for ( int i=0; i<level; i++ ) System.out.print('-'); 
				System.out.println("text-unit: "+unit.toString());
			}
			else if ( unit instanceof SkeletonUnit ) {
				for ( int i=0; i<level; i++ ) System.out.print('-'); 
				System.out.println("skeleton-unit: "+unit.toString());
			}
		}
	}

}
