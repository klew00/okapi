package net.sf.okapi.filters.html;



import net.sf.okapi.common.Event;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.*;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlSnippetsTest {

	private HtmlFilter htmlFilter;
	private URL parameters;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");

	@Before
	public void setUp() {
		htmlFilter = new HtmlFilter();
		parameters = HtmlSnippetsTest.class.getResource("wellformedConfiguration.yml");
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testHtmlNonWellFormedEmptyTag() {
		String snippet = "<br>text<br/>";
		ArrayList<Event> events = getEvents(snippet);
		TextUnit tu = (TextUnit)events.get(1).getResource();
		List<Code> codes = tu.getSourceContent().getCodes();
		for (Code code : codes) {			
			assertEquals(TagType.PLACEHOLDER, code.getTagType());
		}
	}

	@Test
	public void testMETATag1() {
		String snippet = "<meta http-equiv=\"keywords\" content=\"one,two,three\"/>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testPWithAttributes() {
		String snippet = "<p title='my title' dir='rtl'>Text of p</p>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testLang() {
		String snippet = "<p lang='en'>Text of p</p>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testLangUpdate() {
		String snippet = "<p lang='en'>Text <span lang='en'>text</span> text</p>";
		assertEquals("<p lang='fr'>Text <span lang='fr'>text</span> text</p>", generateOutput(getEvents(snippet),
				snippet, locFR));
	}

	@Test
	public void testMultilangUpdate() {
		String snippet = "<p lang='en'>Text</p><p lang='ja'>JA text</p>";
		assertEquals("<p lang='fr'>Text</p><p lang='ja'>JA text</p>", generateOutput(getEvents(snippet), snippet, locFR));
	}

	@Test
	public void testComplexEmptyElement() {
		String snippet = "<dummy write=\"w\" readonly=\"ro\" trans=\"tu1\" />";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testPWithInlines() {
		String snippet = "<p>Before <b>bold</b> <a href=\"there\"/> after.</p>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testMETATag2() {
		String snippet = "<meta http-equiv=\"Content-Language\" content=\"en\"/>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testPWithInlines2() {
		String snippet = "<p>Before <img href=\"img.png\" alt=\"text\"/> after.</p>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testPWithInlineTextOnly() {
		String snippet = "<p>Before <img alt=\"text\"/> after.</p>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testTableGroups() {
		String snippet = "<table id=\"100\"><tr><td>text</td></tr></table>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testGroupInPara() {
		String snippet = "<p>Text before list:" + "<ul>" + "<li>Text of item 1</li>" + "<li>Text of item 2</li>"
				+ "</ul>" + "and text after the list.</p>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testInput() {
		String snippet = "<p>Before <input type=\"radio\" name=\"FavouriteFare\" value=\"spam\" checked=\"checked\"/> after.</p>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testCollapseWhitespaceWithPre() {
		String snippet = "<pre>   \n   \n   \t    </pre>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testCollapseWhitespaceWithoutPre() {
		String snippet = " <b>   text1\t\r\n\ftext2    </b> ";
		assertEquals("<b> text1 text2 </b>", generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testEscapedCodesInisdePre() {
		String snippet = "<pre><code>&lt;b></code></pre>";
		assertEquals("<pre><code>&lt;b></code></pre>", generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testCdataSection() {
		String snippet = "<![CDATA[&lt;b>]]>";
		assertEquals("<![CDATA[&lt;b>]]>", generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testEscapes() {
		String snippet = "<p><b>Question</b>: When the \"<code>&lt;b></code>\" code was added</p>";
		assertEquals("<p><b>Question</b>: When the &quot;<code>&lt;b></code>&quot; code was added</p>", generateOutput(
				getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testEscapedEntities() {
		String snippet = "&nbsp;M&#x0033;";
		assertEquals("\u00A0M\u0033", generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testNewlineDetection() {
		String snippet = "\r\nX\r\nY\r\n";
		URL originalParameters = parameters;
		parameters = HtmlSnippetsTest.class.getResource("/collapseWhitespaceOff.yml");
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
		parameters = originalParameters;
	}

	@Test
	public void testNormalizeNewlinesInPre() {
		String snippet = "<pre>\r\nX\r\nY\r\n</pre>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testSupplementalSupport() {
		String snippet = "<p>[&#x20000;]=U+D840,U+DC00</p>";
		assertEquals("<p>[\uD840\uDC00]=U+D840,U+DC00</p>", generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void testSimpleSupplementalSupport() {
		String snippet = "&#x20000;";
		assertEquals("\uD840\uDC00", generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void textUnitsInARow() {
		String snippet = "<td><p><h1>para text in a table element</h1></p></td>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void textUnitsInARowWithTwoHeaders() {
		String snippet = "<td><p><h1>header one</h1><h2>header two</h2></p></td>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}
	
	@Test
	public void twoTextUnitsInARowNonWellformed() {
		String snippet = "<td><p><h1>para text in a table element</td>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}
	
	@Test
	public void textUnitName() {
		String snippet = "<p id=\"logo\">para text in a table element</p>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}

	@Test
	public void textUnitStartedWithText() {
		String snippet = "this is some text<x/>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}
	
	@Test
	public void table() {
		String snippet = 
		"<table>" +
		"<tbody><tr valign=\"baseline\">" +
		"<th align=\"right\">" +
		"<strong>Subject</strong>:</th>" +
		"<td align=\"left\">" +
		"ugly <a id=\"KonaLink0\" target=\"top\" class=\"kLink\">stuff</a></td>" +
		"</tr>" +
		"</tbody></table>";
		assertEquals(snippet, generateOutput(getEvents(snippet), snippet, locEN));
	}
	
	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		htmlFilter.setParametersFromURL(parameters);
		htmlFilter.open(new RawDocument(snippet, locEN));
		while (htmlFilter.hasNext()) {
			Event event = htmlFilter.next();
			list.add(event);
		}
		htmlFilter.close();
		return list;
	}

	private String generateOutput(ArrayList<Event> list, String original, LocaleId trgLang) {
		GenericSkeletonWriter writer = new GenericSkeletonWriter();
		StringBuilder tmp = new StringBuilder();
		for (Event event : list) {
			switch (event.getEventType()) {
			case START_DOCUMENT:
				writer.processStartDocument(trgLang, "utf-8", null, new EncoderManager(), (StartDocument) event
						.getResource());
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
		writer.close();
		return tmp.toString();
	}

}
