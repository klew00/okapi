package net.sf.okapi.common.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;
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

	@Test
	public void testOutputIsDifferentFromInput ()
		throws URISyntaxException, FileNotFoundException
	{
		String root = TestUtil.getParentDir(this.getClass(), "/safeouttest1.txt");
		File expectedFile = new File(Util.toURI(root+"safeouttest1.out.txt"));
		expectedFile.delete();
		
		RawDocument rd = new RawDocument(Util.toURI(root+"safeouttest1.txt"), "UTF-8", LocaleId.ENGLISH);
		writeStringAndFinalize(expectedFile.toURI(), rd, "test1");
		
		assertTrue(expectedFile.exists());
		assertEquals(5, expectedFile.length());
	}

	@Test
	public void testOutputIsSameAsInput ()
		throws URISyntaxException, FileNotFoundException
	{
		String root = TestUtil.getParentDir(this.getClass(), "/safeouttest1.out.txt");
		File expectedFile = new File(Util.toURI(root+"safeouttest1.out.txt"));
		expectedFile.delete();
		
		RawDocument rd = new RawDocument(Util.toURI(root+"safeouttest1.out.txt"), "UTF-8", LocaleId.ENGLISH);
		writeStringAndFinalize(expectedFile.toURI(), rd, "test1+");
		
		assertTrue(expectedFile.exists());
		assertEquals(6, expectedFile.length());
	}

	private void writeStringAndFinalize (URI outputURI,
		RawDocument rd,
		String text)
		throws FileNotFoundException
	{
		File file = rd.createOutputFile(outputURI);
		PrintWriter writer = new PrintWriter(file);
		writer.write(text);
		writer.close();
		rd.finalizeOutput();
	}
	
}
