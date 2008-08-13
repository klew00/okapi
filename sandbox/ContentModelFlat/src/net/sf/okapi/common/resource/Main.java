package net.sf.okapi.common.resource;

import java.util.Iterator;

import net.sf.okapi.common.resource.IContent.TagType;

public class Main {

	public static void main(String[] args) {

		RootContainer cont = new RootContainer();
		
		cont.append('a');
		cont.append("bc ");
		cont.append(TagType.OPENING, "b", "<b1>");
		cont.append("bold");
		cont.append(TagType.OPENING, "b", "<b2>");
		cont.append("rebold.");
		cont.append(TagType.CLOSING, "b", "</b2>");
		cont.append(TagType.CLOSING, "b", "</b1>");
		cont.append(TagType.PLACEHOLDER, "br", "<br/>");
		cont.append("After break.");
		System.out.println("-Initial:");
		System.out.println(cont.toString());
		
		cont.clear();
		System.out.println("-After clear:");
		System.out.println(cont.toString());
		
		TextUnit tu1 = new TextUnit("p1", "parent");
		TextUnit tu2 = new TextUnit("sf1", "Text of SF1");
		TextUnit tu3 = new TextUnit("sf2", "Text of SF2");
		tu1.addChild(tu2);
		tu1.addChild(tu3);
		cont.setParent(tu1);
		cont.append("Before ");
		Code code = cont.append(TagType.PLACEHOLDER, "image",
			String.format("<img alt='%ssf1%s' title='%ssf2%s'/>",
			IContent.SFMARKER_START, IContent.SFMARKER_END,
			IContent.SFMARKER_START, IContent.SFMARKER_END));
		code.setHasSubflow(true);
		cont.append(" after");
		System.out.println("-With subflow:");
		System.out.println(cont.toString());
	
		/*
		printSegments(cont);

		System.out.print("List of the parts: ");
		for ( int i=0; i<cont.size(); i++ ) {
			System.out.print(String.format("[part(%d)='%s'] ", i, cont.get(i).getEquivText()));
		}
		System.out.println("");

		List<Code> codes = cont.getCodes();
		System.out.print("List of the codes: ");
		for ( int i=0; i<codes.size(); i++ ) {
			System.out.print(String.format("[code(%d)=id=%d,data='%s'] ", i, 
				codes.get(i).getID(), codes.get(i).getData()));
		}
		System.out.println("\n\n");

		// New test set
		cont = new RootContainer();
		cnt = cont.addContent(true, "sent1");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("bold1_1");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("bold1_2");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.append("bold1_1bis");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.addContent(false, "_");
		cont.addContent(true, "sent2");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("bold2_1");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("bold2_2");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.append("bold2_1bis");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.addContent(false, "_");
		cont.addContent(true, "sent3");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("bold3_1");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("bold3_2");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.append("bold3_1bis");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		printSegments(cont);

		ITextContainer cont2 = new RootContainer();
		cont2.add(cont.getSegment(1));
		System.out.println("cont:");
		printSegments(cont);
		System.out.println("cont2:");
		printSegments(cont2);

		//cont.removeSegment(1);
		//System.out.println("cont after removeSegment(1):");
		//printSegments(cont);

		cont.joinSegments(1, 2);
		printSegments(cont);
		
		codes = cont.getCodes();
		String codedText = cont.getCodedText();
		cont.setCodedText(modifyCodedText(codedText));
		System.out.println("cont after modif:");
		printSegments(cont);
		System.out.println("cont2:");
		printSegments(cont2);
		
		//cont.getCodes(, end)
*/

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

		tu = new TextUnit("t4", "Text before footnote[ref fn1] Text after.");
		tu.setSkeletonBefore(new SkeletonUnit("s6", "<para>"));
		tu.setSkeletonAfter(new SkeletonUnit("s7", "</para>"));
		group1 = new Group();
		group1.setID("fn1");
		tu2 = new TextUnit("t5", "Text of the footnote");
		group1.add(tu2);
		tu.addChild(group1);
		doc.add(tu);
		
		doc.add(new SkeletonUnit("sLast", "</doc>"));
		show(doc, 0);
		
		// Try loop on TU
		System.out.println("\n\nTry loop on TU:");
		TextUnit tmp = tu.getFirstTextUnit();
		do {
			processTU(tmp);
		} while (( tmp = tu.getNextTextUnit()) != null );

		System.out.println("\n\nTry iterator on TU:");
		processTU(tu);
		if ( tu.hasChild() ) {
			Iterator<TextUnit> iter = tu.childTextUnitIterator().iterator();
			while ( iter.hasNext() ) {
				processTU(iter.next());
			}
		}
			
	
	}

	/*
	private static void printSegments (ITextContainer container) {
		List<IContent> list = container.getSegments();
		System.out.println("List of the segments:");
		for ( int i=0; i<list.size(); i++ ) {
			System.out.println(String.format("seg(%d)=\"%s\"", i, list.get(i).getEquivText()));
		}
		System.out.println("");
	}
	
	private static String modifyCodedText (String codedText) {
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case IContent.CODE_CLOSING:
			case IContent.CODE_OPENING:
			case IContent.CODE_ISOLATED:
				tmp.append(codedText.charAt(i++));
				tmp.append(codedText.charAt(i));
				break;
			default:
				if ( Character.isLowerCase(codedText.charAt(i)) )
					tmp.append(Character.toUpperCase(codedText.charAt(i)));
				else
					tmp.append(Character.toLowerCase(codedText.charAt(i)));
				break;
			}
		}
		return tmp.toString();
	}
	*/

	private static void processTU (TextUnit tu) {
		System.out.println("tu: id='" + tu.getID() + "' text='" +tu.getSource() + "'");
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
