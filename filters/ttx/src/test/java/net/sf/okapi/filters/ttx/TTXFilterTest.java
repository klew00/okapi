/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.ttx;

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.filters.ttx.TTXFilter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TTXFilterTest {

	private TTXFilter filter;
	private GenericContent fmt;
	private String root;
	private LocaleId locENUS = LocaleId.fromString("en-us");
	private LocaleId locESEM = LocaleId.fromString("es-em");
	private LocaleId locFRFR = LocaleId.fromString("fr-fr");
	private LocaleId locKOKR = LocaleId.fromString("ko-kr");
	private ISkeletonWriter skelWriter;
	
	private static final String STARTFILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<TRADOStag Version=\"2.0\"><FrontMatter>\n"
		+ "<ToolSettings CreationDate=\"20070508T094743Z\" CreationTool=\"TRADOS TagEditor\" CreationToolVersion=\"7.0.0.615\"></ToolSettings>\n"
		+ "<UserSettings DataType=\"STF\" O-Encoding=\"UTF-8\" SettingsName=\"\" SettingsPath=\"\" SourceLanguage=\"EN-US\" TargetLanguage=\"ES-EM\" SourceDocumentPath=\"abc.rtf\" SettingsRelativePath=\"\" PlugInInfo=\"\"></UserSettings>\n"
		+ "</FrontMatter><Body><Raw>\n";

	private static final String STARTFILENOLB = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<TRADOStag Version=\"2.0\"><FrontMatter>\n"
		+ "<ToolSettings CreationDate=\"20070508T094743Z\" CreationTool=\"TRADOS TagEditor\" CreationToolVersion=\"7.0.0.615\"></ToolSettings>\n"
		+ "<UserSettings DataType=\"STF\" O-Encoding=\"UTF-8\" SettingsName=\"\" SettingsPath=\"\" SourceLanguage=\"EN-US\" TargetLanguage=\"ES-EM\" SourceDocumentPath=\"abc.rtf\" SettingsRelativePath=\"\" PlugInInfo=\"\"></UserSettings>\n"
		+ "</FrontMatter><Body><Raw>";

	private static final String STARTFILEKO = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<TRADOStag Version=\"2.0\"><FrontMatter>\n"
		+ "<ToolSettings CreationDate=\"20070508T094743Z\" CreationTool=\"TRADOS TagEditor\" CreationToolVersion=\"7.0.0.615\"></ToolSettings>\n"
		+ "<UserSettings DataType=\"STF\" O-Encoding=\"UTF-8\" SettingsName=\"\" SettingsPath=\"\" SourceLanguage=\"EN-US\" TargetLanguage=\"KO-KR\" TargetDefaultFont=\"\ubd7e\" SourceDocumentPath=\"abc.rtf\" SettingsRelativePath=\"\" PlugInInfo=\"\"></UserSettings>\n"
		+ "</FrontMatter><Body><Raw>\n";

	@Before
	public void setUp() {
		filter = new TTXFilter();
		fmt = new GenericContent();
		root = TestUtil.getParentDir(this.getClass(), "/Test01.html.ttx");
	}

	@Test
	public void testBasicNoExtractableData () {
		String snippet = STARTFILE
			+ " <ut Style=\"external\">some &amp; code</ut>\n\n  <!-- comments-->"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNull(tu);
	}

	@Test
	public void testOutputNoExtractableData () {
		String snippet = STARTFILE
			+ " <ut Style=\"external\">some &amp; code</ut>\n\n  <!-- comments-->"
			+ "</Raw></Body></TRADOStag>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
			filter.createSkeletonWriter()));
	}

	@Test
	public void testBasicNoTU () {
		String snippet = STARTFILE
			+ "text"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals("\ntext", cont.toString());
		assertFalse(tu.hasTarget(locESEM));
	}

	@Test
	public void testOutputBasicNoTU () {
		String snippet = STARTFILENOLB
			+ "text"
			+ "</Raw></Body></TRADOStag>";
		String expected = STARTFILENOLB
			+ "<Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">text</Tuv>"
			+ "<Tuv Lang=\"ES-EM\">text</Tuv></Tu>"
			+ "</Raw></Body></TRADOStag>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
			filter.createSkeletonWriter()));
	}

	@Test
	public void testBasicNoTUWithDF () {
		String snippet = STARTFILENOLB
			+ "<df Size=\"16\">text</df>"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals("text", cont.toString());
		assertFalse(tu.hasTarget(locESEM));
	}

