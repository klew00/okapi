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

package net.sf.okapi.common.filterwriter;

import static net.sf.okapi.common.TestUtil.getFileAsString;
import net.sf.okapi.common.XMLWriter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnit;

/**
 * @author HaslamJD
 */
public class TMXWriterTest {

    final static File TMX_File = new File("target/test-classes/tmxwritertest_tmxfile.tmx");

    TMXWriter tmxWriter;
    StringWriter strWriter;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	private LocaleId locKR = LocaleId.fromString("kr");

    @Before
    public void setUp() {
    	strWriter = new StringWriter();
    	XMLWriter xmlWriter = new XMLWriter(strWriter);
    	tmxWriter = new TMXWriter(xmlWriter);
    	createTmxHeader();
    }

    @Test
    public void constructorStringPath() {
    	TMX_File.delete();
    	tmxWriter = new TMXWriter(TMX_File.getPath());
    	assertTrue("tmx file should have been created", TMX_File.exists());
    }

    @Test
    public void writeStartDocumentWithFile() throws IOException {
    	TMX_File.delete();
    	tmxWriter = new TMXWriter(TMX_File.getPath());
    	createTmxHeader();
    	tmxWriter.close();
    	String tmx = getFileAsString(TMX_File);
    	testHeader(tmx);
    }

    @Test
    public void constructorWithXmlWriter() {
    	String tmx = strWriter.toString();
    	testHeader(tmx);
    }

    @Test
    public void emptyDocument() {
    	tmxWriter.writeEndDocument();
    	String tmx = stripNewLinesAndReturns(strWriter.toString());
    	assertEquals("Header and Footer Only", expectedHeaderTmx + expectedFooterTmx, tmx);
    }

    @Test
    public void testTmxTuNoAttributes() {
    	ITextUnit tu = createTextUnit("id", "SourceContent", "TargetContent", null);
    	tmxWriter.writeTUFull(tu);
    	testTu(tu, strWriter.toString());
    }

    @Test
    public void testTmxTuSingleProp() {
    	ITextUnit tu = createTextUnit("id", "SourceContent", "TargetContent", new String[][]{{"prop1", "value1"}});
    	tmxWriter.writeTUFull(tu);
    	testTu(tu, strWriter.toString());
    }

    @Test
    public void testTmxTuMultiProp() {
    	ITextUnit tu = createTextUnit("id", "SourceContent", "TargetContent", new String[][]{{"prop1", "value1"}, {"prop2", "value2"}});
    	tmxWriter.writeTUFull(tu);
    	testTu(tu, strWriter.toString());
    }

    @Test
    public void testTmxTuMultiLang() {
    	ITextUnit tu = createTextUnit("id", "SourceContent", "TargetContent", new String[][]{{"prop1", "value1"}, {"prop2", "value2"}});
    	tu.setTargetContent(locKR, new TextFragment("KoreanTarget"));
    	tmxWriter.writeTUFull(tu);
    	testTu(tu, strWriter.toString());
    }

    private void testHeader(String tmx) {
    	tmx = stripNewLinesAndReturns(tmx);
    	assertEquals("TMX Header", expectedHeaderTmx, tmx);
    }

    private final static String expectedHeaderTmx = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tmx version=\"1.4\"><header creationtool=\"pensieve\" creationtoolversion=\"0.0.1\" segtype=\"sentence\" o-tmf=\"pensieve_format\" adminlang=\"en\" srclang=\"en\" datatype=\"unknown\"></header><body>";
    private final static String expectedFooterTmx = "</body></tmx>";

    private void testTu(ITextUnit tu, String tmx) {
    	tmx = stripNewLinesAndReturns(tmx);
    	String properties = getProps(tu);
    	String targetTuvs = getTargetTuvs(tu);

    	String expectedTMX = 
    		"<tu tuid=\"" + tu.getName() + "\">" +
    		properties +
    		"<tuv xml:lang=\"en\">" +
    		"<seg>" + tu.getSource().getSegments().getFirstContent().toText() + "</seg>" +
    		"</tuv>" +
    		targetTuvs +
    		"</tu>";
    	assertEquals("TU Element", expectedHeaderTmx + expectedTMX, tmx);
    }

    private String getProps(ITextUnit tu) {
    	String properties = "";
    	for (String propName : tu.getPropertyNames()) {
    		properties += "<prop type=\"" + propName + "\">" + tu.getProperty(propName) + "</prop>";
    	}
    	return properties;
    }

    private String getTargetTuvs(ITextUnit tu) {
    	String targetTuvs = "";
    	for (LocaleId langName : tu.getTargetLocales()) {
    		targetTuvs += "<tuv xml:lang=\"" + langName + "\">" + "<seg>" + 
    			tu.getTargetSegments(langName).getFirstContent().toText() + "</seg>" + "</tuv>";
    	}
    	return targetTuvs;
    }

    private ITextUnit createTextUnit(String id, String sourceContent, String targetContent, String[][] attributes) {
    	ITextUnit tu = new TextUnit(id);
    	tu.setName(id);
    	tu.setSourceContent(new TextFragment(sourceContent));
    	tu.setTargetContent(locFR, new TextFragment(targetContent));
    	if (attributes != null) {
    		for (String[] kvp : attributes) {
    			tu.setProperty(new Property(kvp[0], kvp[1]));
    		}
    	}
    	return tu;
    }

    private void createTmxHeader() {
    	tmxWriter.writeStartDocument(locEN, locFR, "pensieve", "0.0.1", "sentence", "pensieve_format", "unknown");
    }

    private String stripNewLinesAndReturns(String tmx) {
    	return tmx.replaceAll("[\\n\\r]+", "");
    }
}
