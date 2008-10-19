package net.sf.okapi.apptest.dummyfilter;

import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.IInputFilter;

public class DummyInputFilter implements IInputFilter {
	
	private DummyParser parser;
	
	public String getName () {
		return "DummyInputFilter";
	}
	
	public DummyInputFilter () {
		parser = new DummyParser();
	}

	public void cancel () {
		parser.cancel();
	}

	public int next () {
		return parser.next();
	}

	public IResource getResource() {
		return parser.getResource();
	}

	public boolean hasNext() {
		return parser.hasNext();
	}

	public void open (String inputPath) {
		parser.open((InputStream)null); // Fake call
	}

	public void open (URL inputURL) {
		parser.open(inputURL);
	}

	public void open (CharSequence inputText) {
		parser.open(inputText);
	}

	public void setOptions (String language, String defaultEncoding) {
		parser.setOptions(language, defaultEncoding);
	}

	public void close() {
		parser.close();
	}

	public IParameters getParameters () {
		return parser.getParameters();
	}

	public void setParameters (IParameters params) {
		parser.setParameters(params);
	}

}