//	@Test
//	public void testOutputBasicNoTUWithDF () {
//		String snippet = STARTFILENOLB
//			+ "<df Size=\"16\">text</df>"
//			+ "</Raw></Body></TRADOStag>";
//		String expected = STARTFILENOLB
//			+ "<df Size=\"16\"><Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">text</Tuv>"
//			+ "<Tuv Lang=\"ES-EM\">text</Tuv></Tu></df>"
//			+ "</Raw></Body></TRADOStag>";
//		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
//			filter.createSkeletonWriter()));
//	}

	@Test
	public void testWithPINoTU () {
		String snippet = STARTFILE
			+ "<ut Class=\"procinstr\">pi</ut>text"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals("text", cont.toString());
		assertFalse(tu.hasTarget(locESEM));
	}

	@Test
	public void testNoTUEndsWithUT () {
		String snippet = STARTFILE
			+ "text<ut Class=\"procinstr\">pi</ut>"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals("\ntext", cont.toString());
		assertFalse(tu.hasTarget(locESEM));
	}

	@Test
	public void testNoTUContentWithUT () {
		String snippet = STARTFILENOLB
			+ "before <ut Type=\"start\">[</ut>in<ut Type=\"end\">]</ut> after"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals("before [in] after", cont.toString());
		assertFalse(tu.hasTarget(locESEM));
	}

	@Test
	public void testOutputNoTUContentWithUT () {
		String snippet = STARTFILENOLB
			+ "before <ut Type=\"start\">[</ut>in<ut Type=\"end\">]</ut> after"
			+ "</Raw></Body></TRADOStag>";
		String expected = STARTFILENOLB
			+ "<Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">before <ut Type=\"start\">[</ut>in<ut Type=\"end\">]</ut> after</Tuv>"
			+ "<Tuv Lang=\"ES-EM\">before <ut Type=\"start\">[</ut>in<ut Type=\"end\">]</ut> after</Tuv></Tu>"
			+ "</Raw></Body></TRADOStag>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
			filter.createSkeletonWriter()));
	}

//	@Test
//	public void testForExternalDF () {
//		String snippet = STARTFILENOLB
//			+ "<df Font=\"Arial\"><ut Style=\"external\">code</ut>text<ut Style=\"external\">code</ut></df>"
//			+ "</Raw></Body></TRADOStag>";
//		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
//		assertNotNull(tu);
//		TextContainer cont = tu.getSource();
//		assertEquals("text", cont.toString());
//	}

