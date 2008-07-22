package net.sf.okapi.common.resource;

import java.util.List;

public class Main {

	public static void main(String[] args) {

		IContainer cont = new Container();
		
		cont.append('a');
		cont.append('b');
		cont.append("cd ");
		System.out.println(cont.getEquivText());
		
		IContent cnt = cont.addPart(true);
		cnt.append("sent1.");
		System.out.println(cont.getEquivText());

		cont.addPart(false, " ");
		System.out.println(cont.getEquivText());

		cont.addPart(true, "sent2");
		cont.append("+codes: ");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("bold");
		cont.append(IContent.CODE_OPENING, "b", "<b>");
		cont.append("rebold");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.append(IContent.CODE_CLOSING, "b", "</b>");
		cont.append(IContent.CODE_ISOLATED, "br", "<br/>");
		System.out.println(cont.getEquivText());
		
		List<IContent> list = cont.getSegments();
		System.out.print("List of the segments: ");
		for ( int i=0; i<list.size(); i++ ) {
			System.out.print(String.format("[seg(%d)='%s'] ", i, list.get(i).getEquivText()));
		}
		System.out.println("");

		list = cont.getParts();
		System.out.print("List of the parts: ");
		for ( int i=0; i<list.size(); i++ ) {
			System.out.print(String.format("[part(%d)='%s'] ", i, list.get(i).getEquivText()));
		}
		System.out.println("");

		List<Code> codes = cont.getCodes();
		System.out.print("List of the codes: ");
		for ( int i=0; i<codes.size(); i++ ) {
			System.out.print(String.format("[code(%d)=id=%d,data='%s'] ", i, 
				codes.get(i).getID(), codes.get(i).getData()));
		}
		System.out.println("");
	}
	
}
