package net.sf.okapi.apptest.dummyfilter;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.filters.IInputFilter;
import net.sf.okapi.apptest.filters.IParser;
import net.sf.okapi.apptest.resource.Group;
import net.sf.okapi.apptest.resource.IContainable;
import net.sf.okapi.apptest.resource.SkeletonUnit;
import net.sf.okapi.apptest.resource.TextUnit;

public class DummyParser implements IParser {

	ArrayList<IContainable> resList;
	ArrayList<Integer> tokList;
	private int current;
	private boolean canceled;
	
	public void setOptions (String language,
		String defaultEncoding)
	{
		// Not used in this parser
		System.out.println("DummyParser: setOptions() called");
		System.out.println(" -output language = " + language);
		System.out.println(" -output default encoding = " + defaultEncoding);
	}
	
	public void open (InputStream input) {
		System.out.println("DummyParser: open(stream) called");
		resetResources();
	}
	
	public void close () {
		System.out.println("DummyParser: close() called");
		resList.clear();
		resList = null;
	}
	
	public boolean hasNext () {
		if ( resList == null ) return false;
		return (current<resList.size()-1);
	}
	
	public int next () {
		if ( canceled ) {
			if ( current < resList.size()-1 ) {
				current = resList.size()-2;
			}
		}
		current++;
		return tokList.get(current);
	}
	
	public IContainable getResource () {
		return resList.get(current);
	}
	
	private void resetResources () {
		canceled = false;
		current = -1;
		
		resList = new ArrayList<IContainable>();
		tokList = new ArrayList<Integer>();
		
		tokList.add(IInputFilter.START_DOCUMENT);
		Group docRes = new Group();
		docRes.setID("d1");
		resList.add(docRes);
		
		tokList.add(IInputFilter.START_SUBDOCUMENT);
		Group subDocRes = new Group();
		subDocRes.setID("sd1");
		resList.add(subDocRes);
		
		tokList.add(IInputFilter.SKELETON_UNIT);
		resList.add(new SkeletonUnit("s1", "<t id='t1'>"));

		tokList.add(IInputFilter.TEXT_UNIT);
		resList.add(new TextUnit("t1", "Text 1"));
		
		tokList.add(IInputFilter.SKELETON_UNIT);
		resList.add(new SkeletonUnit("s2", "</t>\n"));

		for ( int i=1; i<=10; i++ ) {
			tokList.add(IInputFilter.SKELETON_UNIT);
			resList.add(new SkeletonUnit("sa"+String.valueOf(i), "<t>"));
			tokList.add(IInputFilter.TEXT_UNIT);
			resList.add(new TextUnit("at"+String.valueOf(i), "Auto text "+String.valueOf(i)));
			tokList.add(IInputFilter.SKELETON_UNIT);
			resList.add(new SkeletonUnit("sa"+String.valueOf(i), "</t>\n"));
		}
		
		tokList.add(IInputFilter.START_GROUP);
		Group grp = new Group();
		grp.setID("g1");
		resList.add(grp);

		tokList.add(IInputFilter.SKELETON_UNIT);
		resList.add(new SkeletonUnit("s3", "<grp>\n"));

		tokList.add(IInputFilter.SKELETON_UNIT);
		resList.add(new SkeletonUnit("s4", "<t id='t2'>"));

		tokList.add(IInputFilter.TEXT_UNIT);
		resList.add(new TextUnit("t2", "Text 2"));
		
		tokList.add(IInputFilter.SKELETON_UNIT);
		resList.add(new SkeletonUnit("s5", "</t>\n"));

		tokList.add(IInputFilter.SKELETON_UNIT);
		resList.add(new SkeletonUnit("s6", "</grp>\n"));

		tokList.add(IInputFilter.END_GROUP);
		resList.add(grp);
		
		tokList.add(IInputFilter.END_SUBDOCUMENT);
		resList.add(subDocRes);

		tokList.add(IInputFilter.END_DOCUMENT);
		resList.add(docRes);
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
