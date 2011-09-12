package net.sf.okapi.filters.xmlstream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
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
		ArrayList<Event> events = XmlStreamTestUtils
				.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
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
		ArrayList<Event> events = XmlStreamTestUtils
				.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("Text1", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("Text2", tu.toString());
	}

	@Test
	public void testAltInImg() {
		String snippet = "Text1<img alt=\"Text2\"/>.";
		ArrayList<Event> events = XmlStreamTestUtils
				.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu); // Attributes go first
		assertEquals("Text2", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("Text1<1/>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testNoExtractValueInInput() {
		String snippet = "<input type=\"file\" value=\"NotText\"/>.";
		ArrayList<Event> events = XmlStreamTestUtils
				.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("<1/>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testExtractValueInInput() {
		String snippet = "<input type=\"other\" value=\"Text\"/>.";
		List<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("Text", tu.toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("<1/>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testLabelInOption() {
		String snippet = "Text1<option label=\"Text2\"/>.";
		ArrayList<Event> events = XmlStreamTestUtils
				.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
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
		ArrayList<Event> events = XmlStreamTestUtils
				.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = events.get(1).getTextUnit();
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
	public void testCdataSectionExtraction () {
		String snippet = "<doc><![CDATA[<b> text]]></doc>";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("<b> text", tu.toString());
	}

	@Test
	public void testCdataSectionExtractionAndWS () {
		String snippet = "<doc><p>&lt; line1\nline2</p><p><![CDATA[< line1\nline2]]></p></doc>";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu1 = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu1);
		assertFalse(tu1.preserveWhitespaces());
		assertEquals("< line1 line2", tu1.toString());
		ITextUnit tu2 = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu2);
		assertFalse(tu1.preserveWhitespaces());
		assertEquals(tu1.toString(), tu2.toString());
	}

	@Test
	public void testCdataSectionAsHTML() {
		parameters = XmlSnippetsTest.class.getResource("/cdataAsHTML.yml");
		IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.html.HtmlFilter");
		xmlStreamFilter.setFilterConfigurationMapper(fcMapper);

		String snippet = "<doc><p>&amp;xmp;=amp</p><p><![CDATA[&amp;=amp]]></p></doc>";
		assertEquals("<doc><p>&amp;xmp;=amp</p><p><![CDATA[&amp;=amp]]></p></doc>", XmlStreamTestUtils.generateOutput(
			XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
			xmlStreamFilter));
	}

	@Test
	public void testCdataSectionAsHTMLButEmpty() {
		parameters = XmlSnippetsTest.class.getResource("/cdataAsHTML.yml");
		IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.html.HtmlFilter");
		xmlStreamFilter.setFilterConfigurationMapper(fcMapper);

		String snippet = "<doc><p><![CDATA[]]></p></doc>";
		assertEquals("<doc><p><![CDATA[]]></p></doc>", XmlStreamTestUtils.generateOutput(
			XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
			xmlStreamFilter));
	}

	@Test
	public void testCdataSectionExtractionWithCondition () {
		parameters = XmlSnippetsTest.class.getResource("/cdataWithConditions.yml");
		IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.html.HtmlFilter");
		xmlStreamFilter.setFilterConfigurationMapper(fcMapper);

		String snippet = "<doc><no><![CDATA[code1 &lt;=lt,&amp;=amp]]></no><no>code2</no><yes><![CDATA[text&lt;=lt,&amp;=amp]]></yes></doc>";
		ArrayList<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("text<=lt,&=amp", tu.toString());
		
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
			XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
			xmlStreamFilter));
		
	}


	@Test
	public void testEscapes() {
		String snippet = "<p><b>Question</b>: When the \"<code>&lt;b></code>\" code was added</p>";
		assertEquals(
				"<p><b>Question</b>: When the &quot;<code>&lt;b></code>&quot; code was added</p>",
				XmlStreamTestUtils.generateOutput(
						XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters),
						snippet, locEN, xmlStreamFilter));
	}
	
	@Test
	public void testEscapes2() {
		String snippet = "<p>&lt;=lt, &amp;=amp, etc. but &amp;amp=escaped amp</p>";
		assertEquals("<p>&lt;=lt, &amp;=amp, etc. but &amp;amp=escaped amp</p>",
				XmlStreamTestUtils.generateOutput(
						XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters),
						snippet, locEN, xmlStreamFilter));
	}

