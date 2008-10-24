package net.sf.okapi.apptest.filters;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;

public interface IFilter {	

	public String getName();

	public void setOptions(String language, String defaultEncoding);

	public void open(String inputPath);

	public void open(CharSequence inputText);

	public void close();

	public boolean hasNext();

	public FilterEvent next();

	public IResource getResource();

	public void cancel();

	public IParameters getParameters();

	public void setParameters(IParameters params);
}
