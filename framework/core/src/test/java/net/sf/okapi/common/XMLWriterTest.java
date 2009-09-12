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

package net.sf.okapi.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.EmptyStackException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLWriterTest {

    XMLWriter writer;
    StringWriter sWriter;

    @Before
    public void setUp(){
        writer = new XMLWriter();
        sWriter = new StringWriter();
        writer.create(sWriter);
    }

    //TODO: possibly move out into integration test since it touches the file system. For now it is still fast.
    @Test
    public void createWithPath() throws IOException {
        final String filename = "target/test-classes/some/dir/to/be/created/some.xml";
        writer.create(filename);
        File f = new File(filename);
        assertTrue("A file should have been created along with the directory structure", f.exists());
        writer.writeStartDocument();
        writer.close();
        String xml = getFileAsString(f);
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", xml.trim());
        assertTrue(f.delete());
    }

    @Test
    public void createWithWriter(){
        final StringWriter sWriter = new StringWriter();
        writer.create(sWriter);
        writer.writeStartDocument();
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", sWriter.toString().trim());

    }

    @Test
    public void createNoParams(){
        assertEquals("writer's contents", "", sWriter.toString());
    }

    @Test
    public void writeStartDocument(){
        writer.writeStartDocument();
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", sWriter.toString().trim());
    }

    @Test
    public void writeEndDocumentNoStartTag(){
        writer.writeStartDocument();
        writer.writeEndDocument();
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", sWriter.toString().trim());
    }

    @Test
    public void writeStartElement(){
        writer.writeStartDocument();
        writer.writeStartElement("joe");
        String xml = sWriter.toString().trim();
        xml = xml.replaceAll("\n","");
        xml = xml.replaceAll("\r","");
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><joe", xml);
    }

    @Test
    public void writeStartElementWithPreviousStartElement(){
        writer.writeStartDocument();
        writer.writeStartElement("jack");
        writer.writeStartElement("diane");
        String xml = sWriter.toString().trim();
        xml = xml.replaceAll("\n","");
        xml = xml.replaceAll("\r","");
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><jack><diane", xml);
    }

    @Test
    public void writeEndElement(){
        writer.writeStartDocument();
        writer.writeStartElement("jack");
        writer.writeEndElement();
        String xml = sWriter.toString().trim();
        xml = xml.replaceAll("\n","");
        xml = xml.replaceAll("\r","");
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><jack></jack>", xml);
    }

    //TODO: Is this the behavior we want? Why not just first check for a previous tag and only end it if it is started?
    //For example, writeEndElementLineBreak checks the stack before attempting to end the tag.
    @Test(expected = EmptyStackException.class)
    public void writeEndElementNoStartElement(){
        writer.writeStartDocument();
        writer.writeEndElement();
        String xml = sWriter.toString().trim();
        xml = xml.replaceAll("\n","");
        xml = xml.replaceAll("\r","");
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", xml);
    }

    @Test
    public void writeEndElementLineBreakNoStartElement(){
        writer.writeStartDocument();
        writer.writeEndElementLineBreak();
        String xml = sWriter.toString().trim();
        xml = xml.replaceAll("\n","");
        xml = xml.replaceAll("\r","");
        assertEquals("writer's contents", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", xml);
    }

    @Test
    public void writeEndElementLineBreakStartElement(){
        writer.writeStartDocument();
        writer.writeStartElement("mary");
        writer.writeEndElementLineBreak();
        String xml = sWriter.toString();
        Pattern p = Pattern.compile("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>[\\n\\r]+<mary></mary>[\\r\\n]+", Pattern.MULTILINE);
        Matcher m = p.matcher(xml);
        assertTrue("New lines were not written", m.matches());
    }

    private String getFileAsString(final File file) throws IOException {
        final BufferedInputStream bis = new BufferedInputStream(
            new FileInputStream(file));
        final byte [] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();
        return new String(bytes);
    }

}
