package net.sf.okapi.common.resource;

import java.util.List;

public class Main {

	public static void main(String[] args) {

		IContainer cont = new Container();
		
		cont.append('a');
		cont.append('b');
		cont.append("cd ");
		System.out.println(cont.getEquivText());
		
		IContent cnt = cont.addContent(true);
		cnt.append("sent1.");
		System.out.println(cont.getEquivText());

		cont.addContent(false, " ");
		System.out.println(cont.getEquivText());

		cont.addContent(true, "sent2");
		cont.append("+codes: ");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("bold");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("rebold");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.append(IContent.CODE_ISOLATED, "br", "<br/>");
		System.out.println(cont.getEquivText());
		
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
		cont = new Container();
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

		IContainer cont2 = new Container();
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

	}

	private static void printSegments (IContainer container) {
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
}
