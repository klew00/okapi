package net.sf.okapi.apptest.dummyfilter;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IParser;
import net.sf.okapi.apptest.filters.FilterEvent.FilterEventType;
import net.sf.okapi.apptest.resource.Code;
import net.sf.okapi.apptest.resource.Document;
import net.sf.okapi.apptest.resource.GenericSkeleton;
import net.sf.okapi.apptest.resource.GenericSkeletonPart;
import net.sf.okapi.apptest.resource.Group;
import net.sf.okapi.apptest.resource.PropertiesUnit;
import net.sf.okapi.apptest.resource.SubDocument;
import net.sf.okapi.apptest.resource.TextContainer;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.apptest.resource.TextFragment.TagType;

public class DummyParser implements IParser {

	ArrayList<FilterEvent> list;
	private int current;
	private boolean canceled;
	private String language;
	
	public void setOptions (String language,
		String defaultEncoding)
	{
		// Not used in this parser
		System.out.println("DummyParser: setOptions() called");
		this.language = language;
		System.out.println(" -output language = " + language);
		System.out.println(" -output default encoding = " + defaultEncoding);
	}
	
	public void open (InputStream input) {
		System.out.println("DummyParser: open(stream) called");
		resetResources();
	}
	
	public void close () {
		System.out.println("DummyParser: close() called");
		list.clear();
		list = null;
	}
	
	public boolean hasNext () {
		if ( list == null ) return false;
		return (current<list.size()-1);
	}
	
	public FilterEvent next () {
		if ( canceled ) {
			if ( current < list.size()-1 ) {
				current = list.size()-2;
			}
		}
		current++;
		return list.get(current);
	}
	
	public IResource getResource () {
		return list.get(current).getResource();
	}

	private void makeCase001 () {
		// <p>Before <img href='img.png' alt='text'/> after.</p>
/*		Group grp1 = new Group(null, "g1");
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp1,
			new GenericSkeleton(new GenericSkeletonPart("s1", "<p>"))));
*/
		PropertiesUnit pu = new PropertiesUnit("p1", true);
		pu.getSourceProperties().setProperty("href", "img.png");
		list.add(new FilterEvent(FilterEventType.PROPERTIES_UNIT, pu));
		
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT,
			new TextUnit("t1", "text", true)));

		TextUnit tu = new TextUnit();
		tu.setID("t2");
		tu.setIsReference(true);
		TextContainer tc = new TextContainer(tu);
		tc.append("Before ");
		Code code = tc.append(TagType.PLACEHOLDER, "image",
			"<img href='" + TextContainer.makeRefMarker("p1","href") +
			"' alt='" + TextContainer.makeRefMarker("t1") + "'/>");
		code.setHasReference(true);
		tc.append(" after.");
		tu.setSourceContent(tc);
		GenericSkeleton gs = new GenericSkeleton();
		gs.add(new GenericSkeletonPart("s1", "<p>"));
		gs.add(new GenericSkeletonPart("s2", TextFragment.makeRefMarker("t2")));
		gs.add(new GenericSkeletonPart("s3", "</p>"));
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu, gs));

