package net.sf.okapi.common.pipeline;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.resource.RawDocument;

class DocumentData extends BaseContext implements IBatchItemContext {

	List<DocumentDataItem> list;
	public String srcLang;
	public String trgLang;
	
	public DocumentData () {
		list = new ArrayList<DocumentDataItem>();
	}
	
	public String getDefaultEncoding (int index) {
		return list.get(index).defaultEncoding;
	}
	
	public String getFilterConfiguration (int index) {
		return list.get(index).filterConfig;
	}
	
	public URI getInputURI (int index) {
		return list.get(index).inputURI;
	}
	
	public String getOutputEncoding (int index) {
		return list.get(index).outputEncoding;
	}
	
	public String getOutputPath (int index) {
		return list.get(index).outputPath;
	}
	
	public RawDocument getRawDocument (int index) {
		return new RawDocument(list.get(index).inputURI, list.get(index).defaultEncoding,
			srcLang, trgLang);
	}
	
	public String getSourceLanguage () {
		return srcLang;
	}

	public String getTargetLanguage () {
		return trgLang;
	}

}
