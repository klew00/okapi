package net.sf.okapi.apptest.filters;

import java.io.InputStream;
import java.util.ArrayList;

import net.sf.okapi.common.filters.IParser.ParserTokenType;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class DummyParser {

	ArrayList<IContainable> resList;
	ArrayList<ParserTokenType> tokList;
	private int current;
	
	public void open (InputStream input) {
		resetResources();
		current = -1;
	}
	
	public void close () {
		resList.clear();
		resList = null;
	}
	
	public boolean hasNext () {
		if ( resList == null ) return false;
		return (current<resList.size()-2);
	}
	
	public ParserTokenType next () {
		current++;
		return tokList.get(current);
	}
	
	public IContainable getResource () {
		return resList.get(current);
	}
	
	private void resetResources () {
		resList = new ArrayList<IContainable>();
		tokList = new ArrayList<ParserTokenType>();
		
		tokList.add(ParserTokenType.SKELETON);
		resList.add(new SkeletonUnit("s1", "<t id='t1'>"));

		tokList.add(ParserTokenType.TRANSUNIT);
		resList.add(new TextUnit("t1", "Text 1"));
		
		tokList.add(ParserTokenType.SKELETON);
		resList.add(new SkeletonUnit("s2", "</t>\n"));

		for ( int i=1; i<=10; i++ ) {
			tokList.add(ParserTokenType.SKELETON);
			resList.add(new SkeletonUnit("sa"+String.valueOf(i), "<t>"));
			tokList.add(ParserTokenType.TRANSUNIT);
			resList.add(new TextUnit("at"+String.valueOf(i), "Auto text "+String.valueOf(i)));
			tokList.add(ParserTokenType.SKELETON);
			resList.add(new SkeletonUnit("sa"+String.valueOf(i), "</t>\n"));
		}
		
		tokList.add(ParserTokenType.STARTGROUP);
		Group grp = new Group();
		grp.setID("g1");
		resList.add(grp);

		tokList.add(ParserTokenType.SKELETON);
		resList.add(new SkeletonUnit("s3", "<grp>\n"));

		tokList.add(ParserTokenType.SKELETON);
		resList.add(new SkeletonUnit("s4", "<t id='t2'>"));

		tokList.add(ParserTokenType.TRANSUNIT);
		resList.add(new TextUnit("t2", "Text 2"));
		
		tokList.add(ParserTokenType.SKELETON);
		resList.add(new SkeletonUnit("s5", "</t>\n"));

		tokList.add(ParserTokenType.SKELETON);
		resList.add(new SkeletonUnit("s6", "</grp>\n"));

		tokList.add(ParserTokenType.ENDGROUP);
		resList.add(grp);
		
		tokList.add(ParserTokenType.ENDINPUT);
	}
}
