package net.sf.okapi.common.filters;

import java.io.Reader;
import java.net.URL;

import net.sf.okapi.common.IParameters;

public interface IFilter {	

	public String getName();

	public void setOptions(String language, String defaultEncoding);

	public void open(String inputPath);

	public void open(URL inputURL);

	public void open(CharSequence inputText);
	
	public void open(Reader input);

	public void close();

	public boolean hasNext();

	public Enum<?> next();

	public Object getResource();

	public void cancel();

	public IParameters getParameters();

	public void setParameters(IParameters params);
}
