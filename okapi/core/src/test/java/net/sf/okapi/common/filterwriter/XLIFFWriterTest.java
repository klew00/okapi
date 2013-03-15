/*===========================================================================
  Copyright (C) 2010-2013 by the Okapi Framework contributors
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

package net.sf.okapi.common.filterwriter;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.InlineAnnotation;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;

public class XLIFFWriterTest {
	
	private XLIFFWriter writer;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	
	@Before
	public void setUp() {
		writer = new XLIFFWriter();
		root = TestUtil.getParentDir(this.getClass(), "/");
	}

	@Test
	public void testMinimal ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, null, null, "original.ext", null);
		writer.writeStartFile(null, null, null);
		writer.writeEndFile();
		writer.close();
		
		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"unknown\" source-language=\"en\" datatype=\"x-undefined\">\n"
			+ "<body>\n</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testVeryMinimal ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, null, null, "original.ext", null);
		writer.close();
		
		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"original.ext\" source-language=\"en\" datatype=\"x-undefined\">\n"
			+ "<body>\n</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testWithExtra ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, null, null, "original.ext", null);
		writer.writeStartFile(null, null, null, "<phase-group phase-name=\"a\" process-name=\"b\"/>");
		writer.writeEndFile();
		writer.close();
		
		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"unknown\" source-language=\"en\" datatype=\"x-undefined\">\n"
			+ "<header><phase-group phase-name=\"a\" process-name=\"b\"/></header>\n"
			+ "<body>\n</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testBasicSourceOnly ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, null, null, "original.ext", null);
		ITextUnit tu = new TextUnit("tu1", "src1 with <>");
		writer.writeTextUnit(tu);
		writer.close();

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"original.ext\" source-language=\"en\" datatype=\"x-undefined\">\n"
			+ "<body>\n"
			+ "<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\">src1 with &lt;></source>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testAnnotations ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, locFR, null, "original.ext", null);
		writer.writeStartFile(null, null, null);
		ITextUnit tu = new TextUnit("tu1");
		TextFragment tf = tu.getSource().getFirstSegment().getContent();
		tf.append("t1 t2");
		GenericAnnotations anns1 = new GenericAnnotations();
		anns1.setData("lqi1");
		GenericAnnotation ann1 = anns1.add(GenericAnnotationType.LQI);
		ann1.setString(GenericAnnotationType.LQI_COMMENT, "rem1");
		ann1.setBoolean(GenericAnnotationType.LQI_ENABLED, false);
		ann1.setString(GenericAnnotationType.LQI_PROFILEREF, "uri");
		GenericAnnotation ann2 = anns1.add(GenericAnnotationType.LQI);
		ann2.setString(GenericAnnotationType.LQI_COMMENT, "rem2");
		ann2.setBoolean(GenericAnnotationType.LQI_ENABLED, true);
		ann2.setDouble(GenericAnnotationType.LQI_SEVERITY, 12.34);
		ann2.setString(GenericAnnotationType.LQI_TYPE, "grammar");
		tf.annotate(0, 2, GenericAnnotationType.GENERIC, anns1);
		tf = tu.createTarget(locFR, false, IResource.COPY_ALL).getFirstContent();
		GenericAnnotation ann3 = ann2.clone();
		ann3.setString(GenericAnnotationType.LQI_COMMENT, "rem3");
		ann3.setDouble(GenericAnnotationType.LQI_SEVERITY, 99.0);
		GenericAnnotations anns3 = new GenericAnnotations();
		anns3.setData("lqi2");
		anns3.add(ann3);
		anns3.add(new GenericAnnotation(GenericAnnotationType.LQI,
			GenericAnnotationType.LQI_COMMENT, "rem4"));
		tf.annotate(3+4, 5+4, GenericAnnotationType.GENERIC, anns3); // +4 is for the markers of the previous annotation
		writer.writeTextUnit(tu);
		writer.writeEndFile();
		writer.close();		

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"unknown\" source-language=\"en\" target-language=\"fr\" datatype=\"x-undefined\">\n"
			+ "<body>\n<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\"><mrk its:locQualityIssuesRef=\"#VARID\" mtype=\"x-its\">t1</mrk> t2</source>\n"
			+ "<target xml:lang=\"fr\"><mrk its:locQualityIssuesRef=\"#VARID\" mtype=\"x-its\">t1</mrk> "
			+ "<mrk its:locQualityIssuesRef=\"#VARID\" mtype=\"x-its\">t2</mrk></target>\n"
			+ "<its:locQualityIssues xml:id=\"VARID\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"rem1\" locQualityIssueEnabled=\"no\" locQualityIssueProfileRef=\"uri\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"rem2\" locQualityIssueSeverity=\"12.34\" locQualityIssueType=\"grammar\"/>\n"
			+ "</its:locQualityIssues>\n"
			+ "<its:locQualityIssues xml:id=\"VARID\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"rem1\" locQualityIssueEnabled=\"no\" locQualityIssueProfileRef=\"uri\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"rem2\" locQualityIssueSeverity=\"12.34\" locQualityIssueType=\"grammar\"/>\n"
			+ "</its:locQualityIssues>\n"
			+ "<its:locQualityIssues xml:id=\"VARID\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"rem3\" locQualityIssueSeverity=\"99\" locQualityIssueType=\"grammar\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"rem4\"/>\n"
			+ "</its:locQualityIssues>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", stripVariableID(result));
	}

	@Test
	public void testTerminologyAnnotations ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, locFR, null, "original.ext", null);
		writer.writeStartFile(null, null, null);
		ITextUnit tu = new TextUnit("tu1");
		TextFragment tf = tu.getSource().getFirstSegment().getContent();
		tf.append("t1");
		
		TextContainer tc = tu.getSource();
		GenericAnnotations anns = new GenericAnnotations();
		anns.add(new GenericAnnotation(GenericAnnotationType.TERM,
			GenericAnnotationType.TERM_CONFIDENCE, 0.5,
			GenericAnnotationType.TERM_INFO, "REF:info"));
		tc.setAnnotation(anns);
		
		anns = new GenericAnnotations();
		anns.add(new GenericAnnotation(GenericAnnotationType.TERM,
			GenericAnnotationType.TERM_CONFIDENCE, 0.7,
			GenericAnnotationType.TERM_INFO, "REF:info2"));
		tc.get(0).getContent().annotate(0, 2, GenericAnnotationType.GENERIC, anns);
		
		writer.writeTextUnit(tu);
		writer.writeEndFile();
		writer.close();		

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"unknown\" source-language=\"en\" target-language=\"fr\" datatype=\"x-undefined\">\n"
			+ "<body>\n<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\" its:term=\"yes\" its:termConfidence=\"0.5\" its:termInfoRef=\"info\">"
			+ "<mrk its:termConfidence=\"0.7\" its:termInfoRef=\"info2\" mtype=\"term\">t1</mrk></source>\n"
			+ "<target xml:lang=\"fr\" its:term=\"yes\" its:termConfidence=\"0.5\" its:termInfoRef=\"info\">"
			+ "<mrk its:termConfidence=\"0.7\" its:termInfoRef=\"info2\" mtype=\"term\">t1</mrk></target>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", stripVariableID(result));
	}

	@Test
	public void testmultipleLQI ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, locFR, null, "original.ext", null);
		writer.writeStartFile(null, null, null);
		ITextUnit tu = new TextUnit("tu1");
		
		TextFragment tf = tu.getSource().getFirstSegment().getContent();
		tf.setCodedText("Span 1 Span 2");
		//               0123456789012
		// First LQI
		GenericAnnotations anns = new GenericAnnotations();
		anns.add(new GenericAnnotation(GenericAnnotationType.LQI,
			GenericAnnotationType.LQI_COMMENT, "comment-1a"));
		anns.add(new GenericAnnotation(GenericAnnotationType.LQI,
			GenericAnnotationType.LQI_COMMENT, "comment-1b"));
		tf.annotate(0, 6, GenericAnnotationType.GENERIC, anns);
		// second LQI
		anns = new GenericAnnotations();
		anns.add(new GenericAnnotation(GenericAnnotationType.LQI,
			GenericAnnotationType.LQI_COMMENT, "comment-2a"));
		anns.add(new GenericAnnotation(GenericAnnotationType.LQI,
			GenericAnnotationType.LQI_COMMENT, "comment-2b"));
		tf.annotate(11, 17, GenericAnnotationType.GENERIC, anns); // +4 is for first marker

		tu.createTarget(locFR, false, IResource.COPY_ALL);

		writer.writeTextUnit(tu);
		writer.writeEndFile();
		writer.close();
		
		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"unknown\" source-language=\"en\" target-language=\"fr\" datatype=\"x-undefined\">\n"
			+ "<body>\n<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\"><mrk its:locQualityIssuesRef=\"#VARID\" mtype=\"x-its\">Span 1</mrk> "
			+ "<mrk its:locQualityIssuesRef=\"#VARID\" mtype=\"x-its\">Span 2</mrk></source>\n"
			+ "<target xml:lang=\"fr\"><mrk its:locQualityIssuesRef=\"#VARID\" mtype=\"x-its\">Span 1</mrk> "
			+ "<mrk its:locQualityIssuesRef=\"#VARID\" mtype=\"x-its\">Span 2</mrk></target>\n"
			+ "<its:locQualityIssues xml:id=\"VARID\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment-1a\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment-1b\"/>\n"
			+ "</its:locQualityIssues>\n"
			+ "<its:locQualityIssues xml:id=\"VARID\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment-2a\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment-2b\"/>\n"
			+ "</its:locQualityIssues>\n"
			+ "<its:locQualityIssues xml:id=\"VARID\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment-1a\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment-1b\"/>\n"
			+ "</its:locQualityIssues>\n"
			+ "<its:locQualityIssues xml:id=\"VARID\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment-2a\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment-2b\"/>\n"
			+ "</its:locQualityIssues>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", stripVariableID(result));
	}
	
	@Test
	public void testTextWithDefaultCodes ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, null, null, "original.ext", null);
		ITextUnit tu = new TextUnit("tu1");
		tu.getSource().getFirstSegment().getContent().append(TagType.OPENING, "z", "<z>");
		tu.getSource().getFirstSegment().getContent().append("s1");
		tu.getSource().getFirstSegment().getContent().append(TagType.CLOSING, "z", "</z>");
		tu.getSource().getFirstSegment().getContent().append(TagType.PLACEHOLDER, "br", "<br/>");
		writer.writeTextUnit(tu);
		writer.close();

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"original.ext\" source-language=\"en\" datatype=\"x-undefined\">\n"
			+ "<body>\n"
			+ "<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\"><g id=\"1\">s1</g><x id=\"2\"/></source>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testTextWithEncapsulatedCodes ()
		throws IOException
	{
		((XLIFFWriterParameters)writer.getParameters()).setPlaceholderMode(false);
		writer.create(root+"out.xlf", null, locEN, null, null, "original.ext", null);
		ITextUnit tu = new TextUnit("tu1");
		tu.getSource().getFirstSegment().getContent().append(TagType.OPENING, "z", "<z>");
		tu.getSource().getFirstSegment().getContent().append("s1");
		tu.getSource().getFirstSegment().getContent().append(TagType.CLOSING, "z", "</z>");
		tu.getSource().getFirstSegment().getContent().append(TagType.PLACEHOLDER, "br", "<br/>");
		writer.writeTextUnit(tu);
		writer.close();

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"original.ext\" source-language=\"en\" datatype=\"x-undefined\">\n"
			+ "<body>\n"
			+ "<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\"><bpt id=\"1\">&lt;z></bpt>s1<ept id=\"1\">&lt;/z></ept><ph id=\"2\">&lt;br/></ph></source>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testBasicSourceOnlyGtEscaped ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, null, null, "original.ext", null);
		ITextUnit tu = new TextUnit("tu1", "src1 with <>");
		((XLIFFWriterParameters)writer.getParameters()).setEscapeGt(true);
		writer.writeTextUnit(tu);
		writer.close();

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"original.ext\" source-language=\"en\" datatype=\"x-undefined\">\n"
			+ "<body>\n"
			+ "<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\">src1 with &lt;&gt;</source>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testBasicSourceAndTarget ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, locFR, null, "original.ext", null);
		ITextUnit tu = new TextUnit("tu1", "src1<&\"\'>");
		tu.setTarget(locFR, new TextContainer("trg1"));
		writer.writeTextUnit(tu);
		writer.close();

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"original.ext\" source-language=\"en\" target-language=\"fr\" datatype=\"x-undefined\">\n"
			+ "<body>\n"
			+ "<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\">src1&lt;&amp;\"\'></source>\n"
			+ "<target xml:lang=\"fr\">trg1</target>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testCompleteSourceAndTarget ()
		throws IOException
	{
		writer.create(root+"out.xlf", "skel.skl", locEN, locFR, "dtValue", "original.ext", "messageValue");
		ITextUnit tu = new TextUnit("tu1", "src1<&\"\'>");
		tu.setTarget(locFR, new TextContainer("trg1"));
		writer.writeTextUnit(tu);
		writer.close();

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<!--messageValue-->\n"
			+ "<file original=\"original.ext\" source-language=\"en\" target-language=\"fr\" datatype=\"x-dtValue\">\n"
			+ "<header><skl><external-file href=\"skel.skl\"></external-file></skl></header>\n"
			+ "<body>\n"
			+ "<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\">src1&lt;&amp;\"\'></source>\n"
			+ "<target xml:lang=\"fr\">trg1</target>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", result);
	}

	@Test
	public void testBasicWithITSProperties ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, null, null, "original.ext", null);
		ITextUnit tu = new TextUnit("tu1", "text");
		GenericAnnotation.addAnnotation(tu, new GenericAnnotation(GenericAnnotationType.DOMAIN,
			GenericAnnotationType.DOMAIN_VALUE, "dom1, dom2"));
		GenericAnnotation.addAnnotation(tu, new GenericAnnotation(GenericAnnotationType.EXTERNALRES,
			GenericAnnotationType.EXTERNALRES_VALUE, "http://example.com/res"));
		writer.writeTextUnit(tu);
		writer.close();

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"original.ext\" source-language=\"en\" datatype=\"x-undefined\">\n"
			+ "<body>\n"
			+ "<trans-unit id=\"tu1\" okp:itsDomain=\"dom1, dom2\" okp:itsExternalResourceRef=\"http://example.com/res\">\n"
			+ "<source xml:lang=\"en\">text</source>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", result);
	}
	
	@Test
	public void testVariousITSAnnotations ()
		throws IOException
	{
		writer.create(root+"out.xlf", null, locEN, locFR, null, "original.ext", null);
		writer.writeStartFile(null, null, null);
		
		ITextUnit tu = new TextUnit("tu1");
		TextFragment tf = tu.getSource().getFirstSegment().getContent();
		tf.append("Text ");
		
		// Translate:
		// There is no pre-defined annotation for Translate because the filters usually convert the not-transltable content into code
		// But you can add an mrk element for this this way:
		// Add the starting code for the original starting tag
		Code code = tf.append(TagType.OPENING, "span", "<its:span translate='no'>");
		// Set a general annotation of type 'protected', this will get you an mrk with mtype='protected'
		code.setAnnotation("protected", new InlineAnnotation("protected"));
		// Add the text not to translate
		tf.append("DO-NOT-TRANSLATE");
		// Add the original closing tag 
		tf.append(TagType.CLOSING, "span", "</its:span>");
		
		// Terminology:
		// There is a pre-define annotation for Terminology: GenericAnnotationType.TERM 
		tf.append(" term");
		GenericAnnotations anns = new GenericAnnotations(
			new GenericAnnotation(GenericAnnotationType.TERM,
				GenericAnnotationType.TERM_INFO, "Definition of 'term'"));
		// Annotate using offsets
		// Each inline code takes 2 chars. so:
		// Text ##DO-NOT-TRANSLATE## term
		// 0123456789012345678901234567890
		tf.annotate(26, 30, GenericAnnotationType.GENERIC, anns);
		
		// Localization Note
		tf.append(" etc.");
		anns = new GenericAnnotations(
			new GenericAnnotation(GenericAnnotationType.LOCNOTE,
				GenericAnnotationType.LOCNOTE_TYPE, "alert",
				GenericAnnotationType.LOCNOTE_VALUE, "Text of the localization note."));
		// Text ##DO-NOT-TRANSLATE## ##term## etc.
		// 0123456789012345678901234567890123456789
		tf.annotate(35, 39, GenericAnnotationType.GENERIC, anns);
		
		writer.writeTextUnit(tu);
		writer.writeEndFile();
		writer.close();		

		String result = readFile(root+"out.xlf");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\" xmlns:okp=\"okapi-framework:xliff-extensions\"" +
			" xmlns:its=\"http://www.w3.org/2005/11/its\" its:version=\"2.0\">\n"
			+ "<file original=\"unknown\" source-language=\"en\" target-language=\"fr\" datatype=\"x-undefined\">\n"
			+ "<body>\n<trans-unit id=\"tu1\">\n"
			+ "<source xml:lang=\"en\">Text <g id=\"1\"><mrk mtype=\"protected\">DO-NOT-TRANSLATE</g> <mrk comment=\"Definition of 'term'\"" +
			" mtype=\"term\">term</mrk> <mrk comment=\"Text of the localization note.\" okp:itsLocNoteType=\"alert\" mtype=\"x-its\">etc.</mrk></source>\n"
			+ "<target xml:lang=\"fr\">Text <g id=\"1\"><mrk mtype=\"protected\">DO-NOT-TRANSLATE</g> <mrk comment=\"Definition of 'term'\"" +
			" mtype=\"term\">term</mrk> <mrk comment=\"Text of the localization note.\" okp:itsLocNoteType=\"alert\" mtype=\"x-its\">etc.</mrk></target>\n"
			+ "</trans-unit>\n"
			+ "</body>\n</file>\n</xliff>\n", result);
	}

	private String readFile (String path)
		throws IOException
	{
		byte[] buffer = new byte[1024];
		int count = 0;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
		StringBuilder sb = new StringBuilder();
		while ((count = bis.read(buffer)) != -1) {
			sb.append(new String(buffer, 0, count));
		}
		String tmp = sb.toString().replace("\r\n", "\n");
		tmp = tmp.replace("\r", "\n");
		return tmp;
	}

	private String stripVariableID (String text) {
		text = text.replaceAll("locQualityIssuesRef=\"#(.*?)\"", "locQualityIssuesRef=\"#VARID\""); 
		text = text.replaceAll("xml:id=\"(.*?)\"", "xml:id=\"VARID\""); 
		return text;
	}
}
