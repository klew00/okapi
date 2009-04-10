package net.sf.okapi.filters.openxml.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.InputResource;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
//import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.filters.openxml.OpenXMLContentFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


//TODO: Test for properties
public class OpenXMLSnippetsTest {
	private OpenXMLContentFilter openXMLContentFilter;
	private static final int MSWORD=1;
	private static final int MSEXCEL=2;
	private static final int MSPOWERPOINT=3;
	private String snappet;
	
	@Before
	public void setUp()  {
		openXMLContentFilter = new OpenXMLContentFilter();	
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testInlineLanguage() {
		String snippet = "<w:p><w:lang w:val=\"en-US\"/></w:p>";
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, snippet);
	}

	@Test
	public void testInlineTranslatable() {
		String snippet = "<w:p><wp:docPr id=\"2\" name=\"Picture 1\"></w:p>";
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, snippet);
	}

	@Test
	public void testLostDocParts() {
		String snippet = "<w:p><w:rPr><w:lang w:val=\"en-US\" w:eastAsia=\"zh-TW\"/></w:rPr><wp:docPr name=\"Picture 1\"><pic:cNvPr name=\"Picture 1\"><a:stretch/></w:p>";				
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, snippet);
	}
/*
	@Test
	public void testAuthor() {
		String snippet = "<comments><author>Dan Higinbotham</author></comments>";
		snappet = generateOutput(getEvents(snippet, MSEXCEL), snippet);
		assertEquals(snappet, snippet);
	}

	@Test
	public void testComplexEmptyElement() {
		String snippet = "<dummy write=\"w\" readonly=\"ro\" trans=\"tu1\" />";
		assertEquals(generateOutput(getEvents(snippet, MSWORD), snippet), snippet);
	}

	@Test
	public void testPWithInlines() {
		String snippet = "<p>Before <b>bold</b> <a href=\"there\"/> after.</p>";
		assertEquals(generateOutput(getEvents(snippet, MSWORD), snippet), snippet);
	}

	@Test
	public void testMETATag2() {
		String snippet = "<meta http-equiv=\"Content-Language\" content=\"en\"/>";
		assertEquals(generateOutput(getEvents(snippet, MSWORD), snippet), snippet);
	}
	
	@Test
	public void testPWithInlines2() {
		String snippet = "<p>Before <img href=\"img.png\" alt=\"text\"/> after.</p>";
		assertEquals(generateOutput(getEvents(snippet, MSWORD), snippet), snippet);
	}
	
	@Test
	public void testTableGroups() {
		String snippet = "<table id=\"100\"><tr><td>text</td></tr></table>";
		assertEquals(generateOutput(getEvents(snippet, MSWORD), snippet), snippet);
	}
	
	@Test
	public void testGroupInPara() {
		String snippet = "<p>Text before list:"
			 + "<ul>"
			 + "<li>Text of item 1</li>"
			 + "<li>Text of item 2</li>"
			 + "</ul>"
			 + "and text after the list.</p>";
		assertEquals(generateOutput(getEvents(snippet, MSWORD), snippet), snippet);
	}
*/	
	private ArrayList<Event> getEvents(String snippet, int filetype) {
		ArrayList<Event> list = new ArrayList<Event>();
		openXMLContentFilter.setUpConfig(filetype);
		openXMLContentFilter.open(new InputResource(snippet, "en"));
		while (openXMLContentFilter.hasNext()) {
			Event event = openXMLContentFilter.next();
			openXMLContentFilter.displayOneEvent(event);
			list.add(event);
		}
		openXMLContentFilter.close();
		return list;
	}

	private String generateOutput(ArrayList<Event> list, String original) {
		GenericSkeletonWriter writer = new GenericSkeletonWriter();
		StringBuilder tmp = new StringBuilder();
		for (Event event : list) {
			switch (event.getEventType()) {
			case START_DOCUMENT:
				writer.processStartDocument("en-US", "utf-8", null, new EncoderManager(),
					(StartDocument)event.getResource());
				break;
			case TEXT_UNIT:
				TextUnit tu = (TextUnit) event.getResource();
				tmp.append(writer.processTextUnit(tu));
				break;
			case DOCUMENT_PART:
				DocumentPart dp = (DocumentPart) event.getResource();
				tmp.append(writer.processDocumentPart(dp));
				break;
			case START_GROUP:
				StartGroup startGroup = (StartGroup) event.getResource();
				tmp.append(writer.processStartGroup(startGroup));
				break;
			case END_GROUP:
				Ending ending = (Ending) event.getResource();
				tmp.append(writer.processEndGroup(ending));
				break;
			}
		}		
		System.out.println("\nOriginal: "+original);
		System.out.println("Output:   "+tmp.toString()+"\n");
		writer.close();
		return tmp.toString();
	}
}
