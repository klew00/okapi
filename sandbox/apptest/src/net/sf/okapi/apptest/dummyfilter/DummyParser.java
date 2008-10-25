package net.sf.okapi.apptest.dummyfilter;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IParser;
import net.sf.okapi.apptest.filters.FilterEvent.FilterEventType;
import net.sf.okapi.apptest.resource.Document2;
import net.sf.okapi.apptest.resource.Group2;
import net.sf.okapi.apptest.resource.SkeletonUnit;
import net.sf.okapi.apptest.resource.SubDocument;
import net.sf.okapi.apptest.resource.TextContainer;
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
		
		Document2 docRes = new Document2();
		docRes.setID("d1");
		docRes.setEncoding("UTF-8"); // Always
		docRes.setLanguage(language);
		list.add(new FilterEvent(FilterEventType.START_DOCUMENT, docRes));
		
		SubDocument subDocRes = new SubDocument(docRes.getID());
		subDocRes.setID("sd1");
		list.add(new FilterEvent(FilterEventType.START_SUBDOCUMENT, subDocRes));
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s0", "<main>")));
		
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s1", "<t id='t1'>")));

		list.add(new FilterEvent(FilterEventType.TEXT_UNIT,
				new TextUnit("t1", "Text 1")));

		TextUnit tu = new TextUnit();
		tu.setID("t1bis");
		TextContainer tc = new TextContainer(tu);
		tc.append("Text before ");
		tc.append(TagType.OPENING, "bold", "<b>");
		tc.append("bolded text");
		tc.append(TagType.CLOSING, "bold", "</b>");
		tc.append(" and after.");
		tu.setSourceContent(tc);
		list.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));
			
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s2", "</t>")));

		for ( int i=1; i<=10; i++ ) {
			list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
				new SkeletonUnit("sa"+String.valueOf(i), "<t>")));
			list.add(new FilterEvent(FilterEventType.TEXT_UNIT,
				new TextUnit("at"+String.valueOf(i), "Auto text "+String.valueOf(i))));
			list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
				new SkeletonUnit("sa"+String.valueOf(i), "</t>")));
		}
		
		Group2 grp = new Group2(subDocRes.getID());
		grp.setID("g1");
		list.add(new FilterEvent(FilterEventType.START_GROUP, grp));

		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s3", "<grp>")));

		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s4", "<t id='t2'>")));

		list.add(new FilterEvent(FilterEventType.TEXT_UNIT,
			new TextUnit("t2", "Text 2")));
		
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s5", "</t>")));

		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s6", "</grp>")));

		list.add(new FilterEvent(FilterEventType.END_GROUP, grp));
		
		list.add(new FilterEvent(FilterEventType.SKELETON_UNIT,
			new SkeletonUnit("s7", "</main>")));
		list.add(new FilterEvent(FilterEventType.END_SUBDOCUMENT, subDocRes));

		list.add(new FilterEvent(FilterEventType.END_DOCUMENT, docRes));
	}

	public void cancel() {
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
