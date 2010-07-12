package net.sf.okapi.filters.xmlstream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.*;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.filters.xmlstream.integration.XmlStreamTestUtils;

import org.junit.After;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class XmlSnippetsTest {

	private XmlStreamFilter xmlStreamFilter;
	private URL parameters;
	private LocaleId locEN = LocaleId.fromString("en");
	private GenericContent fmt = new GenericContent();

	@Before
	public void setUp() {
		xmlStreamFilter = new XmlStreamFilter();
		parameters = XmlSnippetsTest.class.getResource("/wellformedConfiguration.yml");
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testMultipleMETA() {
		String snippet = "<html>" + "<meta name=\"keywords\" content=\"Text1\"/>"
				+ "<meta name=\"creation_date\" content=\"May 24, 2001\"/>"
				+ "<meta name=\"DESCRIPTION\" content=\"Text2\"/>" + "<p>Text3</p>" + "</html>";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		TextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("Text1", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("Text2", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 3);
		assertNotNull(tu);
		assertEquals("Text3", tu.toString());
	}

	@Test
	public void testTitleInP() {
		String snippet = "<p title=\"Text1\">Text2</p>";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		TextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("Text1", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("Text2", tu.toString());
	}

	@Test
	public void testAltInImg() {
		String snippet = "Text1<img alt=\"Text2\"/>.";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		TextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu); // Attributes go first
		assertEquals("Text2", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("Text1<1/>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testNoExtractValueInInput() {
		String snippet = "<input type=\"file\" value=\"NotText\"/>.";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		TextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("<1/>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testExtractValueInInput() {
		String snippet = "<input type=\"other\" value=\"Text\"/>.";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		TextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("Text", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("<1/>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testLabelInOption() {
		String snippet = "Text1<option label=\"Text2\"/>.";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		TextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu); // Attributes go first
		assertEquals("Text2", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("Text1", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 3);
		assertNotNull(tu);
		assertEquals(".", tu.toString());
	}

	@Test
	public void testHtmlNonWellFormedEmptyTag() {
		String snippet = "<br>text<br/>";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		TextUnit tu = (TextUnit) events.get(1).getResource();
		List<Code> codes = tu.getSource().getFirstContent().getCodes();
		for (Code code : codes) {
			assertEquals(TagType.PLACEHOLDER, code.getTagType());
		}
	}

	@Test
	public void testMETATag1() {
		String snippet = "<meta http-equiv=\"keywords\" content=\"one,two,three\"/>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testPWithAttributes() {
		String snippet = "<p title='my title' dir='rtl'>Text of p</p>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testLang() {
		String snippet = "<p xml:lang='en'>Text of p</p>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testComplexEmptyElement() {
		String snippet = "<dummy write=\"w\" readonly=\"ro\" trans=\"tu1\" />";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testPWithInlines() {
		String snippet = "<p>Before <b>bold</b> <a href=\"there\"/> after.</p>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testMETATag2() {
		String snippet = "<meta http-equiv=\"Content-Language\" content=\"en\"/>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testPWithInlines2() {
		String snippet = "<p>Before <img href=\"img.png\" alt=\"text\"/> after.</p>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testPWithInlineTextOnly() {
		String snippet = "<p>Before <img alt=\"text\"/> after.</p>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testTableGroups() {
		String snippet = "<table id=\"100\"><tr><td>text</td></tr></table>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testGroupInPara() {
		String snippet = "<p>Text before list:" + "<ul>" + "<li>Text of item 1</li>"
				+ "<li>Text of item 2</li>" + "</ul>" + "and text after the list.</p>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testInput() {
		String snippet = "<p>Before <input type=\"radio\" name=\"FavouriteFare\" value=\"spam\" checked=\"checked\"/> after.</p>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testCollapseWhitespaceWithPre() {
		String snippet = "<pre>   \n   \n   \t    </pre>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testCollapseWhitespaceWithoutPre() {
		String snippet = " <b>   text1\t\r\n\ftext2    </b> ";
		assertEquals("<b> text1 text2 </b>", XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testEscapedCodesInisdePre() {
		String snippet = "<pre><code>&lt;b></code></pre>";
		assertEquals("<pre><code>&lt;b></code></pre>", XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testCdataSection() {
		String snippet = "<![CDATA[&lt;b>]]>";
		assertEquals("<![CDATA[&lt;b>]]>", XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testEscapes() {
		String snippet = "<p><b>Question</b>: When the \"<code>&lt;b></code>\" code was added</p>";
		assertEquals(
				"<p><b>Question</b>: When the &quot;<code>&lt;b></code>&quot; code was added</p>",
				XmlStreamTestUtils.generateOutput(
						XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet,
						locEN, xmlStreamFilter));
	}

	@Test
	public void testEscapedEntities() {
		String snippet = "&nbsp;M&#x0033;";
		assertEquals("\u00A0M\u0033", XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testNewlineDetection() {
		String snippet = "\r\nX\r\nY\r\n";
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("/collapseWhitespaceOff.yml");
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
		parameters = originalParameters;
	}

	@Test
	public void testCodeFinder() {
		String snippet = "<p>text notVAR1 VAR2<p>";
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("/withCodeFinderRules.yml");
		TextUnit tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 1);
		assertNotNull(tu);
		List<Code> list = tu.getSource().getFirstContent().getCodes();
		assertEquals(2, list.size());
		assertEquals("e", list.get(0).getData());
		assertEquals("VAR2", list.get(1).getData());
		parameters = originalParameters;
	}

	@Test
	public void testNormalizeNewlinesInPre() {
		String snippet = "<pre>\r\nX\r\nY\r\n</pre>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testSupplementalSupport() {
		String snippet = "<p>[&#x20000;]=U+D840,U+DC00</p>";
		assertEquals("<p>[\uD840\uDC00]=U+D840,U+DC00</p>", XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void testSimpleSupplementalSupport() {
		String snippet = "&#x20000;";
		assertEquals("\uD840\uDC00", XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void textUnitsInARow() {
		String snippet = "<td><p><h1>para text in a table element</h1></p></td>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void textUnitsInARowWithTwoHeaders() {
		String snippet = "<td><p><h1>header one</h1><h2>header two</h2></p></td>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test(expected = OkapiBadFilterInputException.class)
	public void twoTextUnitsInARowNonWellformed() {
		String snippet = "<td><p><h1>para text in a table element</td>";
		XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
	}

	@Test
	public void textUnitName() {
		String snippet = "<p id=\"logo\">para text in a table element</p>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	@Test
	public void textUnitStartedWithText() {
		String snippet = "this is some text<x/>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}

	/*
	 * Issue 126: Problem with un-quoted translatable attributes This will fail until fixed!!!
	 * @Test public void textWithUnquotedAttribtes() { String snippet = "<img alt=R&amp;D src=image.png>";
	 * assertEquals("<img alt=\"R&amp;D\" src=\"image.png\">", generateOutput(getEvents(snippet), snippet, locEN)); }
	 */

	@Test
	public void table() {
		String snippet = "<table>" + "<tbody><tr valign=\"baseline\">" + "<th align=\"right\">"
				+ "<strong>Subject</strong>:</th>" + "<td align=\"left\">"
				+ "ugly <a id=\"KonaLink0\" target=\"top\" class=\"kLink\">stuff</a></td>"
				+ "</tr>" + "</tbody></table>";
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
	}
}
