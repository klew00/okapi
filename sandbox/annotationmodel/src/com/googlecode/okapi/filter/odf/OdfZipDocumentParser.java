package com.googlecode.okapi.filter.odf;

import java.io.IOException;

import com.googlecode.okapi.filter.zip.ZipDocumentParser;
import com.googlecode.okapi.resource.EventFactory;

public class OdfZipDocumentParser extends ZipDocumentParser{

	public OdfZipDocumentParser(EventFactory factory, String inputFile) throws IOException {
		super(factory, inputFile);
	}
}
