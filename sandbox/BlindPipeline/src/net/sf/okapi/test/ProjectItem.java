package net.sf.okapi.test;

import java.io.File;
import java.net.URI;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.IDocumentData;
import net.sf.okapi.common.resource.RawDocument;

public class ProjectItem implements IDocumentData {

	public String[] inputPaths;
	public String[] encodings;
	public String[] filterConfigs;
	
	public ProjectItem () {
		inputPaths = new String[3];
		encodings = new String[3];
		filterConfigs = new String[3];
	}

	public String getDefaultEncoding (int index) {
		return encodings[index];
	}

	public String getFilterConfiguration (int index) {
		return filterConfigs[index];
	}

	public URI getInputURI (int index) {
		File file = new File(inputPaths[index]);
		return file.toURI();
	}

	public String getOutputEncoding (int index) {
		// Same as input
		return encodings[index];
	}

	public String getOutputPath (int index) {
		return Util.getFilename(inputPaths[index], false)
			+ ".out" + Util.getExtension(inputPaths[index]);
	}

	public String getSourceLanguage () {
		return "en";
	}

	public String getTargetLanguage() {
		return "br";
	}

	public RawDocument getRawDocument (int index) {
		return new RawDocument(getInputURI(index), getDefaultEncoding(index),
			getSourceLanguage(), getTargetLanguage());
	}

}
