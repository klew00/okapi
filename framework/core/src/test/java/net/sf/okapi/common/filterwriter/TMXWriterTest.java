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

/**
 *
 * @author HaslamJD
 */
public class TMXWriterTest {

    final static File TMX_File = new File("target/test-classes/tmxwritertest_tmxfile.tmx");
    TMXWriter tmxWriter;

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Before
    public void setUp() {
        TMX_File.delete();
        tmxWriter = new TMXWriter(TMX_File.getPath());
    }

    @Test
    public void constructorStringPath() {
        assertTrue("tmx file should have been created", TMX_File.exists());
    }

    @Test
    public void writeStartDocument() throws IOException {
        createTmxHeader();
        String tmx = getFileAsString(TMX_File);
        testHeader(tmx);
    }

    @Test
    public void constructorWithXmlWriter() {
        StringWriter strWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(strWriter);
        tmxWriter = new TMXWriter(xmlWriter);
        createTmxHeader();
        String tmx = strWriter.toString();
        testHeader(tmx);
    }

    private void createTmxHeader() {
        tmxWriter.writeStartDocument("EN", "FR", "pensieve", "0.0.1", "sentence", "pensieve_format", "unknown");
        tmxWriter.close();
    }

    private void testHeader(String tmx) {
        tmx = tmx.replaceAll("[\\n\\r]+", "");
        String expectedTMX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tmx version=\"1.4\"><header creationtool=\"pensieve\" creationtoolversion=\"0.0.1\" segtype=\"sentence\" o-tmf=\"pensieve_format\" adminlang=\"en\" srclang=\"EN\" datatype=\"unknown\"></header><body>";
        assertEquals("TMX Header", expectedTMX, tmx);
    }
}
