package net.sf.okapi.apptest.filters;

import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.skeleton.ISkeletonProvider;

public interface IFilter {	

	public String getName ();

	public void setOptions (String language,
		String defaultEncoding,
		ISkeletonProvider skeletonProvider);

	public void open (InputStream input);

	public void open (CharSequence inputText);

	public void open (URL inputURL);

	public void close ();

	public boolean hasNext ();

	public FilterEvent next ();

	public IResource getResource ();

	public void cancel ();

	public IParameters getParameters ();

	public void setParameters (IParameters params);

	public IEncoder getEncoder ();
}
