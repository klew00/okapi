package net.sf.okapi.apptest.filters;

import java.net.URL;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;

public interface IInputFilter {

	public static final int START_DOCUMENT = 0;
	public static final int END_DOCUMENT = 1;
	public static final int START_SUBDOCUMENT = 2;
	public static final int END_SUBDOCUMENT = 3;
	public static final int START_GROUP = 4;
	public static final int END_GROUP = 5;
	public static final int TEXT_UNIT = 6;
	public static final int TEXT_GROUP = 7;
	public static final int SKELETON_UNIT = 8;

	public String getName ();
	
	public void setOptions (String language,
		String defaultEncoding);
	
	public void open (String inputPath);
	
	public void open (URL inputURL);
	
	public void open (CharSequence inputText);
	
	public void close ();
	
	public boolean hasNext ();
	
	public int next ();
	
	public IResource getResource ();
	
	public void cancel ();
	
	public IParameters getParameters ();
	
	public void setParameters (IParameters params);
}
