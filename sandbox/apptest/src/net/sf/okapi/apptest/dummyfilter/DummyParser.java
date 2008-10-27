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
import net.sf.okapi.apptest.resource.Group;
import net.sf.okapi.apptest.resource.SkeletonUnit;
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
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s0", "<main>\n")));
		
		Group grp = new Group(subDocRes.getID());
		grp.setID("g1");
		grp.setIsReference(true);
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp));

		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s6", "<list>\n")));

		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s7", "<t id='t2'>")));

		list.add(new FilterEvent(FilterEventType.TEXT_UNIT,
			new TextUnit("t2", "Text 2")));
		
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s8", "</t>\n")));

		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s3", "<t id='t1bis'>")));
		TextUnit tu = new TextUnit();
		tu.setID("t1bis");
		TextContainer tc = new TextContainer(tu);
		tc.append("Text before ");
		TextUnit tu2 = new TextUnit("talt", "Alt text", true);
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu2));
		Code code = tc.append(TagType.PLACEHOLDER, "image",
			"<img href='img.png' alt='" + TextFragment.makeRefMarker("talt") + "'/>");
		code.setHasReference(true);
		tc.append(" and after.");
		tu.setSourceContent(tc);
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s4", "</t>\n")));

		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s9", "</list>\n")));

		list.add(new FilterEvent(FilterEventType.END_GROUP, grp));

		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
				new SkeletonUnit("s5", "<p>")));

		tu = new TextUnit();
		tu.setID("twithlist");
		tc = new TextContainer(tu);
		tc.append("Before list ");
		code = tc.append(TagType.PLACEHOLDER, "list", TextContainer.makeRefMarker("g1"));
		code.setHasReference(true);
		tc.append(" after list");
		tu.setSourceContent(tc);
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));
		
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
				new SkeletonUnit("s10", "</p>\n")));
		
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s11", "</main>")));
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
