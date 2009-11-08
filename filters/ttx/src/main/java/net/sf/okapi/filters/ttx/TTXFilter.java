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

package net.sf.okapi.filters.ttx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.AltTransAnnotation;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

import org.codehaus.stax2.XMLInputFactory2;

public class TTXFilter implements IFilter {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private boolean hasNext;
	private XMLStreamReader reader;
	private String docName;
	private int tuId;
	private int otherId;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private LinkedList<Event> queue;
	private boolean canceled;
	private GenericSkeleton skel;
	private TextUnit tu;
	private Parameters params;
	private boolean sourceDone;
	private boolean targetDone;
	private TextContainer content;
	private String encoding;
	private Stack<Boolean> preserveSpaces;
	private String lineBreak;
	private boolean hasUTF8BOM;
	private StringBuilder buffer;
	
	public TTXFilter () {
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
			hasNext = false;
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}

	public String getName () {
		return "okf_ttx";
	}

	public String getDisplayName () {
		return "TTX Filter (ALPHA)";
	}

	public String getMimeType () {
		return MimeTypeMapper.TTX_MIME_TYPE;
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MimeTypeMapper.TTX_MIME_TYPE,
			getClass().getName(),
			"TTX",
			"Configuration for Trados Translation eXchange (TTX) documents."));
		return list;
	}
	
	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return hasNext;
	}

	public Event next () {
		try {
			// Check for cancellation first
			if ( canceled ) {
				queue.clear();
				queue.add(new Event(EventType.CANCELED));
				hasNext = false;
			}
			
			// Parse next if nothing in the queue
			if ( queue.isEmpty() ) {
				if ( !read() ) {
					Ending ending = new Ending(String.valueOf(++otherId));
					ending.setSkeleton(skel);
					queue.add(new Event(EventType.END_DOCUMENT, ending));
				}
			}
			
			// Return the head of the queue
			if ( queue.peek().getEventType() == EventType.END_DOCUMENT ) {
				hasNext = false;
			}
			return queue.poll();
		}
		catch ( XMLStreamException e ) {
			throw new OkapiIOException(e);
		}
	}

	public void open (RawDocument input) {
		open(input, true);
	}
	
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		try {
			close();
			canceled = false;

			XMLInputFactory fact = XMLInputFactory.newInstance();
			fact.setProperty(XMLInputFactory.IS_COALESCING, true);
			fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			
			//fact.setXMLResolver(new DefaultXMLResolver());
			//TODO: Resolve the re-construction of the DTD, for now just skip it
			fact.setProperty(XMLInputFactory.SUPPORT_DTD, false);

			// Determine encoding based on BOM, if any
			input.setEncoding("UTF-8"); // Default for XML, other should be auto-detected
			BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
			detector.detectBom();
//TODO: We need to let the XMLStreamReader detect its encoding, only provide a fall back.
//We could just not use BOMNewlineEncodingDetector, but then we would not have info on BOM and linebreak type.
			input.setEncoding(detector.getEncoding());
			reader = fact.createXMLStreamReader(input.getReader());

			String realEnc = reader.getCharacterEncodingScheme();
			if ( realEnc != null ) encoding = realEnc;
			else encoding = input.getEncoding();

			srcLoc = input.getSourceLocale();
			if ( srcLoc == null ) throw new NullPointerException("Source language not set.");
			trgLoc = input.getTargetLocale();
			if ( trgLoc == null ) throw new NullPointerException("Target language not set.");
			hasUTF8BOM = detector.hasUtf8Bom();
			lineBreak = detector.getNewlineType().toString();
			if ( input.getInputURI() != null ) {
				docName = input.getInputURI().getPath();
			}

			preserveSpaces = new Stack<Boolean>();
			preserveSpaces.push(false);
			tuId = 0;
			otherId = 0;
			// Set the start event
			hasNext = true;
			queue = new LinkedList<Event>();
			buffer = new StringBuilder();
			
			StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
			startDoc.setName(docName);
			startDoc.setEncoding(encoding, hasUTF8BOM);
			startDoc.setLocale(srcLoc);
			startDoc.setFilterParameters(getParameters());
			startDoc.setFilterWriter(createFilterWriter());
			startDoc.setType(MimeTypeMapper.XLIFF_MIME_TYPE);
			startDoc.setMimeType(MimeTypeMapper.XLIFF_MIME_TYPE);
			startDoc.setMultilingual(true);
			startDoc.setLineBreak(lineBreak);
			queue.add(new Event(EventType.START_DOCUMENT, startDoc));

			// The XML declaration is not reported by the parser, so we need to
			// create it as a document part when starting
			skel = new GenericSkeleton();
			startDoc.setProperty(new Property(Property.ENCODING, encoding, false));
			skel.append("<?xml version=\"1.0\" encoding=\"");
			skel.addValuePlaceholder(startDoc, Property.ENCODING, LocaleId.EMPTY);
			skel.append("\"?>");
			startDoc.setSkeleton(skel);
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}
	
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	private boolean read () throws XMLStreamException {
		skel = new GenericSkeleton();
		int eventType;
		buffer.setLength(0);
		
		while ( reader.hasNext() ) {
			eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				String name = reader.getLocalName();
				if ( "Tu".equals(name) ) {
					return processTU();
				}
//				if ( "ut".equals(name) ) {
//					processTopUT();
//				}
				else {
					storeStartElement();
				}
				break;
				
			case XMLStreamConstants.END_ELEMENT:
				storeEndElement();
				break;
				
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.CDATA:
				skel.append(reader.getText().replace("\n", lineBreak));
				break;
			case XMLStreamConstants.CHARACTERS: //TODO: escape unsupported chars
				skel.append(Util.escapeToXML(reader.getText().replace("\n", lineBreak), 0, params.getEscapeGT(), null));
				break;
				
			case XMLStreamConstants.COMMENT:
				skel.append("<!--"+ reader.getText().replace("\n", lineBreak) + "-->");
				break;
				
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				skel.append("<?"+ reader.getPITarget() + " " + reader.getPIData() + "?>");
				break;
				
			case XMLStreamConstants.DTD:
				//TODO: Reconstruct the DTD declaration
				// but how? nothing is available to do that
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
				((prefix!=null) ? ":"+prefix : ""),
				reader.getNamespaceURI(i)));
		}
		String attrName;
		boolean ps = preserveSpaces.peek();
		
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i);
			attrName = String.format("%s%s",
				(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
				reader.getAttributeLocalName(i));
			skel.append(String.format(" %s=\"%s\"", attrName,
				Util.escapeToXML(reader.getAttributeValue(i).replace("\n", lineBreak), 3, params.getEscapeGT(), null)));
			if ( attrName.equals("xml:space") ) {
				ps = reader.getAttributeValue(i).equals("preserve");
			}
		}
		skel.append(">");
		preserveSpaces.push(ps);
	}
	
	private void storeEndElement () {
		String prefix = reader.getPrefix();
		if (( prefix != null ) && ( prefix.length()>0 )) {
			skel.append("</"+prefix+":"+reader.getLocalName()+">");
		}
		else {
			skel.append("</"+reader.getLocalName()+">");
		}
		preserveSpaces.pop();
	}

	// Case of a UT element outside a TUV, that is an un-segmented/translate code.
	private void processTopUT () {
		String tmp = reader.getAttributeValue("", "Style");
		// Default is internal
		boolean isExternal = false;
		if ( tmp != null ) {
			isExternal = "external".equals(tmp);
		}
		
	}
	
	private boolean processTU () {
		try {
			// Make a document part with skeleton between the previous event and now.
			// Spaces can go with Tu to reduce the number of events.
			// This allows to have only the Tu skeleton parts with the TextUnit event
			if ( !skel.isEmpty(true) ) {
				DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
				skel = new GenericSkeleton(); // And create a new skeleton for the next event
				queue.add(new Event(EventType.DOCUMENT_PART, dp));
			}
			
			// Process Tu
			sourceDone = false;
			targetDone = false;
			tu = new TextUnit(String.valueOf(++tuId));
			storeStartElement();

			String tmp = reader.getAttributeValue("", "PercentageMatch");
			if ( tmp != null ) {
				//TODO
			}

			// Get the content
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				String name;
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					name = reader.getLocalName();
					if ( "Tuv".equals(name) ) {
						storeStartElement();
						processTUV();
						storeEndElement();
					}
					else {
						addTargetIfNeeded();
						storeStartElement();
					}
					break;
				
				case XMLStreamConstants.END_ELEMENT:
					name = reader.getLocalName();
					//addTargetIfNeeded();
					if ( "Tu".equals(name) ) {
						addTargetIfNeeded();
						storeEndElement();
						tu.setSkeleton(skel);
						tu.setMimeType(MimeTypeMapper.TTX_MIME_TYPE);
						queue.add(new Event(EventType.TEXT_UNIT, tu));
						return true;
					}
					// Else: just store the end
					storeEndElement();
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
								addTargetIfNeeded();
								break;
							}
						}
					}
					//TODO: escape unsupported chars
					skel.append(Util.escapeToXML(reader.getText().replace("\n", lineBreak), 0, params.getEscapeGT(), null));
					break;
					
				case XMLStreamConstants.COMMENT:
					//addTargetIfNeeded();
					skel.append("<!--"+ reader.getText().replace("\n", lineBreak) + "-->");
					break;
				
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					//addTargetIfNeeded();
					skel.append("<?"+ reader.getPITarget() + " " + reader.getPIData() + "?>");
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException("Error processing Tu element.", e);
		}
		return false;
	}
	
	private void processTUV () {
		TextContainer tc;

		// Detect whether it is source or target 
		LocaleId locId;
		boolean isTarget = sourceDone;
		String tmp = reader.getAttributeValue("", "Lang");
		if ( tmp != null ) {
			locId = LocaleId.fromString(tmp);
		}
		else {
			if ( isTarget ) locId = trgLoc;
			else locId = srcLoc;
		}
		//TODO: check actual languages against expected languages
		
		// Add the placeholder for the content
		if ( isTarget ) {
			skel.addContentPlaceholder(tu, locId);
		}
		else {
			skel.addContentPlaceholder(tu);
		}

		// Get the content
		tc = processContent("Tuv", false);
		
		// Finalize the content
		if ( !preserveSpaces.peek() ) TextFragment.unwrap(tc.getContent());
		tu.setPreserveWhitespaces(preserveSpaces.peek());
		if ( isTarget ) {
			tu.setTarget(locId, tc);
			targetDone = true;
		}
		else {
			tu.setSource(tc);
			sourceDone = true;
		}
	}
	
	private void addTargetIfNeeded () {
		if ( targetDone ) return; // Nothing to add
		// If the target language is the same as the source, we should not create new <target>
		if ( srcLoc.equals(trgLoc) ) return; 
		//Else: this trans-unit has no target, we add it here in the skeleton
		// so we can merge target data in it when writing out the skeleton
		skel.append(String.format("<Tuv Lang=\"%s\">", trgLoc));
		skel.addContentPlaceholder(tu, trgLoc);
		skel.append("</Tuv>");
		skel.append(lineBreak);
		targetDone = true;
	}
	
	/**
	 * Processes a segment content.
	 * @param tagName the name of the element content that is being processed.
	 * @param store true if the data must be stored in the skeleton. This is used to merge later on.
	 * @return a new TextContainer object with the parsed content.
	 */
	private TextContainer processContent (String tagName,
		boolean store)
	{
		try {
			content = new TextContainer();
			int id = 0;
			Stack<Integer> idStack = new Stack<Integer>();
			idStack.push(id);
			int eventType;
			String name;
			String tmp;
			Code code;
			TextFragment segment = null;
			int segIdStack = -1;
			// The current variable points either to content or segment depending on where
			// we are currently storing the parsed data, the segments are part of the content
			// at the end, so all can use the same code/skeleton
			TextFragment current = content;
			
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					current.append(reader.getText());
					if ( store ) { //TODO: escape unsupported chars
						skel.append(Util.escapeToXML(reader.getText(), 0, params.getEscapeGT(), null));
					}
					break;
		
				case XMLStreamConstants.END_ELEMENT:
					name = reader.getLocalName();
					if ( name.equals(tagName) ) {
						return content;
					}
					break;
					
				case XMLStreamConstants.START_ELEMENT:
					if ( store ) storeStartElement();
					name = reader.getLocalName();
					if ( name.equals("ut") ) {
						TagType tagType = TagType.PLACEHOLDER;
						String type = "ph";
						int idToUse = ++id;
						tmp = reader.getAttributeValue("", "Type");
						if ( tmp != null ) {
							if ( tmp.equals("start") ) {
								tagType = TagType.OPENING;
								type = "Xpt";
							}
							else if ( tmp.equals("end") ) {
								tagType = TagType.CLOSING;
								type = "Xpt";
								idToUse = -1; id--;
							}
						}
						appendCode(tagType, idToUse, name, "Xpt", store, current);
					}
					break;
				}
			}
			
			// current should be content at the end
			return content;
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}

	/**
	 * Appends a code, using the content of the node. Do not use for <g>-type tags.
	 * @param tagType The type of in-line code.
	 * @param id the id of the code to add.
	 * @param tagName the tag name of the in-line element to process.
	 * @param type the type of code (bpt and ept must use the same one so they can match!) 
	 * @param store true if we need to store the data in the skeleton.
	 */
	private void appendCode (TagType tagType,
		int id,
		String tagName,
		String type,
		boolean store,
		TextFragment content)
	{
		try {
			int endStack = 1;
			StringBuilder innerCode = new StringBuilder();
			StringBuilder outerCode = null;
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
			
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					if ( store ) storeStartElement();
					StringBuilder tmpg = new StringBuilder();
					if ( reader.getLocalName().equals("sub") ) {
						logger.warning("A <sub> element was detected. It will be included in its parent code as <sub> is currently not supported.");
					}
					else if ( tagName.equals(reader.getLocalName()) ) {
						endStack++; // Take embedded elements into account 
					}
					prefix = reader.getPrefix();
					if (( prefix == null ) || ( prefix.length()==0 )) {
						tmpg.append("<"+reader.getLocalName());
					}
					else {
						tmpg.append("<"+prefix+":"+reader.getLocalName());
					}
					count = reader.getNamespaceCount();
					for ( int i=0; i<count; i++ ) {
						prefix = reader.getNamespacePrefix(i);
						tmpg.append(String.format(" xmlns:%s=\"%s\"",
							((prefix!=null) ? ":"+prefix : ""),
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
					innerCode.append(tmpg.toString());
					outerCode.append(tmpg.toString());
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					if ( store ) storeEndElement();
					if ( tagName.equals(reader.getLocalName()) ) {
						if ( --endStack == 0 ) {
							Code code = content.append(tagType, type, innerCode.toString(), id);
							outerCode.append("</"+tagName+">");
							code.setOuterData(outerCode.toString());
							return;
						}
						// Else: fall thru
					}
					// Else store the close tag in the outer code
					prefix = reader.getPrefix();
					if (( prefix == null ) || ( prefix.length()==0 )) {
						innerCode.append("</"+reader.getLocalName()+">");
						outerCode.append("</"+reader.getLocalName()+">");
					}
					else {
						innerCode.append("</"+prefix+":"+reader.getLocalName()+">");
						outerCode.append("</"+prefix+":"+reader.getLocalName()+">");
					}
					break;

				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					innerCode.append(reader.getText());//TODO: escape unsupported chars
					outerCode.append(Util.escapeToXML(reader.getText(), 0, params.getEscapeGT(), null));
					if ( store ) //TODO: escape unsupported chars
						skel.append(Util.escapeToXML(reader.getText(), 0, params.getEscapeGT(), null));
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}
	
}
