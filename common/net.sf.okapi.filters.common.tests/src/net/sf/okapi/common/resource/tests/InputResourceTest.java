package net.sf.okapi.common.resource.tests;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.InputResource;

public class InputResourceTest {

	@Test
	public void getReaderWithURI() throws URISyntaxException, IOException {
		InputResource ir = new InputResource(InputResourceTest.class.getResource("/test.html").toURI(), "UTF-8", "en");
		Reader r = ir.getReader();
		assertTrue(r.ready());
	}

	@Test(expected = OkapiIOException.class)
	public void getReaderWithExceptionUriNotFound() throws URISyntaxException {
		InputResource ir = new InputResource(new URI("file:///home/username/bad.bad"), "UTF-8", "en");
		ir.getReader();
	}
	
	@Test(expected = OkapiIOException.class)
	public void getReaderWithExceptionIllegalUri() throws URISyntaxException {
		InputResource ir = new InputResource(new URI("/bad/bad"), "UTF-8", "en");
		ir.getReader();
	}
}
