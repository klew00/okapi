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

package net.sf.okapi.filters.idml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.IEncoder;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.writer.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.common.writer.GenericFilterWriter;

public class IDMLContentFilter implements IFilter {

	private String srcLang;
	private String encoding;
	private String docName;
	private XMLStreamReader reader;
	private LinkedList<Event> queue;
	private int tuId;
	private int otherId;
	private GenericSkeleton skel;
	private TextFragment frag;
	private boolean canceled;
	private boolean checkForEmpty;
	private int stack;
	private StringBuilder elemTag;
	private String elemName;
	private String elemPrefix;
	private Parameters params;
	private boolean wasEmpty;
	private boolean forSkel;

	public IDMLContentFilter () {
		params = new Parameters();
	}
	
	public void cancel () {
		canceled = true;
	}

	public void close () {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
			queue = null;
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public ISkeletonWriter createSkeletonWriter () {
		return new GenericSkeletonWriter();
	}
	
	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	public String getName () {
		return "okf_idmlcontent";
	}
	
	public String getMimeType () {
		return "text/xml";
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return (queue != null);
	}

	public Event next () {
		// Handle cancellation
		if ( canceled ) {
			queue = null;
			return new Event(EventType.CANCELED);
		}
		
		// Send any event already in the queue
		Event event;
		if ( queue.size() > 0 ) {
			event = queue.poll();
		}
		else { // Process the next event
			event = read();
			if ( event.getEventType() == EventType.END_DOCUMENT ) {
				queue = null;
			}
		}
		return event;
	}

	public void open (InputStream input) {
		try {
			close();
			// Open the input reader from the provided stream
			BOMAwareInputStream bis = new BOMAwareInputStream(input, "UTF-8");
			// Correct the encoding if we have detected a different one
			encoding = bis.detectEncoding();
			commonOpen(new InputStreamReader(bis, encoding));
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void open (CharSequence inputText) {
		encoding = "UTF-16";
		commonOpen(new StringReader(inputText.toString()));
	}

	public void open (URI inputURI) {
		try {
			docName = inputURI.getPath();
			open(inputURI.toURL().openStream());
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void setOptions (String sourceLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(sourceLanguage, null, defaultEncoding, generateSkeleton);
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		// Ignore encoding: We always output UTF-8 here
		srcLang = sourceLanguage;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	private void commonOpen (Reader inputReader) {
		close();
		canceled = false;

		XMLInputFactory fact = XMLInputFactory.newInstance();
		fact.setProperty(XMLInputFactory.IS_COALESCING, true);
		fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
		try {
			reader = fact.createXMLStreamReader(inputReader);
		}
		catch ( XMLStreamException e ) {
			throw new RuntimeException(e);
		}

		//TODO: Need to auto-detect the encoding and update 'encoding' variable
		// use reader.getCharacterEncodingScheme() ??? but start doc not reported
		
		// Set the start event
		stack = 0;
		tuId = 0;
		otherId = 0;
		queue = new LinkedList<Event>();
		elemTag = new StringBuilder();
		checkForEmpty = false;
		
		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, false); //TODO: UTF8BOM detection
		startDoc.setLanguage(srcLang);
		startDoc.setFilterParameters(getParameters());
		startDoc.setType("text/xml");
		startDoc.setMimeType("text/xml");
		queue.add(new Event(EventType.START_DOCUMENT, startDoc));

		// The XML declaration is not reported by the parser, so we need to
		// create it as a document part when starting
		skel = new GenericSkeleton();
		startDoc.setProperty(new Property(IEncoder.PROP_ENCODING, encoding, false));
		skel.append("<?xml version=\"1.0\" encoding=\"");
		skel.addValuePlaceholder(startDoc, IEncoder.PROP_ENCODING, "");
		skel.append("\"?>");
		startDoc.setSkeleton(skel);
	}

	private Event read () {
		skel = new GenericSkeleton();
		frag = null;
		int eventType;
		try {
			while ( reader.hasNext() ) {
				eventType = reader.next();
				if ( checkForEmpty ) checkForEmpty();
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					if ( frag == null ) { // Not extracting yet
						buildStartElement(); // Tag will be added when checking for empty
						if ( !params.breakAtContent && elemName.equals("ParagraphStyleRange") ) {
							frag = new TextFragment();
							stack++;
							forSkel = true;
						}
						else if ( elemName.equals("Content") ) {
							frag = new TextFragment();
							stack++;
							forSkel = true;
						}
					}
					else { // In extraction
						stack++;
						buildStartElement(); // Tag will be added when checking for empty
						//frag.append(TagType.OPENING, elemName, elemTag.toString());
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					if ( frag == null ) {
						if ( !wasEmpty ) skel.append(buildEndElement());
						DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false);
						return new Event(EventType.DOCUMENT_PART, dp, skel);
					}
					else {
						if ( --stack == 0 ) {
							TextUnit tu = new TextUnit(String.valueOf(++tuId));
							tu.setSourceContent(frag);
							tu.setMimeType("text/xml");
							skel.addContentPlaceholder(tu);
							skel.append(buildEndElement());
							return new Event(EventType.TEXT_UNIT, tu, skel);
						}
						else {
							if ( !wasEmpty ) {
								buildEndElement();
								frag.append(TagType.CLOSING, elemName, elemTag.toString());
							}
						}
					}
					wasEmpty = false;
					break;
					
				case XMLStreamConstants.SPACE:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.CHARACTERS:
					if ( frag == null ) {
						// UTF-8 is the encoding, no need to escape normal characters
						skel.append(Util.escapeToXML(reader.getText(), 0, false, null));
					}
					else {
						frag.append(reader.getText());
					}
					break;
					
				case XMLStreamConstants.COMMENT:
					if ( frag == null ) {
						skel.append("<!--"+ reader.getText() + "-->");
					}
					else {
						frag.append(TagType.PLACEHOLDER, null, "<!--"+ reader.getText() + "-->");
					}
					break;
					
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					if ( frag == null ) {
						skel.append("<?"+ reader.getPITarget() + " " + reader.getPIData() + "?>");
					}
					else {
						frag.append(TagType.PLACEHOLDER, null,
							"<?"+ reader.getPITarget() + " " + reader.getPIData() + "?>");
					}
					break;
					
				case XMLStreamConstants.DTD:
					//TODO
					break;
					
				case XMLStreamConstants.ENTITY_REFERENCE:
				case XMLStreamConstants.ENTITY_DECLARATION:
				case XMLStreamConstants.NAMESPACE:
				case XMLStreamConstants.NOTATION_DECLARATION:
				case XMLStreamConstants.ATTRIBUTE:
					break;
				case XMLStreamConstants.START_DOCUMENT:
				case XMLStreamConstants.END_DOCUMENT:
					break;
				}
			}
		}
		catch ( XMLStreamException e ) {
			throw new RuntimeException(e);
		}
		catch ( Throwable e ) {
			throw new RuntimeException(e);
		}
		
		// No more XML events
		Ending ending = new Ending(String.valueOf(++otherId));
		return new Event(EventType.END_DOCUMENT, ending, skel);
	}

	private String buildStartElement () {
		elemTag.setLength(0);
		elemPrefix = reader.getPrefix();
		if (( elemPrefix == null ) || ( elemPrefix.length()==0 )) {
			elemName = reader.getLocalName();
		}
		else {
			elemName = elemPrefix+":"+reader.getLocalName();
		}
		elemTag.append("<"+elemName);

		int count = reader.getNamespaceCount();
		for ( int i=0; i<count; i++ ) {
			elemPrefix = reader.getNamespacePrefix(i);
			elemTag.append(String.format(" xmlns%s=\"%s\"",
				((elemPrefix!=null) ? ":"+elemPrefix : ""),
				reader.getNamespaceURI(i)));
		}
		String attrName;
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			elemPrefix = reader.getAttributePrefix(i);
			attrName = String.format("%s%s",
				(((elemPrefix==null)||(elemPrefix.length()==0)) ? "" : elemPrefix+":"),
				reader.getAttributeLocalName(i));
			// UTF-8 is the encoding so no need to escape the normal characters
			elemTag.append(String.format(" %s=\"%s\"", attrName,
				Util.escapeToXML(reader.getAttributeValue(i), 3, false, null)));
		}
		//elemTag.append(">");
		checkForEmpty = true;
		return elemTag.toString();
	}
	
	private String buildEndElement () {
		elemPrefix = reader.getPrefix();
		if (( elemPrefix == null ) || ( elemPrefix.length()==0 )) {
			elemName = reader.getLocalName();
		}
		else {
			elemName = elemPrefix+":"+reader.getLocalName();
		}
		elemTag.setLength(0);
		elemTag.append("</"+elemName+">");
		return elemTag.toString();
	}

	private void checkForEmpty () {
		checkForEmpty = false;
		
		wasEmpty = false;
		if ( reader.getEventType() == XMLStreamConstants.END_ELEMENT ) {
			wasEmpty = elemName.equals(reader.getName().toString());
			//TODO: consume the end tag of the empty element
		}
		
		// Add the ending of previous element
		if ( wasEmpty ) {
			elemTag.append("/>");
		}
		else {
			elemTag.append(">");
		}
		if (( frag == null ) || forSkel ) {
			skel.append(elemTag.toString());
			forSkel = false;
		}
		else {
			frag.append((wasEmpty ? TagType.PLACEHOLDER : TagType.OPENING),
				elemName, elemTag.toString());
		}
	}

}
