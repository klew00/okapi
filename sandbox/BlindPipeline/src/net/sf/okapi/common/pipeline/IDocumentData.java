package net.sf.okapi.common.pipeline;

import java.net.URI;

import net.sf.okapi.common.resource.RawDocument;

public interface IDocumentData {

	public URI getInputURI (int index);
	
	public String getDefaultEncoding (int index);
	
	public String getFilterConfiguration (int index);
	
	public RawDocument getRawDocument (int index);
	
	public String getOutputPath (int index);
	
	public String getOutputEncoding (int index);
	
	public String getSourceLanguage ();
	
	public String getTargetLanguage ();
	
}
