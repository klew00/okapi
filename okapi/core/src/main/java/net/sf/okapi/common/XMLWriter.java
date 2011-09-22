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

package net.sf.okapi.common;

import net.sf.okapi.common.exceptions.OkapiIOException;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

/**
 * Helper class to write XML documents.
 */
public class XMLWriter {

    private final PrintWriter writer;
    private boolean inStartTag;
    private Stack<String> elements = new Stack<String>();
    private String lineBreak = System.getProperty("line.separator");
    
    /**
     * Creates a new XML document on disk.
     * @param path the full path of the document to create. If any directory in the
     * path does not exists yet it will be created automatically. The document is
     * always written in UTF-8 and the type of line-breaks is the one of the
     * platform where the application runs.
     */
    public XMLWriter (String path) {
    	try {
    		Util.createDirectories(path);
    		final OutputStreamWriter osw = new OutputStreamWriter(
				new BufferedOutputStream(new FileOutputStream(path)),
				"UTF-8");
    		writer = new PrintWriter(osw);
    	}
    	catch ( IOException e ) {
    		throw new OkapiIOException(e);
    	}
    }

    /**
     * Creates a new XML document for a given writer object.
     * @param writer the writer to use to output the document. If this writer outputs to
     * bytes it must be set to output in UTF-8.
     */
    public XMLWriter (Writer writer) {
    	this.writer = new PrintWriter(writer);
    }
    
    protected void finalize()
    	throws Throwable
    {
    	try {
    		close();
    	}
    	finally {
    		super.finalize();
    	}
    }
    
    /**
     * Sets the type of line-break to use when writing out the
     * document. By default, the type is the one of the platform.
     * This method can be used for example, to force the line-breaks
     * to be "\n" instead of "\r\n" when writing out on a string that
     * will be later written out on a file where "\n" will be converted
     * to "\r\n" (therefore avoiding to end up with "\r\r\n").
     * @param lineBreak the new type of line-break to use.
     */
    public void setLineBreak (String lineBreak) {
    	this.lineBreak = lineBreak;
    }
    
    /**
     * Gets the line break string currently used for this writer.
     * @return the line break string used.
     */
    public String getLineBreak () {
    	return lineBreak;
    }

    /**
     * Closes the writer and release any associated resources.
     */
    public void close () {
    	if ( writer != null ) {
    		writer.flush();
    		writer.close();
    	}
    	if ( elements != null ) {
    		elements.clear();
    		elements = null;
    	}
    }

    /**
     * Writes the start of the document. This method generates the XML declaration.
     */
    public void writeStartDocument () {
    	writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+lineBreak);
    }

    /**
     * Writes the start of a XHTML document, including the head and start of body element.
     * @param title the title of the document (can be null).
     */
    public void writeStartHTMLDocument (String title) {
    	writer.print("<html>"+lineBreak);
    	writer.print("<head>"+lineBreak);
    	writer.print("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+lineBreak);
    	if ( title != null ) {
    		writeElementString("title", title);
    		writeLineBreak();
    	}
    	writer.print("</head>"+lineBreak);
    }

    /**
     * Writes the end of the document. This method closes any open tag, and
     * flush the writer.
     */
    public void writeEndDocument () {
    	closeStartTag();
    	writer.flush();
    }

    /**
     * Writes the start of an element.
     * @param name the name of the element to start.
     */
    public void writeStartElement (String name) {
    	closeStartTag();
    	elements.push(name);
    	writer.write("<" + name);
    	inStartTag = true;
    }

    /**
     * Writes the end of the last started element.
     */
    public void writeEndElement () {
    	closeStartTag();
    	writer.write("</" + elements.pop() + ">");
    }

    /**
     * Writes the end of the last started element and writes a line-break.
     */
    public void writeEndElementLineBreak () {
    	closeStartTag();
   		writer.write("</" + elements.pop() + ">"+lineBreak);
    }

    /**
     * Writes an element and its content.
     * @param name the name of the element to write.
     * @param content the content to enclose inside this element.
     */
    public void writeElementString (String name,
   		String content)
    {
    	closeStartTag();
    	writer.write("<" + name + ">");
    	writer.write(Util.escapeToXML(content, 0, false, null));
    	writer.print("</" + name + ">");
    }

    /**
     * Writes an attribute and its associated value. You must use
     * {@link #writeStartElement(String)} just before.
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     */
    public void writeAttributeString (String name,
   		String value)
    {
    	writer.write(" " + name + "=\"" + Util.escapeToXML(value, 3, false, null) + "\"");
    }

    /**
     * Writes a string. The text is automatically escaped.
     * @param text the text to output.
     */
    public void writeString (String text) {
    	closeStartTag();
    	writer.write(Util.escapeToXML(text, 0, false, null).replace("\n", lineBreak));
    }

    /**
     * Writes a chunk of raw XML (where line-breaks are assumed to be normalized to \n).
     * @param xmlData the data to output. No escaping is performed, but the line-breaks are
     * converted to the line-break type of the output.
     */
    public void writeRawXML (String xmlData) {
    	closeStartTag();
    	writer.write(xmlData.replace("\n", lineBreak));
    }

    /**
     * Writes a comment.
     * @param text the text of the comment.
     * @param withLineBreak add a line break at the end of the comment.
     */
    public void writeComment (String text,
    	boolean withLineBreak)
    {
    	closeStartTag();
    	writer.write("<!--");
    	writer.write(text.replace("\n", lineBreak));
    	writer.write("-->");
    	if ( withLineBreak ) writer.write(lineBreak);
    }

    /**
     * Writes a line-break, and if the writer is in a start tag, close it before.
     */
    public void writeLineBreak () {
    	closeStartTag();
    	writer.write(lineBreak);
    }

    /**
     * Closes the tag of the last start tag output, if needed.
     */
    private void closeStartTag () {
    	if ( inStartTag ) {
    		writer.write(">");
    		inStartTag = false;
    	}
    }

}
