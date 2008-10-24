package net.sf.okapi.apptest.dummyfilter;

import java.io.InputStream;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilter;

public class DummyFilter implements IFilter {

	private DummyParser parser;
	
	public DummyFilter () {
		parser = new DummyParser();
	}

	public void cancel() {
		parser.cancel();
	}

	public void close() {
		parser.close();
	}

	public String getName() {
		return "DummyFilter";
	}

	public IParameters getParameters() {
		return parser.getParameters();
	}

	public IResource getResource() {
		return parser.getResource();
	}

	public boolean hasNext () {
		return parser.hasNext();
	}

	public FilterEvent next() {
		return parser.next();
	}

	public void open(String inputPath) {
		// Real filter would create the stream
		parser.open((InputStream)null);
	}

	public void open(CharSequence inputText) {
		parser.open(inputText);
	}

	public void setOptions(String language,
		String defaultEncoding)
	{
		parser.setOptions(language, defaultEncoding);
	}

	public void setParameters(IParameters params) {
		parser.setParameters(params);
	}

}
