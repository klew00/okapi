/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.xliff;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.filters.xliff.XLIFFFilter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class XLIFFFilterTest {

	private XLIFFFilter filter;
	private XLIFFFilter segFilter;
	private XLIFFFilter noSegFilter;
	private GenericContent fmt;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	private LocaleId locES = LocaleId.fromString("es");
	private LocaleId locDE = LocaleId.fromString("de");

	@Before
	public void setUp() {
		filter = new XLIFFFilter();
		fmt = new GenericContent();
		root = TestUtil.getParentDir(this.getClass(), "/JMP-11-Test01.xlf");
		segFilter = new XLIFFFilter();
		Parameters params = (Parameters)segFilter.getParameters();
		params.setSegmentationType(Parameters.SEGMENTATIONTYPE_SEGMENTED);
		noSegFilter = new XLIFFFilter();
		params = (Parameters)noSegFilter.getParameters();
		params.setSegmentationType(Parameters.SEGMENTATIONTYPE_NOTSEGMENTED);
	}

//	@Test
//	public void testMisOrderedCodes () {
//		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//			+ "<xliff version=\"1.2\">"
//			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
//			+ "<body>"
//			+ "<trans-unit id=\"1\"><source>"
//			+ "<g id=\"1\"><g id=\"2\"></g></g><bx id=\"3\"/><x id=\"4\"/><bx id=\"5\"/><ex id=\"3\"/><bx id=\"6\"/>"
//			+ "</source></trans-unit>"
//			+ "</body>"
//			+ "</file></xliff>";
//		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
//		assertNotNull(tu);
//		assertEquals("[<1><2></2></1><b3/><4/><b5/><e3/><b6/>]", fmt.printSegmentedContent(tu.getSource(), true));
//	}

	@Test
	public void testSegmentedTarget () {
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\">"
			+ "<source>t1. t2</source>"
			+ "<target xml:lang=\"fr\"><mrk mid=\"i1\" mtype=\"seg\">t1.</mrk> <mrk mid=\"i2\" mtype=\"seg\">t2</mrk></target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		TextContainer cont = tu.getTarget(locFR);
		ISegments segments = cont.getSegments();
		assertEquals("[t1.] [t2]", fmt.printSegmentedContent(cont, true));
		assertEquals(2, segments.count());
		assertEquals("t1.", segments.get(0).text.toString());
		assertEquals("i2", segments.get(1).id);
		assertEquals("t2", segments.get(1).text.toString());
		assertEquals("i2", segments.get(1).id);
	}

	@Test
	public void testSegmentedEntry () {
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\">"
			+ "<source>t1. t2</source>"
			+ "<seg-source><mrk mid=\"1\" mtype=\"seg\">t1.</mrk> <mrk mid=\"2\" mtype=\"seg\">t2</mrk></seg-source>"
			+ "<target xml:lang=\"fr\">t1. t2</target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		TextContainer cont = tu.getSource();
		ISegments segments = cont.getSegments();
		assertNotNull(tu);
		assertEquals("[t1.] [t2]", fmt.printSegmentedContent(cont, true));
		assertEquals(2, segments.count());
		assertEquals("t1.", segments.get(0).text.toString());
		assertEquals("t2", segments.get(1).text.toString());
	}

	@Test
	public void testSegmentedEntryWithDifferences () {
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1withWarning\">"
			+ "<source>t1. x t2</source>" // Extra x in source
			+ "<seg-source><mrk mid=\"1\" mtype=\"seg\">t1.</mrk> <mrk mid=\"2\" mtype=\"seg\">t2</mrk></seg-source>"
			+ "<target xml:lang=\"fr\">t1. t2</target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals(1, tu.getSource().getSegments().count());
		assertEquals("[t1. x t2]", fmt.printSegmentedContent(tu.getSource(), true));
	}

	@Test
	public void testSegmentedEntryOutput () {
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\"><!--comment-->"
			+ "<source>t1. t2</source>"
			+ "<seg-source><mrk mid=\"1\" mtype=\"seg\">t1.</mrk> <mrk mid=\"2\" mtype=\"seg\">t2</mrk></seg-source>\r"
			+ "<target xml:lang=\"fr\"><mrk mid=\"1\" mtype=\"seg\">tt1.</mrk> <mrk mid=\"2\" mtype=\"seg\">tt2</mrk></target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\"><!--comment-->"
			+ "<source>t1. t2</source>"
			+ "<seg-source><mrk mid=\"1\" mtype=\"seg\">t1.</mrk> <mrk mid=\"2\" mtype=\"seg\">t2</mrk></seg-source>\r"
			+ "<target xml:lang=\"fr\"><mrk mid=\"1\" mtype=\"seg\">tt1.</mrk> <mrk mid=\"2\" mtype=\"seg\">tt2</mrk></target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertEquals("[t1.] [t2]", fmt.printSegmentedContent(tu.getSource(), true));
		assertEquals("[tt1.] [tt2]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
		
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			locFR, filter.createSkeletonWriter(), filter.getEncoderManager()));
	}

	@Test
	public void testSegmentedNoTargetEntryOutput () {
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "\r<body>"
			+ "<trans-unit id=\"1\" xml:space=\"preserve\">"
			+ "<source>t1.   t2</source>"
			+ "<seg-source><mrk mid=\"1\" mtype=\"seg\">t1.</mrk>   <mrk mid=\"2\" mtype=\"seg\">t2</mrk></seg-source>"
			+ "</trans-unit></body></file></xliff>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "\r<body>"
			+ "<trans-unit id=\"1\" xml:space=\"preserve\">"
			+ "<source>t1.   t2</source>"
			+ "<seg-source><mrk mid=\"1\" mtype=\"seg\">t1.</mrk>   <mrk mid=\"2\" mtype=\"seg\">t2</mrk></seg-source>"
			+ "<target xml:lang=\"fr\"><mrk mid=\"1\" mtype=\"seg\">t1.</mrk>   <mrk mid=\"2\" mtype=\"seg\">t2</mrk></target>"
			+ "\r</trans-unit></body></file></xliff>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			locFR, filter.createSkeletonWriter(), filter.getEncoderManager()));

		expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "\r<body>"
			+ "<trans-unit id=\"1\" xml:space=\"preserve\">"
			+ "<source>t1.   t2</source>"
			+ "<target xml:lang=\"fr\">t1.   t2</target>"
			+ "\r</trans-unit></body></file></xliff>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			locFR, noSegFilter.createSkeletonWriter(), noSegFilter.getEncoderManager()));
	}

	@Test
	public void testSpecialAttributeValues () {
		// Test even on invalid attributes
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\">"
			+ "<source>S1<ph ts=\"&lt;&quot;&gt;&apos;\" id=\"1\" x=\"&lt;&quot;&gt;&apos;\">code</ph></source>"
			+ "<target>T1<ph ts=\"&lt;&quot;&gt;&apos;\" id=\"1\" x=\"&lt;&quot;&gt;&apos;\">code</ph></target>"
			+ "</trans-unit></body>"
			+ "</file></xliff>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\">"
			+ "<source>S1<ph ts=\"&lt;&quot;>'\" id=\"1\" x=\"&lt;&quot;>'\">code</ph></source>"
			+ "<target>T1<ph ts=\"&lt;&quot;>'\" id=\"1\" x=\"&lt;&quot;>'\">code</ph></target>"
			+ "</trans-unit></body>"
			+ "</file></xliff>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			locFR, filter.createSkeletonWriter(), filter.getEncoderManager()));

		expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" target-language=\"fr\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\">"
			+ "<source>S1<ph ts=\"&lt;&quot;>'\" id=\"1\" x=\"&lt;&quot;>'\">code</ph></source>"
			+ "<seg-source><mrk mid=\"0\" mtype=\"seg\">S1<ph ts=\"&lt;&quot;>'\" id=\"1\" x=\"&lt;&quot;>'\">code</ph></mrk></seg-source>\r"
			+ "<target><mrk mid=\"0\" mtype=\"seg\">T1<ph ts=\"&lt;&quot;>'\" id=\"1\" x=\"&lt;&quot;>'\">code</ph></mrk></target>"
			+ "</trans-unit></body>"
			+ "</file></xliff>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			locFR, segFilter.createSkeletonWriter(), segFilter.getEncoderManager()));
	}

	@Test
	public void testAlTrans () {
		TextUnit tu = FilterTestDriver.getTextUnit(createTUWithAltTrans(), 1);
		assertNotNull(tu);
		assertEquals("t1", tu.getSource().toString());
		AltTranslationsAnnotation annot = tu.getTarget(locFR).getAnnotation(AltTranslationsAnnotation.class);
		assertNotNull(annot);
		assertEquals("alt source {t1}", annot.getFirst().getEntry().getSource().toString());
		assertEquals("alt target {t1}", annot.getFirst().getEntry().getTarget(locFR).toString());
	}

	@Test
	public void testMixedAlTrans () {
		TextUnit tu = FilterTestDriver.getTextUnit(createTUWithMixedAltTrans(), 1);
		assertNotNull(tu);
		assertEquals("t1 inter t2", tu.getSource().toString());
		assertEquals("[t1] inter [t2]", fmt.printSegmentedContent(tu.getSource(), true));
		assertEquals("[] inter []", fmt.printSegmentedContent(tu.getTarget(locFR), true));
		AltTranslationsAnnotation annot = tu.getTarget(locFR).getAnnotation(AltTranslationsAnnotation.class);
		assertNotNull(annot);
		assertEquals("", annot.getFirst().getEntry().getSource().toString()); // No source
		assertEquals("TRG for t1 inter t2", annot.getFirst().getEntry().getTarget(locFR).toString());
		ISegments segs = tu.getTarget(locFR).getSegments();
		annot = segs.get(0).getAnnotation(AltTranslationsAnnotation.class);
		assertNull(annot);
		annot = segs.get(1).getAnnotation(AltTranslationsAnnotation.class);
		assertEquals("", annot.getFirst().getEntry().getSource().toString()); // No source
		assertEquals("TRG for t2", annot.getFirst().getEntry().getTarget(locFR).toString());
		assertNotNull(annot);
	}

	@Test
	public void testOutputBPTTypeTransUnit () {
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">"
			+ "<file source-language=\"en\" datatype=\"plaintext\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"1\" resname=\"13\"><source><g id=\"1\">S1</g>, <g id=\"2\">S2</g></source>"
			+ "<target><g id=\"2\">T2</g>, <g id=\"1\">T1</g></target></trans-unit></body>"
			+ "</file></xliff>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xliff version=\"1.2\">"
			+ "<file source-language=\"en\" datatype=\"plaintext\" original=\"file.ext\" target-language=\"fr\">"
			+ "<body><trans-unit id=\"1\" resname=\"13\"><source><g id=\"1\">S1</g>, <g id=\"2\">S2</g></source>"
			+ "<target><g id=\"2\">T2</g>, <g id=\"1\">T1</g></target></trans-unit></body>"
			+ "</file></xliff>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			locEN, filter.createSkeletonWriter(), filter.getEncoderManager()));
		
	}

	@Test
	public void testApprovedTU () {
		TextUnit tu = FilterTestDriver.getTextUnit(createApprovedTU(), 1);
		assertNotNull(tu);
		assertEquals("t1", tu.getSource().getFirstContent().toString());
		assertEquals("translated t1", tu.getTarget(locFR).getFirstContent().toString());
		Property prop = tu.getTargetProperty(locFR, Property.APPROVED);
		assertNotNull(prop);
		assertEquals("yes", prop.getValue());
	}

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
			new InputDocument(root+"JMP-11-Test01.xlf", null),
			"UTF-8", locEN, locEN));
	}
	
	@Test
	public void testStartDocumentFromList () {
		StartDocument sd = FilterTestDriver.getStartDocument(createSimpleXLIFF());
		assertNotNull(sd);
		assertNotNull(sd.getEncoding());
		assertNotNull(sd.getType());
		assertNotNull(sd.getMimeType());
		assertNotNull(sd.getLocale());
		assertEquals("\r", sd.getLineBreak());
	}
	
	@Test
	public void testStartSubDocumentFromList () {
		StartDocument sd = FilterTestDriver.getStartDocument(createSimpleXLIFF());
		StartSubDocument subd = FilterTestDriver.getStartSubDocument(createSimpleXLIFF(), 1);
		assertNotNull(subd);
		assertNotNull(subd.getId());
		assertEquals(sd.getId(), subd.getParentId());
		assertEquals("file.ext", subd.getName());
		Property prop = subd.getProperty("build-num");
		assertNotNull(prop);
		assertEquals("13", prop.getValue());
	}
	
	@Test
	public void testSimpleTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(createSimpleXLIFF(), 1);
		assertNotNull(tu);
		assertEquals("Hello World!", tu.getSource().toString());
		assertEquals("13", tu.getName());
		Property prop = tu.getProperty("extradata");
		assertNotNull(prop);
		assertEquals("xd", prop.getValue());
	}

	@Test
	public void testWithNamespaces () {
		TextUnit tu = FilterTestDriver.getTextUnit(createInputWithNamespace(), 1);
		assertNotNull(tu);
		assertEquals("t1", tu.getSource().toString());
		assertEquals("translated t1", tu.getTarget(locFR).toString());
	}
	
	@Test
	public void testBilingualTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(createBilingualXLIFF(), 1);
		assertNotNull(tu);
		assertEquals("S1, S2", tu.getSource().toString());
		fmt.setContent(tu.getSource().getFirstContent());
		assertEquals("<1>S1</1>, <2>S2</2>", fmt.toString());
		assertTrue(tu.hasTarget(locFR));
		assertEquals("T2, T1", tu.getTarget(locFR).toString());
		fmt.setContent(tu.getTarget(locFR).getFirstContent());
		assertEquals("<2>T2</2>, <1>T1</1>", fmt.toString());
	}

	@Test
	public void testBilingualInlines () {
		TextUnit tu = FilterTestDriver.getTextUnit(createBilingualXLIFF(), 1);
		assertNotNull(tu);
		assertTrue(tu.hasTarget(locFR));
		TextFragment src = tu.getSource().getFirstContent();
		TextFragment trg = tu.getTarget(locFR).getFirstContent();
		assertEquals(4, src.getCodes().size());
		assertEquals(src.getCodes().size(), trg.getCodes().size());
		FilterTestDriver.checkCodeData(src, trg);
	}
	
	@Test
	public void testBPTTypeTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(createBPTTypeXLIFF(), 1);
		assertNotNull(tu);
		fmt.setContent(tu.getSource().getFirstContent());
		assertEquals("<1>S1</1>, <2>S2</2>", fmt.toString());
		assertTrue(tu.hasTarget(locFR));
		fmt.setContent(tu.getTarget(locFR).getFirstContent());
		assertEquals("<2>T2</2>, <1>T1</1>", fmt.toString());
	}

	@Test
	public void testBPTAndSUBTypeTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(createBPTAndSUBTypeXLIFF(), 1);
		assertNotNull(tu);
		fmt.setContent(tu.getSource().getFirstContent());
		assertEquals("<1>S1</1>, <2>S2</2>", fmt.toString());
	}

	@Test
	public void testBPTWithSUB () {
		TextUnit tu = FilterTestDriver.getTextUnit(createBPTAndSUBTypeXLIFF(), 1);
		assertNotNull(tu);
		Code code = tu.getSource().getFirstContent().getCode(0);
		assertEquals(code.getData(), "a<sub>text</sub>");
		assertEquals(code.getOuterData(), "<bpt id=\"1\">a<sub>text</sub></bpt>");
	}

	@Test
	public void testPreserveSpaces () {
		TextUnit tu = FilterTestDriver.getTextUnit(createTUWithSpaces(), 1);
		assertNotNull(tu);
		fmt.setContent(tu.getSource().getFirstContent());
		assertTrue(tu.preserveWhitespaces());
		assertEquals("t1  t2 t3\t\t<1/>  t4", fmt.toString());
	}

	@Test
	public void testUnwrapSpaces () {
		TextUnit tu = FilterTestDriver.getTextUnit(createTUWithSpaces(), 2);
		assertNotNull(tu);
		fmt.setContent(tu.getSource().getFirstContent());
		assertFalse(tu.preserveWhitespaces());
		assertEquals("t1 t2 t3 <1/> t4", fmt.toString());
	}

	@Test
	public void testPreserveSpacesInSegmentedTU () {
		TextUnit tu = FilterTestDriver.getTextUnit(createSegmentedTUWithSpaces(), 1);
		assertNotNull(tu);
		assertEquals("[t1  t2]  [t3  t4]", fmt.printSegmentedContent(tu.getSource(), true));
		//TODO: XLIFF filter needs to get segmented targets too
		assertEquals("[tt1  tt2  tt3  tt4]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
	}

	@Test
	public void testUnwrapSpacesInSegmentedTU () {
		TextUnit tu = FilterTestDriver.getTextUnit(createSegmentedTUWithSpaces(), 2);
		assertNotNull(tu);
		assertEquals("[t1 t2] [t3 t4]", fmt.printSegmentedContent(tu.getSource(), true));
		//TODO: XLIFF filter needs to get segmented targets too
		assertEquals("[tt1 tt2 tt3 tt4]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
	}

	@Test
	public void testComplexSUB () {
		TextUnit tu = FilterTestDriver.getTextUnit(createComplexSUBTypeXLIFF(), 1);
		assertNotNull(tu);
		Code code = tu.getSource().getFirstContent().getCode(0);
		assertEquals(code.getData(), "startCode<sub>[nested<ph id=\"2\">ph-in-sub</ph>still in sub]</sub>endCode");
		assertEquals(code.getOuterData(), "<ph id=\"1\">startCode<sub>[nested<ph id=\"2\">ph-in-sub</ph>still in sub]</sub>endCode</ph>");
	}

	@Test
	public void testComplexSUBInTarget () {
		TextUnit tu = FilterTestDriver.getTextUnit(createComplexSUBTypeXLIFF(), 1);
		assertNotNull(tu);
		tu.createTarget(locFR, true, IResource.COPY_ALL);
		Code code = tu.getTarget(locFR).getFirstContent().getCode(0);
		assertEquals(code.getData(), "startCode<sub>[nested<ph id=\"2\">ph-in-sub</ph>still in sub]</sub>endCode");
		assertEquals(code.getOuterData(), "<ph id=\"1\">startCode<sub>[nested<ph id=\"2\">ph-in-sub</ph>still in sub]</sub>endCode</ph>");
	}

	@Test
	public void testSegmentationWithEmptyTarget () {
		TextUnit tu = FilterTestDriver.getTextUnit(createSegmentedTUEmptyTarget(), 1);
		assertNotNull(tu);
		assertEquals("<1/>[t1]", fmt.printSegmentedContent(tu.getSource(), true));
		TextContainer trgCont = tu.getTarget(locFR);
		assertNotNull(trgCont);
		assertEquals("<1/>[]", fmt.printSegmentedContent(trgCont, true));
	}

	@Test
	public void testOutputSegmentationWithEmptyTarget () {
		String snippet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\" target-language=\"fr\">"
			+ "<body>"
			+ "<trans-unit id=\"1\" xml:space=\"preserve\"><source><ph id=\"1\">code</ph>t1</source>"
			+ "<seg-source><ph id=\"1\">code</ph><mrk mid=\"s1\" mtype=\"seg\">t1</mrk></seg-source>\r"
			+ "<target xml:lang=\"fr\"><ph id=\"1\">code</ph><mrk mid=\"s1\" mtype=\"seg\"></mrk></target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\" target-language=\"fr\">"
			+ "<body>"
			+ "<trans-unit id=\"1\" xml:space=\"preserve\"><source><ph id=\"1\">code</ph>t1</source>"
			+ "<seg-source><ph id=\"1\">code</ph><mrk mid=\"s1\" mtype=\"seg\">t1</mrk></seg-source>\r"
			+ "<target xml:lang=\"fr\"><ph id=\"1\">code</ph><mrk mid=\"s1\" mtype=\"seg\"></mrk></target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			locEN, filter.createSkeletonWriter(), filter.getEncoderManager()));
	}

	@Test
	public void testNotes () {
		TextUnit tu = FilterTestDriver.getTextUnit(createDecoratedXLIFF(), 1);
		assertNotNull(tu);
		Property prop = tu.getProperty(Property.NOTE);
		assertNotNull(prop);
		assertEquals("note 1\n---\nnote 2", prop.getValue());
		prop = tu.getSourceProperty(Property.NOTE);
		assertNotNull(prop);
		assertEquals("note src 1\n---\nnote src 2", prop.getValue());
		prop = tu.getTargetProperty(locFR, Property.NOTE);
		assertNotNull(prop);
		assertEquals("note trg", prop.getValue());
	}

	@Test
	public void testDoubleExtractionFR () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"JMP-11-Test01.xlf", null));
		list.add(new InputDocument(root+"Manual-12-AltTrans.xlf", null));
		list.add(new InputDocument(root+"PAS-10-Test01.xlf", null));
		list.add(new InputDocument(root+"RB-11-Test01.xlf", null));
		list.add(new InputDocument(root+"RB-12-Test02.xlf", null));
		list.add(new InputDocument(root+"SF-12-Test03.xlf", null));
		list.add(new InputDocument(root+"NSTest01.xlf", null));
		list.add(new InputDocument(root+"BinUnitTest01.xlf", null));
		list.add(new InputDocument(root+"MQ-12-Test01.xlf", null));
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locFR));
	}

	@Test
	public void testDoubelextractionDE () {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Typo3Draft.xlf", null));
		list.add(new InputDocument(root+"Xslt-Test01.xlf", null));
		list.add(new InputDocument(root+"TS09-12-Test01.sdlxliff", null));
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locDE));
	}

	@Test
	public void testDoubelextractionES () {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"SF-12-Test01.xlf", null));
		list.add(new InputDocument(root+"SF-12-Test02.xlf", null));
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locES));
	}

	private ArrayList<Event> createSimpleXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"plaintext\" original=\"file.ext\" build-num=\"13\">"
			+ "<body><trans-unit id=\"1\" resname=\"13\" extradata=\"xd\"><source>Hello World!</source></trans-unit></body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createBilingualXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"plaintext\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\"><source><g id='1'>S1</g>, <g id='2'>S2</g></source>"
			+ "<target><g id='2'>T2</g>, <g id='1'>T1</g></target></trans-unit></body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createBPTTypeXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\"><source><bpt id='1'>a</bpt>S1<ept id='1'>/a</ept>, <bpt id='2'>b</bpt>S2<ept id='2'>/b</ept></source>"
			+ "<target><bpt id='2'>b</bpt>T2<ept id='2'>/b</ept>, <bpt id='1'>a</bpt>T1<ept id='1'>/a</ept></target></trans-unit></body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}

	private ArrayList<Event> createDecoratedXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\">"
			+ "<source>text src</source>"
			+ "<target>text trg</target>"
			+ "<note>note 1</note>"
			+ "<note annotates='general'>note 2</note>"
			+ "<note annotates='source'>note src 1</note>"
			+ "<note annotates='target'>note trg</note>"
			+ "<note annotates='source'>note src 2</note>"
			+ "</trans-unit></body></file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createBPTAndSUBTypeXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\"><source><bpt id=\"1\">a<sub>text</sub></bpt>S1<ept id=\"1\">/a</ept>, <bpt id=\"2\">b</bpt>S2<ept id=\"2\">/b</ept></source>"
			+ "</trans-unit></body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createComplexSUBTypeXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\"><source>t1 <ph id=\"1\">startCode<sub>[nested<ph id=\"2\">ph-in-sub</ph>still in sub]</sub>endCode</ph> t2</source>"
			+ "</trans-unit></body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createTUWithSpaces () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\" xml:space=\"preserve\"><source>t1  t2 t3\t\t<ph id='1'>X</ph>  t4</source></trans-unit>"
			+ "<trans-unit id=\"2\"><source>t1  t2 t3\t\t<ph id='1'>X</ph>  t4</source></trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createSegmentedTUWithSpaces () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\" xml:space='preserve'><source>t1  t2  t3  t4</source>"
			+ "<seg-source><mrk mid='1' mtype='seg'>t1  t2</mrk>  <mrk mid='2' mtype='seg'>t3  t4</mrk></seg-source>"
			+ "<target xml:lang='fr'>tt1  tt2  tt3  tt4</target>"
			+ "</trans-unit>"
			+ "<trans-unit id=\"2\"><source>t1  t2  t3  t4</source>"
			+ "<seg-source><mrk mid='1' mtype='seg'>t1  t2</mrk>  <mrk mid='2' mtype='seg'>t3  t4</mrk></seg-source>"
			+ "<target xml:lang='fr'>tt1  tt2  tt3  tt4</target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}

	private ArrayList<Event> createSegmentedTUEmptyTarget () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\" xml:space='preserve'><source><ph id='1'>code</ph>t1</source>"
			+ "<seg-source><ph id='1'>code</ph><mrk mid='s1' mtype='seg'>t1</mrk></seg-source>"
			+ "<target xml:lang='fr'><ph id='1'>code</ph><mrk mid='s1' mtype='seg'></mrk></target>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}

	private ArrayList<Event> createApprovedTU () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\" approved=\"yes\"><source>t1</source>"
			+ "<target>translated t1</target></trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createInputWithNamespace () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<x:xliff version=\"1.2\" xmlns:x=\"'urn:oasis:names:tc:xliff:document:1.2'\">\r"
			+ "<x:file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<x:body>"
			+ "<x:trans-unit id=\"1\" approved=\"yes\"><x:source>t1</x:source>"
			+ "<x:target>translated t1</x:target></x:trans-unit>"
			+ "</x:body>"
			+ "</x:file></x:xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createTUWithAltTrans () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\"><source>t1</source>"
			+ "<target>translated t1</target>"
			+ "<alt-trans>"
			+ "<source>alt source <bpt id=\"1\">{</bpt>t1<ept id=\"1\">}</ept></source>"
			+ "<target>alt target <mrk mtype=\"term\"><bpt id=\"1\">{</bpt>t1<ept id=\"1\">}</ept></mrk></target>"
			+ "</alt-trans>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createTUWithMixedAltTrans () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body>"
			+ "<trans-unit id=\"1\">"
			+ "<source>t1 inter t2</source>"
			+ "<seg-source><mrk mid=\"s1\" mtype=\"seg\">t1</mrk> inter <mrk mid=\"s2\" mtype=\"seg\">t2</mrk></seg-source>"
			+ "<alt-trans>"
			+ "<target>TRG for t1 inter t2</target>"
			+ "</alt-trans>"
			+ "<alt-trans mid=\"s2\">"
			+ "<target>TRG for t2</target>"
			+ "</alt-trans>"
			+ "</trans-unit>"
			+ "</body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, locEN, locFR));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}
	
}