//	@Test
//	public void testOutputForExternalDF () {
//		String snippet = STARTFILENOLB
//			+ "<df Font=\"Arial\"><ut Style=\"external\">code</ut>text<ut Style=\"external\">code</ut></df>"
//			+ "</Raw></Body></TRADOStag>";
//		String expected = STARTFILENOLB
//			+ "<df Font=\"Arial\"><ut Style=\"external\">code</ut>"
//			+ "<Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">text</Tuv>"
//			+ "<Tuv Lang=\"ES-EM\">text</Tuv></Tu>"
//			+ "<ut Style=\"external\">code</ut></df>"
//			+ "</Raw></Body></TRADOStag>";
//		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
//				filter.createSkeletonWriter()));
//	}

	@Test
	public void testForTwoTUs () {
		String snippet = STARTFILENOLB
			+ "text1<ut Style=\"external\">code</ut>text2"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals("text1", cont.toString());
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 2);
		assertNotNull(tu);
		cont = tu.getSource();
		assertEquals("text2", cont.toString());
	}

	@Test
	public void testOutputForTwoTUs () {
		String snippet = STARTFILENOLB
			+ "text1<ut Style=\"external\">code</ut>text2"
			+ "</Raw></Body></TRADOStag>";
		String expected = STARTFILENOLB
			+ "<Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">text1</Tuv><Tuv Lang=\"ES-EM\">text1</Tuv></Tu>"
			+ "<ut Style=\"external\">code</ut>"
			+ "<Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">text2</Tuv><Tuv Lang=\"ES-EM\">text2</Tuv></Tu>"
			+ "</Raw></Body></TRADOStag>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
			filter.createSkeletonWriter()));
	}

	@Test
	public void testOutputWithPINoTU () {
		String snippet = STARTFILENOLB
			+ "<ut Class=\"procinstr\">pi</ut>text"
			+ "</Raw></Body></TRADOStag>";
		String expected = STARTFILENOLB
			+ "<ut Class=\"procinstr\">pi</ut><Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">text</Tuv>"
			+ "<Tuv Lang=\"ES-EM\">text</Tuv></Tu>"
			+ "</Raw></Body></TRADOStag>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
			filter.createSkeletonWriter()));
	}

	@Test
	public void testBasicNoUT () {
		String snippet = STARTFILE
			+ "<Tu>"
			+ "<Tuv Lang=\"EN-US\">text en</Tuv>"
			+ "<Tuv Lang=\"ES-EM\">text es</Tuv>"
			+ "</Tu>"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals(1, cont.getSegmentCount());
		cont.mergeAllSegments();
		assertEquals("text en", cont.toString());
		cont = tu.getTarget(locESEM);
		assertEquals(1, cont.getSegmentCount());
		cont.mergeAllSegments();
		assertEquals("text es", cont.toString());
	}

	@Test
	public void testBasicTwoUT () {
		String snippet = STARTFILE
			+ "<Tu><Tuv Lang=\"EN-US\">text1 en</Tuv><Tuv Lang=\"ES-EM\">text1 es</Tuv></Tu>"
			+ "  <Tu><Tuv Lang=\"EN-US\">text2 en</Tuv><Tuv Lang=\"ES-EM\">text2 es</Tuv></Tu>"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals(2, cont.getSegmentCount());
		assertEquals("text1 en", cont.getSegments().get(0).text.toString());
		assertEquals("text2 en", cont.getSegments().get(1).text.toString());
		assertEquals("0  1", cont.toString());
		cont = tu.getTarget(locESEM);
		assertEquals(2, cont.getSegmentCount());
		assertEquals("text1 es", cont.getSegments().get(0).text.toString());
		assertEquals("text2 es", cont.getSegments().get(1).text.toString());
//TODO		assertEquals("0  1", cont.toString());
	}

	@Test
	public void testBasicWithUT () {
		String snippet = STARTFILE
			+ "<Tu>"
			+ "<Tuv Lang=\"EN-US\">text <ut DisplayText=\"br\">&lt;br/&gt;</ut>en <ut Type=\"start\">&lt;b></ut>bold<ut Type=\"end\">&lt;/b></ut>.</Tuv>"
			+ "<Tuv Lang=\"ES-EM\">TEXT <ut DisplayText=\"br\">&lt;br/&gt;</ut>ES <ut Type=\"start\">&lt;b></ut>BOLD<ut Type=\"end\">&lt;/b></ut>.</Tuv>"
			+ "</Tu>"
			+ "</Raw></Body></TRADOStag>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locESEM), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getSource();
		assertEquals(1, cont.getSegmentCount());
		assertEquals("text <br/>en <b>bold</b>.", cont.getSegments().get(0).text.toString());
		assertEquals("text <1/>en <2>bold</2>.", fmt.setContent(cont.getSegments().get(0).text).toString());
		cont = tu.getTarget(locESEM);
		assertEquals(1, cont.getSegmentCount());
		assertEquals("TEXT <br/>ES <b>BOLD</b>.", cont.getSegments().get(0).text.toString());
		assertEquals("TEXT <1/>ES <2>BOLD</2>.", fmt.setContent(cont.getSegments().get(0).text).toString());
	}

	@Test
	public void testOutputSimple () {
		String snippet = STARTFILE
			+ "<Tu PercentageMatch=\"0\">"
			+ "<Tuv Lang=\"EN-US\">text en</Tuv>"
			+ "<Tuv Lang=\"ES-EM\">text es</Tuv>"
			+ "</Tu>"
			+ "</Raw></Body></TRADOStag>";
		String expected = STARTFILE
			+ "<Tu PercentageMatch=\"0\">"
			+ "<Tuv Lang=\"EN-US\">text en</Tuv>"
			+ "<Tuv Lang=\"ES-EM\">text es</Tuv>"
			+ "</Tu>"
			+ "</Raw></Body></TRADOStag>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
			filter.createSkeletonWriter()));
	}

	@Test
	public void testOutputTwoTU () {
		String snippet = STARTFILE
			+ "<Tu PercentageMatch=\"0\">"
			+ "<Tuv Lang=\"EN-US\">text1 en</Tuv><Tuv Lang=\"ES-EM\">text1 es</Tuv></Tu>\n"
			+ "  <ut Style=\"external\">some code</ut>  "
			+ "<Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">text2 en</Tuv><Tuv Lang=\"ES-EM\">text2 es</Tuv></Tu>\n"
			+ "</Raw></Body></TRADOStag>";
		String expected = STARTFILE
			+ "<Tu PercentageMatch=\"0\">"
			+ "<Tuv Lang=\"EN-US\">text1 en</Tuv><Tuv Lang=\"ES-EM\">text1 es</Tuv></Tu>\n"
			+ "  <ut Style=\"external\">some code</ut>  "
			+ "<Tu PercentageMatch=\"0\"><Tuv Lang=\"EN-US\">text2 en</Tuv><Tuv Lang=\"ES-EM\">text2 es</Tuv></Tu>\n"
			+ "</Raw></Body></TRADOStag>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
			filter.createSkeletonWriter()));
	}
	
