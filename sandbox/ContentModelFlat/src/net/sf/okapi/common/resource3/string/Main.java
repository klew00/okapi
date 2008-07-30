package net.sf.okapi.common.resource3.string;

import java.util.List;

import net.sf.okapi.common.resource3.tree.SegmentContainer;
import net.sf.okapi.common.resource3.tree.TreeRootContainer;

public class Main {

	public static void main(String[] args) {

		StringRootContainer flatRoot = new StringRootContainer();
		
		IContent cnt = flatRoot.addContent(true);
		cnt.append("before ");
		cnt.append(Content.CODE_OPENING, "b", "<b>");
		cnt.append("bold");
		cnt.append(Content.CODE_CLOSING, "b", "</b>");
		cnt.append("after.");
		cnt = flatRoot.addContent(false);
		cnt.append("{ not-seg }");
		cnt = flatRoot.addContent(true);
		cnt.append("And last segment.");

		System.out.println("String:");
		System.out.println(flatRoot.getEquivText());
		List<IContent> segList = flatRoot.getSegments();
		int i = 0;
		for ( IContent seg : segList ) {
			System.out.println(String.format("seg=%d: '%s'", ++i, seg.getEquivText()));
		}
		
		System.out.println("Tree:");
		TreeRootContainer root = flatRoot.getTreeView();
		System.out.println(root.toString());
		List<SegmentContainer> segList2 = root.getSegments();
		i = 0;
		for ( SegmentContainer seg : segList2 ) {
			System.out.println(String.format("seg=%d: '%s'", ++i, seg.getEquivText()));
		}
		
	}

}
