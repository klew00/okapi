package net.sf.okapi.common.pipeline;

import java.net.URI;

import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;

public class PipelineContext implements IDocumentData {

	private IDocumentData docData;
	private IFilterConfigurationMapper configMapper;
	
	
	public void setFilterConfigurationMapper (IFilterConfigurationMapper configMapper) {
		this.configMapper = configMapper;
	}
	
	public IFilterConfigurationMapper getFilterConfigurationMapper () {
		return configMapper;
	}
	
	public void setDocumentData (IDocumentData docData) {
		this.docData = docData;
	}
	
	public String getDefaultEncoding (int index) {
		return docData.getDefaultEncoding(index);
	}

	public String getFilterConfiguration (int index) {
		return docData.getFilterConfiguration(index);
	}

	public URI getInputURI (int index) {
		return docData.getInputURI(index);
	}

	public String getOutputEncoding (int index) {
		return docData.getOutputEncoding(index);
	}

	public String getOutputPath (int index) {
		return docData.getOutputPath(index);
	}

	public RawDocument getRawDocument (int index) {
		return docData.getRawDocument(index);
	}

	public String getSourceLanguage () {
		return docData.getSourceLanguage();
	}

	public String getTargetLanguage () {
		return docData.getTargetLanguage();
	}
	
}