//	@Test
//	public void testEscapes2 () {
//		String snippet = "<p>&lt;=lt, &amp;=amp, etc. but &amp;amp=escaped amp</p>";
//		assertEquals(
//				"<p>&lt;=lt, &amp;=amp, etc. but &amp;amp=escaped amp</p>",
//				XmlStreamTestUtils.generateOutput(
//						XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters),
//						snippet, locEN, xmlStreamFilter));
//	}

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
		ITextUnit tu = FilterTestDriver.getTextUnit(
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

	@Test
	public void testInlineAndExclude() {
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("dita.yml");
		String snippet = "this is text with <ph translate=\"no\"><b>inline</b></ph> exclusions";
		ITextUnit tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 1);
		assertEquals("<ph translate=\"no\"><b>inline</b></ph>", tu.getSource().getFirstContent()
				.getCode(0).getOuterData());
		assertEquals("<b>inline</b>", tu.getSource().getFirstContent().getCode(0).getData());
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
		parameters = originalParameters;
	}

	@Test
	public void testInlineAndExcludeWithTwoExcludes() {
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("dita.yml");
		String snippet = "this is text with <ph translate=\"no\">inline</ph> exclusions <ph translate=\"no\">inline2</ph>";
		ITextUnit tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 1);
		assertEquals("<ph translate=\"no\">inline</ph>", tu.getSource().getFirstContent()
				.getCode(0).getOuterData());
		assertEquals("inline", tu.getSource().getFirstContent().getCode(0).getData());
		assertEquals("<ph translate=\"no\">inline2</ph>",
				tu.getSource().getFirstContent().getCode(1).getOuterData());
		assertEquals("inline2", tu.getSource().getFirstContent().getCode(1).getData());
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
		parameters = originalParameters;
	}

	@Test
	public void testInlineAndNotExclude() {
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("dita.yml");
		String snippet = "this is text with <ph translate=\"yes\">inline</ph> exclusions";
		ITextUnit tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 1);
		assertEquals("<ph translate=\"yes\">", tu.getSource().getFirstContent().getCode(0)
				.toString());
		assertEquals("</ph>", tu.getSource().getFirstContent().getCode(1).toString());
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
		parameters = originalParameters;
	}

	@Test
	public void testInlineAndExcludeEmbedded() {
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("dita.yml");
		String snippet = "<ph translate=\"yes\">this is text with <ph translate=\"no\">inline</ph> exclusions</ph>";
		ITextUnit tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 1);
		assertEquals("<ph translate=\"yes\">", tu.getSource().getFirstContent().getCode(0)
				.toString());
		assertEquals("<ph translate=\"no\">inline</ph>", tu.getSource().getFirstContent()
				.getCode(1).getOuterData());
		assertEquals("inline", tu.getSource().getFirstContent().getCode(1).getData());
		assertEquals("</ph>", tu.getSource().getFirstContent().getCode(2).toString());
		assertEquals(snippet, XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter));
		parameters = originalParameters;
	}

	// FIXME: Handle embedded translate=no/yes elements @Test
	public void testInlineAndNotExcludeEmbedded() {
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("dita.yml");
		String snippet = "<ph translate=\"no\">this is text with <ph translate=\"yes\">inline</ph> exclusions</ph>";
		ITextUnit tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 1);
		assertEquals("<ph translate=\"no\">this is text with ", tu.getSource().getFirstContent()
				.getCode(0).getOuterData());
		assertEquals("<ph translate=\"yes\">", tu.getSource().getFirstContent().getCode(1)
				.getOuterData());
		assertEquals("</ph>", tu.getSource().getFirstContent().getCode(2).getOuterData());
		assertEquals(" exclusions</ph>", tu.getSource().getFirstContent().getCode(3).getOuterData());
		assertFalse(snippet.equals(XmlStreamTestUtils.generateOutput(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), snippet, locEN,
				xmlStreamFilter)));
		parameters = originalParameters;
	}

	@Test
	public void testXmlIdResname() {
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("dita.yml");
		String snippet = "<note id=\"v512165_fr-fr\" type=\"other\" othertype=\"WARNING\">Some text here... </note>";
		ITextUnit tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 1);
		assertNull(tu.getName());

		snippet = "<note xml:id=\"v512165_fr-fr\" type=\"other\" othertype=\"WARNING\">Some text here... </note>";
		tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 2);
		assertEquals("v512165_fr-fr-xml:id", tu.getName());
		parameters = originalParameters;
	}

	@Test
	public void testConditionalInlineWithAttribute() {
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("dita.yml");
		String snippet = "<p>TEST: <image href=\"bike.gif\" alt=\"text in alt\"/> more text</p>";
		List<Event> events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		ITextUnit tu1 = FilterTestDriver.getTextUnit(events, 1);
		ITextUnit tu2 = FilterTestDriver.getTextUnit(events, 2);
		assertEquals("text in alt", tu1.toString());
		assertEquals("TEST: <image href=\"bike.gif\" [#$tu2]/> more text", tu2.toString());

		snippet = "<p>TEST: <image placement=\"break\" href=\"bike.gif\" alt=\"text in alt\"/> more text</p>";
		events = XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters);
		tu1 = FilterTestDriver.getTextUnit(events, 1);
		tu2 = FilterTestDriver.getTextUnit(events, 2);
		ITextUnit tu3 = FilterTestDriver.getTextUnit(events, 3);
		assertEquals("text in alt", tu1.toString());
		assertEquals("TEST:", tu2.toString());
		assertEquals("more text", tu3.toString());

		parameters = originalParameters;
	}
	
	@Test
	public void testBadCodeIdsAfterRenumber() {
		String snippet = "A<xref href=\"https://consultant.familysearch.org\" scope=\"external\"\n                              format=\"html\">B</xref>C";
		URL originalParameters = parameters;
		parameters = XmlSnippetsTest.class.getResource("dita.yml");
		ITextUnit tu = FilterTestDriver.getTextUnit(
				XmlStreamTestUtils.getEvents(snippet, xmlStreamFilter, parameters), 1);
		assertNotNull(tu);
		List<Code> list = tu.getSource().getFirstContent().getCodes();
		assertEquals(2, list.size());
		assertEquals(1, list.get(0).getId());
		assertEquals(1, list.get(1).getId());
		
		tu.getSource().getFirstContent().renumberCodes();
		
		list = tu.getSource().getFirstContent().getCodes();
		assertEquals(2, list.size());
		assertEquals(1, list.get(0).getId());
		assertEquals(1, list.get(1).getId());
		
		parameters = originalParameters;
	}
}
