/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.filters.xliff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public class XLIFFFilter implements IFilter {

	private final Logger logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	
	private boolean hasNext;
	private XMLStreamReader reader;
	private StartDocument startDoc;
	private int tuId;
	private int otherId;
	private String srcLang;
	private String trgLang;
	private LinkedList<FilterEvent> queue;
	private boolean canceled;
	private GenericSkeleton skel;
	private TextUnit tu;
	private Parameters params;
	private boolean sourceDone;
	private boolean targetDone;
	private TextContainer content;
	private String encoding;
	
	public XLIFFFilter () {
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
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public String getName () {
		return "XLIFFFilter";
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return hasNext;
	}

	public FilterEvent next () {
		try {
			// Check for cancellation first
			if ( canceled ) {
				queue.clear();
				queue.add(new FilterEvent(FilterEventType.CANCELED));
				hasNext = false;
			}
			
			// Parse next if nothing in the queue
			if ( queue.size() == 0 ) {
				if ( !read() ) {
					Ending ending = new Ending(String.valueOf(++otherId));
					ending.setSkeleton(skel);
					queue.add(new FilterEvent(FilterEventType.END_DOCUMENT, ending));
					queue.add(new FilterEvent(FilterEventType.FINISHED));
				}
			}
			
			// Return the head of the queue
			if ( queue.peek().getEventType() == FilterEventType.END_DOCUMENT ) {
				hasNext = false;
			}
			return queue.poll();
		}
		catch ( XMLStreamException e ) {
			throw new RuntimeException(e);
		}
	}

	public void open (InputStream input) {
		try {
			close();
			canceled = false;

			XMLInputFactory fact = XMLInputFactory.newInstance();
			fact.setProperty(XMLInputFactory.IS_COALESCING, true);
			fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			reader = fact.createXMLStreamReader(input);

			//TODO: Need to auto-detect the encoding and update 'encoding' variable
			// use reader.getCharacterEncodingScheme() ??? but start doc not reported
			
			tuId = 0;
			otherId = 0;
			// Set the start event
			hasNext = true;
			queue = new LinkedList<FilterEvent>();
			queue.add(new FilterEvent(FilterEventType.START));
			queue.add(new FilterEvent(FilterEventType.START_DOCUMENT, startDoc));
			// The XML declaration is not reported by the parser, so we need to
			// create it as a document part when starting
			skel = new GenericSkeleton();
			DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
			dp.setProperty(new Property("encoding", encoding, false));
			skel.append("<?xml version=\"1.0\" encoding=\"");
			skel.addRef(dp, "encoding", "");
			skel.append("\"?>");
			queue.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public void open (CharSequence inputText) {
		//TODO: Check for better solution, going from char to byte to read char is just not good
		open(new ByteArrayInputStream(inputText.toString().getBytes())); 
	}

	public void open (URL inputUrl) {
		try { //TODO: Make sure this is actually working (encoding?, etc.)
			// TODO: docRes should be always set with all opens... need better way
			startDoc = new StartDocument(String.valueOf(++otherId));
			startDoc.setName(inputUrl.getPath());
			startDoc.setLanguage(srcLang);
			startDoc.setIsMultilingual(true);
			startDoc.setFilterParameters(params);
			startDoc.setType("text/x-xliff");
			startDoc.setMimeType("text/x-xliff");
			open(inputUrl.openStream());
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void setOptions(String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLanguage;
		trgLang = targetLanguage;
		encoding = defaultEncoding;
	}

	public void setOptions(String sourceLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(sourceLanguage, null, defaultEncoding, generateSkeleton);
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	private boolean read () throws XMLStreamException {
		skel = new GenericSkeleton();
		int eventType;
		while ( reader.hasNext() ) {
			eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				String name = reader.getLocalName();
				if ( "trans-unit".equals(name) ) {
					return processTransUnit();
				}
				else if ( "file".equals(name) ) {
					return processStartFile();
				}
				else storeStartElement();
				break;
				
			case XMLStreamConstants.END_ELEMENT:
				storeEndElement();
				if ( "file".equals(reader.getLocalName()) ) {
					return processEndFile();
				}
				break;
				
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.CHARACTERS:
				skel.append(Util.escapeToXML(reader.getText(), 0, params.escapeGT));
				break;
				
			case XMLStreamConstants.COMMENT:
				skel.append("<!--"+ reader.getText() + "-->");
				break;
				
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				skel.append("<?"+ reader.getPITarget() + " " + reader.getPIData() + "?>");
				break;
				
			case XMLStreamConstants.DTD:
				//TODO: processDTD();
				break;
				
			case XMLStreamConstants.ENTITY_REFERENCE:
			case XMLStreamConstants.ENTITY_DECLARATION:
			case XMLStreamConstants.NAMESPACE:
			case XMLStreamConstants.NOTATION_DECLARATION:
			case XMLStreamConstants.ATTRIBUTE:
				break;
			case XMLStreamConstants.START_DOCUMENT:
				break;
			case XMLStreamConstants.END_DOCUMENT:
				break;
			}
		}
		return false;
	}

	private boolean processStartFile () {
		// Make a document part with skeleton between the previous event and now.
		// Spaces can go with the file element to reduce the number of events.
		// This allows to have only the file skeleton parts with the sub-document event
		if ( !skel.isEmpty(true) ) {
			DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
			skel = new GenericSkeleton(); // And create a new skeleton for the next event
			queue.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
		}
		
		StartSubDocument startSubDoc = new StartSubDocument(String.valueOf(++otherId));
		storeStartElement();
		String tmp = reader.getAttributeValue("", "original");
		if ( tmp == null ) throw new RuntimeException("Missing attribute 'original'.");
		else startSubDoc.setName(tmp);
		
		// Check the source language
		tmp = reader.getAttributeValue("", "source-language");
		if ( tmp == null ) throw new RuntimeException("Missing attribute 'source-language'.");
		if ( tmp.compareTo(srcLang) != 0 ) { // Warn about source language
			logger.warn(String.format("The source language declared in <file> is '%s'.", tmp));
		}
		
		// Check the target language
		tmp = reader.getAttributeValue("", "target-language");
		if ( tmp != null ) {
			if ( tmp.compareTo(trgLang) != 0 ) { // Warn about target language
				logger.warn(String.format("The target language declared in <file> is '%s'.", tmp));
			}
		}
		
		startSubDoc.setSkeleton(skel);
		queue.add(new FilterEvent(FilterEventType.START_SUBDOCUMENT, startSubDoc));
		return true;
	}

	private boolean processEndFile () {
		Ending ending = new Ending(String.valueOf(+otherId));
		ending.setSkeleton(skel);
		queue.add(new FilterEvent(FilterEventType.END_SUBDOCUMENT, ending));
		return true;
	}
	
	private void storeStartElement () {
		String prefix = reader.getPrefix();
		if (( prefix == null ) || ( prefix.length()==0 )) {
			skel.append("<"+reader.getLocalName());
		}
		else {
			skel.append("<"+prefix+":"+reader.getLocalName());
		}

		int count = reader.getNamespaceCount();
		for ( int i=0; i<count; i++ ) {
			prefix = reader.getNamespacePrefix(i);
			skel.append(String.format(" xmlns%s=\"%s\"",
				((prefix.length()>0) ? ":"+prefix : ""),
				reader.getNamespaceURI(i)));
		}
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i); 
			skel.append(String.format(" %s%s=\"%s\"",
				(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
				reader.getAttributeLocalName(i),
				reader.getAttributeValue(i)));
		}
		skel.append(">");
	}
	
	private void storeEndElement () {
		String ns = reader.getPrefix();
		if (( ns == null ) || ( ns.length()==0 )) {
			skel.append("</"+reader.getLocalName()+">");
		}
		else {
			skel.append("</"+ns+":"+reader.getLocalName()+">");
		}
	}

	private boolean processTransUnit () {
		try {
			// Make a document part with skeleton between the previous event and now.
			// Spaces can go with trans-unit to reduce the number of events.
			// This allows to have only the trans-unit skeleton parts with the TextUnit event
			if ( !skel.isEmpty(true) ) {
				DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
				skel = new GenericSkeleton(); // And create a new skeleton for the next event
				queue.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
			}
			
			// Process trans-unit
			sourceDone = false;
			targetDone = false;
			tu = new TextUnit(String.valueOf(++tuId));
			storeStartElement();

			String tmp = reader.getAttributeValue("", "translate");
			if ( tmp != null ) tu.setIsTranslatable(tmp.equals("yes"));

			tmp = reader.getAttributeValue("", "id");
			if ( tmp == null ) throw new RuntimeException("Missing attribute 'id'.");
			tu.setId(tmp);
			
			tmp = reader.getAttributeValue("", "resname");
			if ( tmp != null ) tu.setName(tmp);
			else if ( params.fallbackToID ) {
				tu.setName(tu.getId());
			}

			tmp = reader.getAttributeValue("", "restype");
			if ( tmp != null ) tu.setType(tmp);
			
			// Get the content
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				String name;
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					name = reader.getLocalName();
					if ( "source".equals(name) ) {
						storeStartElement();
						processSource(false);
						storeEndElement();
					}
					else if ( "target".equals(name) ) {
						storeStartElement();
						processTarget();
						checkTarget();
						storeEndElement();
					}
					else if ( "seg-source".equals(name) ) {
						storeStartElement();
						processSource(true);
						storeEndElement();
					}
					else if ( "note".equals(name) ) {
						checkTarget();
						storeStartElement();
						processNote();
						storeEndElement();
					}
					else {
						checkTarget();
						storeStartElement();
					}
					break;
				
				case XMLStreamConstants.END_ELEMENT:
					name = reader.getLocalName();
					checkTarget();
					if ( "trans-unit".equals(name) ) {
						storeEndElement();
						tu.setSkeleton(skel);
						tu.setMimeType("text/xml");
						queue.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));
						return true;
					}
					else storeEndElement();
					break;
				
				case XMLStreamConstants.SPACE:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.CHARACTERS:
					if ( !targetDone ) {
						// Faster that separating XMLStreamConstants.SPACE
						// from other data in the all process
						tmp = reader.getText();
						for ( int i=0; i<tmp.length(); i++ ) {
							if ( !Character.isWhitespace(tmp.charAt(i)) ) {
								checkTarget();
								break;
							}
						}
					}
					skel.append(Util.escapeToXML(reader.getText(), 0, params.escapeGT));
					break;
					
				case XMLStreamConstants.COMMENT:
					checkTarget();
					skel.append("<!--"+ reader.getText() + "-->");
					break;
				
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					checkTarget();
					skel.append("<?"+ reader.getPITarget() + " " + reader.getPIData() + "?>");
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
		return false;
	}
	
	private void processSource (boolean isSegSource) {
		if ( sourceDone ) {
			// Case where this entry is not the main one, but from an alt-trans
/*			TextContainer tmpCont = new TextContainer(null);
			String propName = "alt-trans";
			Property prop = new Property(propName, "", true);
			skel.addRef(tu, propName, currentLang);
			TextContainer tmpCont = new TextContainer(null);
			processContent("source", true, tmpCont, null);
			tu.setSourceProperty(prop);
*/		}
		else {
			skel.addRef(tu);
			TextContainer tc = processContent(isSegSource ? "seg-source" : "source", false, null);
			if ( isSegSource ) {
				//TODO
			}
			else {
				tu.setSource(tc);
			}
			sourceDone = true;
		}
	}
	
	private void processTarget () {
		if ( targetDone ) {
			// Case where this entry is not the main one, but from an alt-trans
			//TextContainer tmpCont = new TextContainer(null);
			//processContent("target", true, tmpCont, null);
		}
		else {
			skel.addRef(tu, trgLang);
			TextContainer tc = processContent("target", false, null);
			if ( !tc.isEmpty() ) {
				//resource.needTargetElement = false;
				tu.setTarget(trgLang, tc);
			}
		}
	}
	
	
	private void checkTarget () {
		if ( !sourceDone ) return;
		if ( targetDone ) return;
		targetDone = true;
	}
	
	/**
	 * Processes a segment content. Set the 'content' and set 'inCode' gobal variables 
	 * before calling this method with <source> or <target>.
	 * @param tagName The name of the element content that is being processed.
	 * @param store True if the data must be stored in the skeleton.
	 * This is used to merge later on.
	 * @param content The object where to put the code.
	 * @param inlineCodes Array where to save the in-line codes. Do not save if this parameter
	 * is set to null.
	 */
	private TextContainer processContent (String tagName,
		boolean store,
		ArrayList<Code> inlineCodes)
	{
		try {
			content = new TextContainer();
			int id = 0;
			Stack<Integer> idStack = new Stack<Integer>();
			idStack.push(id);
			int eventType;
			String name;
			if ( inlineCodes != null ) {
				inlineCodes.clear();
			}
			
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					content.append(reader.getText());
					if ( store )
						skel.append(Util.escapeToXML(reader.getText(),
							0, params.escapeGT));
					break;
		
				case XMLStreamConstants.END_ELEMENT:
					name = reader.getLocalName();
					if ( name.equals(tagName) ) {
						return content;
					}
					else if ( name.equals("g") || name.equals("mrk") ) {
						if ( store ) storeEndElement();
						content.append(TagType.CLOSING, name, name); 
						if ( inlineCodes != null ) {
							String tmp = reader.getPrefix();
							if (( tmp != null ) && ( tmp.length()>0 )) {
								tmp = tmp+":";
							}
							inlineCodes.add(
								new Code(TagType.CLOSING, name, "</"+tmp+name+">"));
						}
					}
					break;
					
				case XMLStreamConstants.START_ELEMENT:
					if ( store ) storeStartElement();
					name = reader.getLocalName();
					if ( name.equals("g") || name.equals("mrk") ) {
						idStack.push(++id);
						content.append(TagType.OPENING, name, name);
						if ( inlineCodes != null ) {
							String prefix = reader.getPrefix();
							StringBuilder tmpg = new StringBuilder();
							if (( prefix == null ) || ( prefix.length()==0 )) {
								tmpg.append("<"+reader.getLocalName());
							}
							else {
								tmpg.append("<"+prefix+":"+reader.getLocalName());
							}
							int count = reader.getNamespaceCount();
							for ( int i=0; i<count; i++ ) {
								prefix = reader.getNamespacePrefix(i);
								tmpg.append(String.format(" xmlns:%s=\"%s\"",
									((prefix.length()>0) ? ":"+prefix : ""),
									reader.getNamespaceURI(i)));
							}
							count = reader.getAttributeCount();
							for ( int i=0; i<count; i++ ) {
								if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
								prefix = reader.getAttributePrefix(i); 
								tmpg.append(String.format(" %s%s=\"%s\"",
									(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
									reader.getAttributeLocalName(i),
									reader.getAttributeValue(i)));
							}
							tmpg.append(">");
							inlineCodes.add(
								new Code(TagType.OPENING, name, tmpg.toString()));
						}
					}
					else if ( name.equals("x") ) {
						appendCode(TagType.PLACEHOLDER, ++id, name, store, content, inlineCodes);
					}
					else if ( name.equals("bpt") ) {
						idStack.push(++id);
						appendCode(TagType.OPENING, id, name, store, content, inlineCodes);
					}
					else if ( name.equals("ept") ) {
						appendCode(TagType.CLOSING, idStack.pop(), name, store, content, inlineCodes);
					}
					else if ( name.equals("ph") ) {
						appendCode(TagType.PLACEHOLDER, ++id, name, store, content, inlineCodes);
					}
					else if ( name.equals("it") ) {
						appendCode(TagType.PLACEHOLDER, ++id, name, store, content, inlineCodes);
					}
					break;
				}
			}
			return content;
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Appends a code, using the content of the node. Do not use for <g>-type tags.
	 * @param type The type of in-line code.
	 * @param id The id of the code to add.
	 * @param tagName The tag name of the in-line element to process.
	 * @param store True if we need to store the data in the skeleton.
	 * @param content The object where to put the code.
	 * @param inlineCodes Array where to save the original in-line code.
	 * Do not save if this parameter is null.
	 */
	private void appendCode (TagType tagType,
		int id,
		String tagName,
		boolean store,
		TextContainer content,
		ArrayList<Code> inlineCodes)
	{
		try {
			StringBuilder tmp = new StringBuilder();
			StringBuilder outerCode = null;
			if ( inlineCodes != null ) {
				outerCode = new StringBuilder();
				outerCode.append("<"+tagName);
				int count = reader.getAttributeCount();
				String prefix;
				for ( int i=0; i<count; i++ ) {
					if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
					prefix = reader.getAttributePrefix(i); 
					outerCode.append(String.format(" %s%s=\"%s\"",
						(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
						reader.getAttributeLocalName(i),
						reader.getAttributeValue(i)));
				}
				outerCode.append(">");
			}
			
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					if ( store ) storeStartElement();
					if ( inlineCodes != null ) {
						String prefix = reader.getPrefix();
						StringBuilder tmpg = new StringBuilder();
						if (( prefix == null ) || ( prefix.length()==0 )) {
							tmpg.append("<"+reader.getLocalName());
						}
						else {
							tmpg.append("<"+prefix+":"+reader.getLocalName());
						}
						int count = reader.getNamespaceCount();
						for ( int i=0; i<count; i++ ) {
							prefix = reader.getNamespacePrefix(i);
							tmpg.append(String.format(" xmlns:%s=\"%s\"",
								((prefix.length()>0) ? ":"+prefix : ""),
								reader.getNamespaceURI(i)));
						}
						count = reader.getAttributeCount();
						for ( int i=0; i<count; i++ ) {
							if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
							prefix = reader.getAttributePrefix(i); 
							tmpg.append(String.format(" %s%s=\"%s\"",
								(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
								reader.getAttributeLocalName(i),
								reader.getAttributeValue(i)));
						}
						tmpg.append(">");
						outerCode.append(tmpg.toString());
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					if ( store ) storeEndElement();
					if ( tagName.equals(reader.getLocalName()) ) {
						if ( inlineCodes != null ) {
							outerCode.append("</"+tagName+">");
							inlineCodes.add(
								new Code(tagType, tagName, outerCode.toString()));
						}
						content.append(tagType, tagName, tagName); //TODO: was: tmp.toString());
						return;	
					}
					break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					tmp.append(reader.getText());
					if ( inlineCodes != null )
						outerCode.append(Util.escapeToXML(reader.getText(),
							0, params.escapeGT));
					if ( store )
						skel.append(Util.escapeToXML(reader.getText(),
							0, params.escapeGT));
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void processNote () {
		try {
			StringBuilder tmp = new StringBuilder();
			if ( tu.hasProperty("note") ) {
				tmp.append(tu.getProperty("note"));
				tmp.append("\n---\n");
			}
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					skel.append(Util.escapeToXML(reader.getText(),
						0, params.escapeGT));
					tmp.append(reader.getText());
					break;
				case XMLStreamConstants.END_ELEMENT:
					String name = reader.getLocalName();
					if ( name.equals("note") ) {
						//TODO: Handle 'annotates', etc.
						tu.setProperty(new Property("note", tmp.toString(), true));
						return;
					}
					// Else: This should be an error as note are text only.
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
}
