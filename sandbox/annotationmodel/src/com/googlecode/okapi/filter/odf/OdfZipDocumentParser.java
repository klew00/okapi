package com.googlecode.okapi.filter.odf;

import java.io.IOException;

import com.googlecode.okapi.events.EventFactory;
import com.googlecode.okapi.filter.zip.ZipDocumentParser;

public class OdfZipDocumentParser extends ZipDocumentParser{

	public OdfZipDocumentParser(EventFactory factory, String inputFile) throws IOException {
		super(factory, inputFile);
	}
}
