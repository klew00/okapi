package net.sf.okapi.common.resource;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;

public class RawDocumentTest {

	LocaleId locEN = LocaleId.fromString("en");
	
	@Test
	public void getReaderWithURI() throws URISyntaxException, IOException {
		RawDocument ir = new RawDocument(RawDocumentTest.class.getResource("/test.html").toURI(), "UTF-8", locEN);
		Reader r = ir.getReader();
		assertTrue(r.ready());
	}

	@Test(expected = OkapiIOException.class)
	public void getReaderWithExceptionUriNotFound() throws URISyntaxException {
		RawDocument ir = new RawDocument(new URI("file:///home/username/bad.bad"), "UTF-8", locEN);
		ir.getReader();
	}
	
	@Test(expected = OkapiIOException.class)
	public void getReaderWithExceptionIllegalUri() throws URISyntaxException {
		RawDocument ir = new RawDocument(new URI("/bad/bad"), "UTF-8", locEN);
		ir.getReader();
	}

}
