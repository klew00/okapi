package net.sf.okapi.apptest.filters;

import java.io.OutputStream;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.writers.ILayerProvider;

public interface IFilterWriter {

	public String getName();

	public void setOptions(String language, String defaultEncoding);
	
	public void setOutput(String path);

	public void setOutput(OutputStream output);

	public FilterEvent handleEvent(FilterEvent event);

	public void close();

	public IParameters getParameters();

	public void setParameters(IParameters params);
}