//		list.add(new FilterEvent(FilterEventType.END_GROUP, grp1,
//			new GenericSkeleton(new GenericSkeletonPart("s2", "</p>\n"))));
	}
	
	private void makeCase002 () {
		// <p>Before <a href='link.htm'/> after.</p>
		Group grp1 = new Group(null, "g1");
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp1,
			new GenericSkeleton(new GenericSkeletonPart("s1", "<p>"))));

		PropertiesUnit pu = new PropertiesUnit("p1", true);
		pu.getSourceProperties().setProperty("href", "link.htm");
		list.add(new FilterEvent(FilterEventType.PROPERTIES_UNIT, pu));

		TextUnit tu = new TextUnit();
		tu.setID("t1");
		TextContainer tc = new TextContainer(tu);
		tc.append("Before ");
		Code code = tc.append(TagType.PLACEHOLDER, "link",
			"<a href='" + TextContainer.makeRefMarker("p1","href") + "'/>");
		code.setHasReference(true);
		tc.append(" after.");
		tu.setSourceContent(tc);
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));

		list.add(new FilterEvent(FilterEventType.END_GROUP, grp1,
			new GenericSkeleton(new GenericSkeletonPart("s2", "</p>\n"))));

	}
	
	private void makeCase003 () {
		// <table id=100> <tr><td>text</td></tr><table>
		Group grp1 = new Group(null, "g1");
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp1,
			new GenericSkeleton(new GenericSkeletonPart("s1", "<table id=100>\n "))));

		Group grp2 = new Group("g1", "g2");
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp2,
			new GenericSkeleton(new GenericSkeletonPart("s2", "<tr>"))));
		
		Group grp3 = new Group("g2", "g3");
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp3,
			new GenericSkeleton(new GenericSkeletonPart("s3", "<td>"))));
		
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT,
			new TextUnit("t1", "text")));
		
		list.add(new FilterEvent(FilterEventType.END_GROUP, grp3,
			new GenericSkeleton(new GenericSkeletonPart("s4", "</td>"))));

		list.add(new FilterEvent(FilterEventType.END_GROUP, grp2,
			new GenericSkeleton(new GenericSkeletonPart("s5", "</tr>\n"))));
		
		list.add(new FilterEvent(FilterEventType.END_GROUP, grp1,
			new GenericSkeleton(new GenericSkeletonPart("s6", "</table>\n"))));
	}

	private void makeCase004 () {
		Group grp1 = new Group(null, "g1");
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp1,
			new GenericSkeleton(new GenericSkeletonPart("s1", "<p>"))));
		
		Group grp2 = new Group(null, "g2", true);
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp2,
			new GenericSkeleton(new GenericSkeletonPart("s2", "<ul>", true))));
		
		Group grp3 = new Group(null, "g3", false);
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp3,
			new GenericSkeleton(new GenericSkeletonPart("s3", "\n  <li>"))));
		
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT,
			new TextUnit("t1", "Text of item 1")));
		
		list.add(new FilterEvent(FilterEventType.END_GROUP, grp3,
			new GenericSkeleton(new GenericSkeletonPart("s4", "</li>"))));
		
		Group grp4 = new Group(null, "g4", false);
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp4,
			new GenericSkeleton(new GenericSkeletonPart("s5", "\n  <li>"))));
		
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT,
			new TextUnit("t2", "Text of item 2")));
		
		list.add(new FilterEvent(FilterEventType.END_GROUP, grp4,
			new GenericSkeleton(new GenericSkeletonPart("s6", "</li>"))));
		
		list.add(new FilterEvent(FilterEventType.END_GROUP, grp2,
			new GenericSkeleton(new GenericSkeletonPart("s7", "\n </ul>"))));

		TextUnit tu = new TextUnit();
		tu.setID("t3");
		TextContainer tc = new TextContainer(tu);
		tc.append("Text before list: \n ");
		Code code = tc.append(TagType.PLACEHOLDER, "list", TextContainer.makeRefMarker("g2"));
		code.setHasReference(true);
		tc.append("\n and text after the list.");
		tu.setSourceContent(tc);
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));

		list.add(new FilterEvent(FilterEventType.END_GROUP, grp1,
			new GenericSkeleton(new GenericSkeletonPart("s8", "</p>\n"))));
	}
	
	private void resetResources () {
		canceled = false;
		current = -1;
		
		list = new ArrayList<FilterEvent>();
		
		Document docRes = new Document();
		docRes.setID("d1");
		docRes.setEncoding("UTF-8"); // Always
		docRes.setLanguage(language);
		list.add(new FilterEvent(FilterEventType.START_DOCUMENT, docRes));
		SubDocument subDocRes = new SubDocument(docRes.getID());
		subDocRes.setID("sd1");
		list.add(new FilterEvent(FilterEventType.START_SUBDOCUMENT, subDocRes));
		
		makeCase001();
		//makeCase002();
		//makeCase003();
		//makeCase004();
	
		list.add(new FilterEvent(FilterEventType.END_SUBDOCUMENT, subDocRes));
		list.add(new FilterEvent(FilterEventType.END_DOCUMENT, docRes));
	}

	public void cancel () {
		canceled = true;
	}

	public void open (CharSequence inputText) {
		System.out.println("DummyParser: open(charseq) called");
		resetResources();
	}

	public void open (URL inputURL) {
		System.out.println("DummyParser: open(URL) called");
		resetResources();
	}

	public IParameters getParameters () {
		return null;
	}

	public void setParameters (IParameters params) {
		// No parameters used for this filter.
	}

}
