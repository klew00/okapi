/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.filters.openxml;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
//import net.sf.okapi.common.skeleton.GenericSkeleton;
//import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.filters.openxml.OpenXMLContentFilter;
import net.sf.okapi.filters.openxml.OpenXMLContentSkeletonWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This is a test that tests OpenXMLContentFilter for short spans of tags.
 */

public class OpenXMLSnippetsTest {

	private static Logger LOGGER;
	private OpenXMLContentFilter openXMLContentFilter;
	public final static int MSWORD=1;
	public final static int MSEXCEL=2;
	public final static int MSPOWERPOINT=3;
	public final static int MSWORDCHART=4; // DWH 4-16-09
	public final static int MSEXCELCOMMENT=5; // DWH 5-13-09
	public final static int MSWORDDOCPROPERTIES=6; // DWH 5-25-09
	private String snappet;
	private LocaleId locENUS = LocaleId.fromString("en-us");
	
	@Before
	public void setUp()  {
		LOGGER = LoggerFactory.getLogger(OpenXMLSnippetsTest.class.getName());
		openXMLContentFilter = new OpenXMLContentFilter();	
		openXMLContentFilter.setLogger(LOGGER);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testInlineLanguageWithText() {
		String snippet = "<w:p><w:r><w:lang w:val=\"en-us\"/><w:t>zorcon</w:t></w:r></w:p>";
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, snippet);
	}

	@Test
	public void testInsertion() {
		String snippet = "<w:p><w:ins><w:r><w:t xml:space=\"preserve\">zorcon</w:t></w:r></w:ins></w:p>";
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, snippet);
	}

	@Test
	public void testBareText() {
		String snippet = "<w:p><w:r><w:t xml:space=\"preserve\">zorcon</w:t></w:r></w:p>";
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, snippet);
	}

	@Test
	public void testOneWord() {
		String snippet = "<w:p><w:r><w:rPr><w:lang w:val=\"en-us\"/><w:b/><w:bCs></w:rPr><w:t>zorcon</w:t></w:r></w:p>";
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, snippet);
	}

	@Test
	public void testInlineLanguage() {
		String snippet = "<w:p><w:r><w:lang w:val=\"en-us\"/></w:r></w:p>";
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
		String snippet = "<w:p><w:r><w:rPr><w:lang w:val=\"en-us\" w:eastAsia=\"zh-TW\"/></w:rPr></w:r><wp:docPr name=\"Picture 1\"><pic:cNvPr name=\"Picture 1\"><a:stretch/></w:p>";				
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, snippet);
	}

	@Test
	public void testAuthor() {
		String snippet = "<comments><author>Dan Higinbotham</author></comments>";
		snappet = generateOutput(getEvents(snippet, MSEXCEL), snippet);
		assertEquals(snappet, snippet);
	}

/*
	@Test this has to be tested in OpenXMLFilter
	public void testSquishSpace() {
		String snippet = "<w:p><w:r><w:t space=\"preserve\">a </w:t></w:r><w:r><w:t>dog</w:t></w:r></w:p>";
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, "<w:p><w:r><w:t space=\"preserve\">a dog</w:t></w:r></w:p>");
	}

	@Test this has to be tested in OpenXMLFilter
	public void testSquishRsid() {
		String snippet = "<w:p w:rsidR=\"00126310\" w:rsidRDefault=\"00AE7E85\"><w:r w:rsidR=\"00402C87\"><w:t>pickle</w:t></w:r></w:p>";
		snappet = generateOutput(getEvents(snippet, MSWORD), snippet);
		assertEquals(snappet, "<w:p><w:r><w:t>pickle</w:t></w:r></w:p>");
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
		openXMLContentFilter.setLogger(LOGGER);
		openXMLContentFilter.setUpConfig(filetype);
		openXMLContentFilter.open(new RawDocument(snippet, locENUS));
		while (openXMLContentFilter.hasNext()) {
			Event event = openXMLContentFilter.next();
			openXMLContentFilter.displayOneEvent(event);
			list.add(event);
		}
		openXMLContentFilter.close();
		return list;
	}

	private String generateOutput(ArrayList<Event> list, String original) {
		int configurationType=openXMLContentFilter.getConfigurationType();
		OpenXMLContentSkeletonWriter writer = new OpenXMLContentSkeletonWriter(configurationType);
		StringBuilder tmp = new StringBuilder();
		for (Event event : list) {
			switch (event.getEventType()) {
			case START_DOCUMENT:
				writer.processStartDocument(locENUS, "utf-8", null,
					openXMLContentFilter.getEncoderManager(),
					(StartDocument)event.getResource());
				break;
			case TEXT_UNIT:
				ITextUnit tu = event.getTextUnit();
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

		// TZU LOGGER.setUseParentHandlers(false);
		LOGGER.debug("nOriginal: "+original);
		LOGGER.debug("Output:    "+tmp.toString());
		writer.close();
		return tmp.toString();
	}
}
