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

package net.sf.okapi.common;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Stack;

import net.sf.okapi.common.Util;

/**
 * Helper class to write XML documents.
 */
public class XMLWriter {
	
	private CharsetEncoder encoder;
	private PrintWriter writer = null;
	private boolean inStartTag;
	private Stack<String> elements;
	private StringWriter strWriter = null;

	private final String lineBreak = System.getProperty("line.separator");

	/**
	 * Creates a new XML document on disk.
	 * @param path the full path of the document to create. If any directory in the
	 * path does not exists yet it will be created automatically. The document is 
	 * always written in UTF-8.
	 */
	public void create (String path) {
		try {
			Util.createDirectories(path);
			OutputStream output = new BufferedOutputStream(new FileOutputStream(path));
			Charset charset = Charset.forName("UTF-8");
			encoder = charset.newEncoder();
			writer = new PrintWriter(new OutputStreamWriter(output, encoder));
			inStartTag = false;
			elements = new Stack<String>();
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates a new XML document in a string.
	 * Use the method {@link #getStringOutput()} to get the resulting string.
	 */
	public void create () {
		strWriter = new StringWriter();
		writer = new PrintWriter(strWriter);
		inStartTag = false;
		elements = new Stack<String>();
	}

	/**
	 * Gets the string buffer of the XML document created with {@link #create()}.
	 * @return the string buffer of the XML document created with {@link #create()}.
	 */
	public String getStringOutput () {
		close();
		return strWriter.toString();
	}
	
	/**
	 * Closes the writer and release any associated resources.
	 */
	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
		if ( elements != null ) {
			elements.clear();
			elements = null;
		}
	}
	
	/**
	 * Writes the start of the document. This method generate the XML declaration.
	 */
	public void writeStartDocument () {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
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
		if ( !elements.isEmpty() ) {
			writer.println("</" + elements.pop() + ">");
		}
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
	 * Writes a chunk of raw XML.
	 * @param xmlData the data to output. No escaping is performed.
	 */
	public void writeRawXML (String xmlData) {
		closeStartTag();
		writer.write(xmlData);
	}
	
	/**
	 * Writes a comment.
	 * @param text the text of the comment.
	 */
	public void writeComment (String text) {
		closeStartTag();
		writer.write("<!--");
		writer.write(text.replace("\n", lineBreak));
		writer.write("-->");
	}
	
	/**
	 * Writes a line-break.
	 */
	public void writeLineBreak () {
		closeStartTag();
		writer.println("");
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
