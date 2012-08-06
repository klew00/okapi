/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.xml;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment.TagType;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.w3c.its.ITSException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class XMLFilterTest {

	private XMLFilter filter;
	private GenericContent fmt;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");

	@Before
	public void setUp() {
		filter = new XMLFilter();
		fmt = new GenericContent();
		root = TestUtil.getParentDir(this.getClass(), "/test01.xml");
	}

	@Test
	public void testSpecialEntities () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc>"
			+ "<p>&lt;=lt &gt;=gt &quot;=quot &apos;=apos, &#x00a0;=nbsp</p>"
			+ "</doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc>"
			+ "<p>&lt;=lt &gt;=gt &quot;=quot '=apos, &#x00a0;=nbsp</p>"
			+ "</doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testSpecialEntitiesWithOptions () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc>"
			+ "<p>&lt;=lt &gt;=gt &quot;=quot &apos;=apos, &#x00a0;=nbsp</p>"
			+ "</doc>";
		// No translatable -> allow to not escape apos and quot.
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc>"
			+ "<p>&lt;=lt >=gt \"=quot '=apos, \u00a0=nbsp</p>"
			+ "</doc>";
		String paramData = "<?xml version=\"1.0\"?>\n"
			+ "<its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\""
			+ " xmlns:zzz=\"okapi-framework:xmlfilter-options\">"
			+ "<zzz:options escapeQuotes='no' escapeGT='no' escapeNbsp='no'/>"
			+ "</its:rules>";
		filter.getParameters().fromString(paramData);
		
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testLocaleFilter1 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"2.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:localeFilterRule selector=\"//para1\" localeFilterType='exclude' localeFilterList='*'/>"
			+ "<its:localeFilterRule selector=\"//para2\" localeFilterType='include' localeFilterList='*'/>"
			+ "</its:rules>"
			+ "<para1>text1</para1>"
			+ "<para2>text2</para2>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text2", tu.getSource().toString());
	}
	
	@Test
	public void testLocaleFilter2 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"2.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:localeFilterRule selector=\"//para1\" localeFilterType='exclude' localeFilterList='*'/>"
			+ "<its:localeFilterRule selector=\"//para2\" localeFilterType='exclude' localeFilterList='fr'/>"
			+ "</its:rules>"
			+ "<para1>text1</para1>"
			+ "<para2>text2</para2>"
			+ "<para3>text3</para3>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text3", tu.getSource().toString());
	}

	@Test
	public void testLocaleFilter3 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"2.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:localeFilterRule selector=\"//para1\" localeFilterType='include' localeFilterList='de-CH, de-AT'/>"
			+ "<its:localeFilterRule selector=\"//para2\" localeFilterType='include' localeFilterList='en-CA, fr-CA'/>"
			+ "</its:rules>"
			+ "<para1>text1</para1>"
			+ "<para2>text2</para2>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text2", tu.getSource().toString());
	}
	
	@Test
	public void testLocaleFilter4 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"2.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:localeFilterRule selector=\"//para1\" localeFilterType='exclude' localeFilterList='fr-CH, de-CH'/>"
			+ "<its:localeFilterRule selector=\"//para2\" localeFilterType='exclude' localeFilterList='en-CA, de'/>"
			+ "</its:rules>"
			+ "<para1>text1</para1>"
			+ "<para2>text2</para2>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text2", tu.getSource().toString());
	}
	
	@Test
	public void testLocaleFilter5 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc xmlns:its=\"http://www.w3.org/2005/11/its\"><its:rules version=\"2.0\">"
			+ "<its:localeFilterRule selector=\"/doc\" localeFilterType='exclude' localeFilterList='fr-CH, de-CH'/>"
			+ "</its:rules>"
			+ "<para1 its:localeFilterType='include' its:localeFilterList='fr-CH'>text1</para1>"
			+ "<para2>text2</para2>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text1", tu.getSource().toString());
	}
	
	@Test
	public void testComplexIdValue () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\""
			+ " xmlns:itsx=\"http://www.w3.org/2008/12/its-extensions\">"
			+ "<its:translateRule selector=\"//doc\" translate=\"no\"/>"
			+ "<its:translateRule selector=\"//src\" translate=\"yes\" itsx:idValue=\"../../name/@id\"/>"
			+ "</its:rules>"
			+ "<grp><name id=\"id1\" /><u><src>text 1</src></u></grp>"
			+ "<grp><name id=\"id1\" /><u><src xml:id=\"xid2\">text 2</src></u></grp>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("id1", tu.getName());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("xid2", tu.getName()); // xml:id overrides global rule
	}
	
	@Test
	public void testIdValueV2 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"2.0\" xmlns:its=\"http://www.w3.org/2005/11/its\""
			+ " >"
			+ "<its:translateRule selector=\"//doc\" translate=\"no\"/>"
			+ "<its:translateRule selector=\"//src\" translate=\"yes\"/>"
			+ "<its:idValueRule selector=\"//src\" idValue=\"../../name/@id\"/>"
			+ "</its:rules>"
			+ "<grp><name id=\"id1\" /><u><src>text 1</src></u></grp>"
			+ "<grp><name id=\"id1\" /><u><src xml:id=\"xid2\">text 2</src></u></grp>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("id1", tu.getName());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("xid2", tu.getName()); // xml:id overrides global rule
	}

	@Test (expected = ITSException.class)
	public void testITSVersionAttribute () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules badversionattribute=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//doc\" translate=\"yes\"/>"
			+ "</its:rules>"
			+ "<p>data</p>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		FilterTestDriver.getTextUnit(list, 1);
	}
	
	@Test
	public void testITSVersion1 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//doc\" translate=\"yes\"/>"
			+ "</its:rules>"
			+ "<p>data</p>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		assertNotNull(FilterTestDriver.getTextUnit(list, 1));
	}
	
	@Test
	public void testITSVersion2 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"2.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//doc\" translate=\"yes\"/>"
			+ "</its:rules>"
			+ "<p>data</p>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		assertNotNull(FilterTestDriver.getTextUnit(list, 1));
	}
	
	@Test
	public void testIdValue () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\""
			+ " xmlns:itsx=\"http://www.w3.org/2008/12/its-extensions\">"
			+ "<its:translateRule selector=\"//p\" translate=\"yes\" itsx:idValue=\"@name\"/>"
			+ "</its:rules>"
			+ "<p name=\"id1\">text 1</p>"
			+ "<p xml:id=\"xid2\">text 2</p>"
			+ "<p xml:id=\"xid3\" name=\"id3\">text 3</p></doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("id1", tu.getName());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("xid2", tu.getName()); // No 'name' attribute
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertNotNull(tu);
		assertEquals("xid3", tu.getName()); // xml:id overrides global rule
	}
	
	@Test
	public void testIdComplexValue () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\""
			+ " xmlns:itsx=\"http://www.w3.org/2008/12/its-extensions\">"
			+ "<its:translateRule selector=\"//text\" translate=\"yes\" itsx:idValue=\"concat(../@name, '_t')\"/>"
			+ "<its:translateRule selector=\"//desc\" translate=\"yes\" itsx:idValue=\"concat(../@name, '_d')\"/>"
			+ "</its:rules>"
			+ "<msg name=\"id1\">"
			+ "<text>Value of text</text>"
			+ "<desc>Value of desc</desc>"
			+ "</msg></doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("id1_t", tu.getName());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("id1_d", tu.getName());
	}

	@Test
	public void testLocalWithinText () { // ITS 2.0
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc xmlns:its=\"http://www.w3.org/2005/11/its\"><its:rules version=\"2.0\" >"
			+ "<its:withinTextRule selector=\"//c\" withinText=\"yes\"/>"
			+ "</its:rules>"
			+ "<p>t1<c its:withinText='no'/>t2</p>"
			+ "<p>t3<c />t4</p>"
			+ "<p>t5<c></c>t6</p>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertEquals("t1", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("t2", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 3);
		Code code = tu.getSource().getFirstContent().getCodes().get(0);
		assertEquals(TagType.PLACEHOLDER, code.getTagType());
		assertEquals("t3<1/>t4", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 4);
		assertEquals("t5<1/>t6", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	//TODO: implement it properly @Test
	public void testLocalWithinTextNested () { // ITS 2.0
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc xmlns:its=\"http://www.w3.org/2005/11/its\"><its:rules version=\"2.0\" >"
			+ "<its:withinTextRule selector=\"//n\" withinText=\"nested\"/>"
			+ "</its:rules>"
			+ "<p>t1 <n>t2</n> t3</p>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertEquals("t1 <1/> t3", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("t2", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testEmptyElements () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:withinTextRule selector=\"//c\" withinText=\"yes\"/>"
			+ "</its:rules>"
			+ "<p>t1<c/>t2</p>"
			+ "<p>t1<c />t2</p>"
			+ "<p>t1<c></c>t2</p>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		Code code = tu.getSource().getFirstContent().getCodes().get(0);
		assertEquals(TagType.PLACEHOLDER, code.getTagType());
		assertEquals("t1<1/>t2", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("t1<1/>t2", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertEquals("t1<1/>t2", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testOutputEmptyElements () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:withinTextRule selector=\"//c\" withinText=\"yes\"/>"
			+ "</its:rules>"
			+ "<p>t1<c/>t2</p>"
			+ "<p>t1<c />t2</p>"
			+ "<p>t1<c></c>t2</p>"
			+ "</doc>";
		// All empty elements are re-written as <elem/>
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:withinTextRule selector=\"//c\" withinText=\"yes\"/>"
			+ "</its:rules>"
			+ "<p>t1<c/>t2</p>"
			+ "<p>t1<c/>t2</p>"
			+ "<p>t1<c/>t2</p>"
			+ "</doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOutputAttributesAndQuotes () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@alt\" translate=\"yes\"/>"
			+ "</its:rules>"
			+ "<p alt='It&apos;s done' notrans='&apos;'>It&apos;s done</p>"
			+ "</doc>";
		// All empty elements are re-written as <elem/>
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@alt\" translate=\"yes\"/>"
			+ "</its:rules>"
			+ "<p alt=\"It's done\" notrans=\"'\">It's done</p>"
			+ "</doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertEquals("It's done", tu.getSource().toString());
		assertEquals(expected, FilterTestDriver.generateOutput(list,
			filter.getEncoderManager(), locEN));
	}
// Cannot test with internal rules, works with external only
//	@Test
//	public void testLineBreakAsCode () {
//		String snippet = "<?xml version=\"1.0\"?>\n"
//			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\""
//			+ " xmlns:okp=\"okapi-framework:xmlfilter-options\">"
//			+ "<okp:options lineBreakAsCode=\"yes\"/>"
//			+ "</its:rules>"
//			+ "<p>line 1&#10;line 2.</p>"
//			+ "</doc>";
//		ArrayList<Event> list = getEvents(snippet);
//		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
//		assertNotNull(tu);
//		assertEquals("line 1.&#10;line 2.", tu.toString());
//	}
	
//TODO: Must fix entity handing for all XML parser
//	@Test
//	public void testSimpleEntities () {
//		String snippet = "<?xml version=\"1.0\" ?>\r"
//			+ "<!DOCTYPE doc ["
//			+ "<!ENTITY aWithRingAndAcute '&#x01fb;'>\r"
//			+ "<!ENTITY text 'TEXT'>\r"
//			+ "]>\r"
//			+ "<doc>"
//			+ "<p>&aWithRingAndAcute;=e1</p>"
//			+ "<p>&text;=e2</p>"
//			+ "</doc>";
//		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r"
//			+ "<!DOCTYPE doc ["
//			+ "<!ENTITY aWithRingAndAcute '&#x01fb;'>\r"
//			+ "<!ENTITY text 'TEXT'>\r"
//			+ "]>\r"
//			+ "<doc>"
//			+ "<p>&aWithRingAndAcute;=e1</p>"
//			+ "<p>&text;=e2</p>"
//			+ "</doc>";
//		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
//		assertNotNull(tu);
//		assertEquals("<1/>=e1", fmt.setContent(tu.getSourceContent()).toString());
//		
//		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
//			filter.getEncoderManager(), locEN));
//	}
//
//	@Test
//	public void testComplexEntities () {
//		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r"
//			+ "<!DOCTYPE doc ["
//			+ "<!ENTITY entity1 '[&entity2;]'>\r"
//			+ "<!ENTITY entity2 'TEXT'>\r"
//			+ "]>\r"
//			+ "<doc>"
//			+ "<p>&entity1;=[TEXT]</p>"
//			+ "<p>&entity2;=TEXT</p>"
//			+ "</doc>";
//		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), locEN));
//	}
	
	@Test
	public void testDefaultInfo () {
		assertNotNull(filter.getParameters());
		assertNotNull(filter.getName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}

	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root+"test01.xml", null),
			"UTF-8", locEN, locEN));
	}
	
	@Test
	public void testStartDocumentFromList () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<doc>text</doc>";
		StartDocument sd = FilterTestDriver.getStartDocument(getEvents(snippet));
		assertNotNull(sd);
		assertNotNull(sd.getEncoding());
		assertNotNull(sd.getType());
		assertNotNull(sd.getMimeType());
		assertNotNull(sd.getLocale());
		assertEquals("\r", sd.getLineBreak());
	}
	
	@Test
	public void testOutputBasic_Comment () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><!--c--></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><!--c--></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOutputBasic_PI () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><?pi ?></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><?pi ?></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOutputBasic_OneChar () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc>T</doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc>T</doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOutputBasic_EmptyRoot () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc/>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc/>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOutputSimpleContent () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>test</p></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><p>test</p></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}

	@Test
	public void testOutputSimpleContent_WithEscapes () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>&amp;=amp, &lt;=lt, &quot;=quot..</p></doc>";
		// No translatable attributes -> allow not escaped apos and quote
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><p>&amp;=amp, &lt;=lt, &quot;=quot..</p></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	//TODO: Implement language handling
/*	@Test
	public void testOutputSimpleContent_WithLang () {
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc xml:lang='en'>test</doc>";
		String expect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc xml:lang='FR'>test</doc>";
		//TODO: Implement replacement of the lang value
		//assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "FR"));
	}*/
	
	@Test
	public void testOutputSupplementalChars () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<p>[&#x20000;]=U+D840,U+DC00</p>";
		String expect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<p>[\uD840\uDC00]=U+D840,U+DC00</p>";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testCDATAParsing () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p><![CDATA[&=amp, <=lt, &#xaaa;=not-a-ncr]]></p></doc>";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals(tu.getSource().getFirstContent().toText(), "&=amp, <=lt, &#xaaa;=not-a-ncr");
	}

	@Test
	public void testOutputCDATA () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p><![CDATA[&=amp, <=lt, &#xaaa;=not-a-ncr]]></p></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><p>&amp;=amp, &lt;=lt, &amp;#xaaa;=not-a-ncr</p></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}

	@Test
	public void testCREntity () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>t1\n\n   &#xD;&#13;   t2</p></doc>";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		String str = tu.getSource().toString();
		assertEquals("t1 \r\r t2", str);
	}

	@Test
	public void testCREntityOutput () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>t1\n\n   &#xD;&#13;   t2</p></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><p>t1 &#13;&#13; t2</p></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}

	@Test
	public void testCommentParsing () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>t1 <!--comment--> t2</p></doc>";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals(fmt.setContent(tu.getSource().getFirstContent()).toString(), "t1 <1/> t2");
	}

	@Test
	public void testOutputComment () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>t1 <!--comment--> t2</p></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><p>t1 <!--comment--> t2</p></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}

	@Test
	public void testPIParsing () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>t1 <?abc attr=\"value\"?> t2</p></doc>";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals(fmt.setContent(tu.getSource().getFirstContent()).toString(), "t1 <1/> t2");
	}
	
	@Test
	public void testOutputPI () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>t1 <?abc attr=\"value\"?> t2</p></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><p>t1 <?abc attr=\"value\"?> t2</p></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}

	@Test
	public void testOutputWhitespacesPreserve () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>part 1\npart 2</p>"
			+ "<p xml:space=\"preserve\">part 1\npart 2</p></doc>";
		String expect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><p>part 1 part 2</p>"
			+ "<p xml:space=\"preserve\">part 1\npart 2</p></doc>";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOutputWhitespacesDefault () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<p>part 1\npart 2\n  part3\n\t part4</p>";
		String expect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<p>part 1 part 2 part3 part4</p>";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOutputWhitespacesITS () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\" xmlns:itsx=\"http://www.w3.org/2008/12/its-extensions\">"
			+ "<its:translateRule itsx:whiteSpaces=\"preserve\" selector=\"//pre\" translate=\"yes\"/></its:rules>"
			+ "<p>[  \t]</p><pre>[  \t]</pre></doc>";
		String expect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\" xmlns:itsx=\"http://www.w3.org/2008/12/its-extensions\">"
			+ "<its:translateRule itsx:whiteSpaces=\"preserve\" selector=\"//pre\" translate=\"yes\"/></its:rules>"
			+ "<p>[ ]</p><pre>[  \t]</pre></doc>";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOutputStandaloneYes () {
		String snippet = "<?xml version=\"1.0\" standalone=\"yes\"?>\n"
			+ "<doc>text</doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
			+ "<doc>text</doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}

	@Test
	public void testSeveralUnits () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><p>text 1</p><p>text 2</p><p>text 3</p></doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text 1", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("text 2", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertNotNull(tu);
		assertEquals("text 3", tu.getSource().toString());
	}
	
	@Test
	public void testTranslatableAttributes () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/></its:rules>"
			+ "<p text=\"value 1\">text 1</p><p>text 2</p><p>text 3</p></doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("value 1", tu.getSource().toString());
	}
	
	@Test
	public void testTranslatableAttributes2 () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/></its:rules>"
			+ "<p text=\"value 1 &quot;=quot\">text 1</p><p>text 2 &quot;=quot</p><p>text 3</p></doc>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("value 1 \"=quot", tu.getSource().toString());
	}
	
	@Test
	public void testTranslatableAttributesOutput () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/></its:rules>"
			+ "<p NOTtext=\"&apos;=apos\" text=\"value 1 &apos;=apos, &quot;=quot\">text 1</p><p>text 2 &quot;=quot</p><p>text 3 &apos;=apos</p></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/></its:rules>"
			+ "<p NOTtext=\"'=apos\" text=\"value 1 '=apos, &quot;=quot\">text 1</p><p>text 2 &quot;=quot</p><p>text 3 '=apos</p></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}

	@Test
	public void testTranslatableAttributesOutputAllowUnescapedQuoteButEscape () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/></its:rules>"
			+ "<p NOTtext='value 1 &apos;=apos, &quot;=quot'>text 1</p><p>text 2 &quot;=quot</p><p>text 3 &apos;=apos</p></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/></its:rules>"
			+ "<p NOTtext=\"value 1 '=apos, &quot;=quot\">text 1</p><p>text 2 &quot;=quot</p><p>text 3 '=apos</p></doc>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testTranslatableAttributesOutputAllowUnescapedQuote () {
		String snippet = "<?xml version=\"1.0\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/>"
			+ "</its:rules>"
			+ "<p NOTtext='value 1 &apos;=apos, &quot;=quot'>text 1</p><p>text 2 &quot;=quot</p><p>text 3 &apos;=apos</p></doc>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/>"
			+ "</its:rules>"
			+ "<p NOTtext=\"value 1 '=apos, &quot;=quot\">text 1</p><p>text 2 \"=quot</p><p>text 3 '=apos</p></doc>";
		
		String paramData = "<?xml version=\"1.0\"?>\n"
			+ "<its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\""
			+ " xmlns:zzz=\"okapi-framework:xmlfilter-options\">"
			+ "<zzz:options escapeQuotes=\"no\"/></its:rules>";
		filter.getParameters().fromString(paramData);
		
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	@Test
	public void testOpenTwice () throws URISyntaxException {
		File file = new File(root+"test01.xml");
		RawDocument rawDoc = new RawDocument(file.toURI(), "windows-1252", locEN);
		filter.open(rawDoc);
		filter.close();
		filter.open(rawDoc);
		filter.close();
	}
	
	@Test
	public void extendedMatchTest () {
		assertTrue(filter.extendedMatch("*-CH", "de-CH"));
		assertTrue(filter.extendedMatch("*-CH", "fr-ch"));
		assertTrue(filter.extendedMatch("de-*-DE, fr", "de-de"));
		assertTrue(filter.extendedMatch("fr, de-*-DE", "de-Latn-de"));
		assertTrue(filter.extendedMatch("za ,  de-*-DE , po-pl  ", "de-DE-x-goethe"));
		assertTrue(filter.extendedMatch("za, de-*-DE,   po-pl", "de-DE"));
		
		assertTrue(filter.extendedMatch("za, de-*, po-pl", "de"));
		assertTrue(filter.extendedMatch("de-*, de", "de"));
		
		assertTrue(filter.extendedMatch("de-*-DE", "de-DE"));
		assertTrue(filter.extendedMatch("de-*-DE", "de-de"));
		assertTrue(filter.extendedMatch("de-*-DE", "de-Latn-DE"));
		assertTrue(filter.extendedMatch("de-*-DE", "de-Latf-DE"));
		assertTrue(filter.extendedMatch("de-*-DE", "de-DE-x-goethe"));
		assertTrue(filter.extendedMatch("de-*-DE", "de-Latn-DE-1996"));
		assertTrue(filter.extendedMatch("de-*-DE", "de-Deva-DE"));
		
		assertFalse(filter.extendedMatch("de-*-DE", "de"));
		assertFalse(filter.extendedMatch("de-*-DE", "de-x-DE"));
		assertFalse(filter.extendedMatch("de-*-DE", "de-Deva"));
	}
	

	@Test
	public void testDoubleExtraction () throws URISyntaxException {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"test01.xml", null));
		list.add(new InputDocument(root+"test02.xml", null));
		list.add(new InputDocument(root+"test03.xml", null));
		list.add(new InputDocument(root+"test04.xml", null));
		list.add(new InputDocument(root+"test05.xml", null));
		list.add(new InputDocument(root+"test06.xml", null));
		list.add(new InputDocument(root+"LocNote-1.xml", null));
		list.add(new InputDocument(root+"LocNote-2.xml", null));
		list.add(new InputDocument(root+"LocNote-3.xml", null));
		list.add(new InputDocument(root+"LocNote-4.xml", null));
		list.add(new InputDocument(root+"LocNote-5.xml", null));
		list.add(new InputDocument(root+"LocNote-6.xml", null));
		list.add(new InputDocument(root+"AndroidTest1.xml", "okf_xml@AndroidStrings.fprm"));
		list.add(new InputDocument(root+"AndroidTest2.xml", "okf_xml@AndroidStrings.fprm"));
		list.add(new InputDocument(root+"AndroidTest3.xml", "okf_xml@AndroidStrings.fprm"));
		list.add(new InputDocument(root+"JavaProperties.xml", "okf_xml@JavaProperties.fprm"));
		list.add(new InputDocument(root+"TestMultiLang.xml", null));
		list.add(new InputDocument(root+"Test01.resx", "okf_xml@RESX.fprm"));
		list.add(new InputDocument(root+"MozillaRDFTest01.rdf", "okf_xml@MozillaRDF.fprm"));
		list.add(new InputDocument(root+"XRTT-Source1.xml", null));
		list.add(new InputDocument(root+"TestCDATA1.xml", null));
		list.add(new InputDocument(root+"test07.xml", null));
		list.add(new InputDocument(root+"test08_utf8nobom.xml", null));
		list.add(new InputDocument(root+"test09.xml", null));

		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locEN));
	}

	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, locEN, LocaleId.FRENCH));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
