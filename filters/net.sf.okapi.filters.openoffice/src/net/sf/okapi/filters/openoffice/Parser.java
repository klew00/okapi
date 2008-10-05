/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.openoffice;

import java.io.InputStream;
import java.net.URL;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;

import net.sf.okapi.common.filters.BaseParser;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class Parser extends BaseParser {

	protected static final String NSURI_TEXT = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";

	protected Document            resource;
	private XMLStreamReader       reader;
	private Stack<Boolean>        extract;

	public Parser () {
		resource = new Document();
		extract = new Stack<Boolean>();
	}

	public void close () {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public IContainable getResource () {
		return getFinalizedToken();
	}

	public void open (InputStream input) {
		try {
			close();
			XMLInputFactory fact = XMLInputFactory.newInstance();
			fact.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
			fact.setProperty(XMLInputFactory.IS_COALESCING, true);
			fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			reader = fact.createXMLStreamReader(input);
			reset();
			setFinishedParsing(false); //TODO: Should this be in reset()???
			extract.clear();
			extract.push(false);
		}
		catch ( XMLStreamException e ) {
			throw new RuntimeException(e);
		}
	}

	public void open (CharSequence input) {
		// TODO Auto-generated method stub
	}

	public void open (URL input) {
		// TODO Auto-generated method stub
	}

	public ParserTokenType parseNext () {
		try {
			if ( isFinishedParsing() ) {
				return ParserTokenType.ENDINPUT;
			}
			reset();

			while ( !isFinishedToken() && reader.hasNext() && !isCanceled() ) {

				switch ( reader.next() ) {
				
				case XMLStreamConstants.CHARACTERS:
					if ( extract.peek() ) appendToTextUnit(reader.getText());
					else appendToSkeletonUnit(reader.getText());
					break;
					
				case XMLStreamConstants.START_DOCUMENT:
					//TODO set resource.setTargetEncoding(SET REAL ENCODING);
					appendToSkeletonUnit("<?xml version=\"1.0\" "
						+ ((reader.getEncoding()==null) ? "" : "encoding=\""+reader.getEncoding()+"\"")
						+ "?>");
					break;
				
				case XMLStreamConstants.END_DOCUMENT:
					finalizeCurrentToken();
					setFinishedParsing(true);
					close();
					break;
				
				case XMLStreamConstants.START_ELEMENT:
					processStartElement();
					break;
				
				case XMLStreamConstants.END_ELEMENT:
					processEndElement();
					break;
				
				case XMLStreamConstants.COMMENT:
					appendToSkeletonUnit("<!--" + reader.getText() + "-->");
					break;

				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					appendToSkeletonUnit("<?" + reader.getPITarget() + " "
						+ reader.getPIData() + "?>");
					break;
				}
			
			} // End of main while		

			return getFinalizedTokenType();
		}
		catch ( XMLStreamException e ) {
			throw new RuntimeException(e);
		}
	}

	private String buildStartTag (String name) {
		StringBuilder tmp = new StringBuilder();
		// Tag name
		tmp.append("<" + name);
		
		// Namespaces
		String prefix;
		int count = reader.getNamespaceCount();
		for ( int i=0; i<count; i++ ) {
			prefix = reader.getNamespacePrefix(i);
			tmp.append(String.format(" xmlns%s=\"%s\"",
				((prefix.length()>0) ? ":"+prefix : ""),
				reader.getNamespaceURI(i)));
		}

		// Attributes
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i); 
			tmp.append(String.format(" %s%s=\"%s\"",
				(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
				reader.getAttributeLocalName(i),
				reader.getAttributeValue(i))); //TODO: Are quotes escaped???
		}

		tmp.append(">");
		return tmp.toString();
	}
	
	private String buildEndTag (String name) {
		return "</" + name + ">";
	}
	
	private String makePrintName () {
		String prefix = reader.getPrefix();
		if (( prefix == null ) || ( prefix.length()==0 )) {
			return reader.getLocalName();
		}
		// Else: with a prefix
		return prefix + ":" + reader.getLocalName();
	}
	
	private void processStartElement () throws XMLStreamException {
		String name = makePrintName();
		if ( name.equals("text:p") ) {
			appendToSkeletonUnit(buildStartTag(name));
			extract.push(true);
		}
		else if ( extract.peek() && name.equals("text:s") ) {
			String tmp = reader.getAttributeValue(NSURI_TEXT, "c");
			if ( tmp != null ) {
				int count = Integer.valueOf(tmp);
				for ( int i=0; i<count; i++ ) {
					appendToTextUnit(" ");
				}
			}
			reader.nextTag(); // Eat the end-element event
		}
		else if ( extract.peek() && name.equals("text:tab") ) {
			appendToTextUnit("\t");
			reader.nextTag(); // Eat the end-element event
		}
		else {
			if ( extract.peek() ) {
				appendToTextUnit(new Code(TagType.OPENING, name, buildStartTag(name)));
			}
			else {
				appendToSkeletonUnit(buildStartTag(name));
			}
		}
	}

	private void processEndElement () {
		String name = makePrintName();
		if ( name.equals("text:p") ) {
			finalizeCurrentToken();
			extract.pop();
			// Add line break because ODT files don't have any
			appendToSkeletonUnit(buildEndTag(name)+"\n");
		}
		else {
			if ( extract.peek() ) {
				appendToTextUnit(new Code(TagType.CLOSING, name, buildEndTag(name)));
			}
			else {
				appendToSkeletonUnit(buildEndTag(name));
				if ( name.equals("style:style")
					|| ( name.equals("text:list-style"))
					|| ( name.equals("draw:frame"))
					|| ( name.equals("text:list"))
					|| ( name.equals("text:list-item")) ) {
					appendToSkeletonUnit("\n");
				}
			}
		}
	}
}