//	@Test
//	public void testOutputWithOriginalWithoutTraget () {
//		String snippet = STARTFILE
//			+ "<Tu PercentageMatch=\"0\">"
//			+ "<Tuv Lang=\"EN-US\">text1 en</Tuv></Tu>\n"
//			+ "  <ut Style=\"external\">some code</ut>  "
//			+ "<Tu><Tuv Lang=\"EN-US\">text2 en</Tuv></Tu>\n"
//			+ "</Raw></Body></TRADOStag>";
//		String expected = STARTFILE
//			+ "<Tu PercentageMatch=\"0\">"
//			+ "<Tuv Lang=\"EN-US\">text1 en</Tuv><Tuv Lang=\"ES-EM\">text1 en</Tuv></Tu>\n"
//			+ "  <ut Style=\"external\">some code</ut>  "
//			+ "<Tu><Tuv Lang=\"EN-US\">text2 en</Tuv><Tuv Lang=\"ES-EM\">text2 en</Tuv></Tu>\n"
//			+ "</Raw></Body></TRADOStag>";
//		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locESEM), locESEM,
//			filter.createSkeletonWriter()));
//	}

//	@Test
//	public void testOutputWithOriginalWithoutTragetKO () {
//		String snippet = STARTFILEKO
//			+ "<Tu><Tuv Lang=\"EN-US\">text1 en</Tuv></Tu>\n"
//			+ "  <ut Style=\"external\">some code</ut>  "
//			+ "<Tu><Tuv Lang=\"EN-US\">text2 en</Tuv></Tu>\n"
//			+ "</Raw></Body></TRADOStag>";
//		String expected = STARTFILEKO
//			+ "<Tu><Tuv Lang=\"EN-US\">text1 en</Tuv><Tuv Lang=\"KO-KR\"><df Font=\"\ubd7e\">text1 en</df></Tuv></Tu>\n"
//			+ "  <ut Style=\"external\">some code</ut>  "
//			+ "<Tu><Tuv Lang=\"EN-US\">text2 en</Tuv><Tuv Lang=\"KO-KR\"><df Font=\"\ubd7e\">text2 en</df></Tuv></Tu>\n"
//			+ "</Raw></Body></TRADOStag>";
//		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locKOKR), locKOKR,
//			filter.createSkeletonWriter()));
//	}

//	@Test
//	public void testDoubleExtraction () {
//		// Read all files in the data directory
//		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
//		list.add(new InputDocument(root+"Test01.html.ttx", null));
//		list.add(new InputDocument(root+"Test02_noseg.html.ttx", null));
//		list.add(new InputDocument(root+"Test02_allseg.html.ttx", null));
//		RoundTripComparison rtc = new RoundTripComparison();
//		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locENUS, locFRFR));
//	}

	private ArrayList<Event> getEvents(String snippet, LocaleId trgLocId) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, locENUS, trgLocId));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}