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
import java.util.logging.Logger;

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
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class TTXFilter implements IFilter {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private boolean hasNext;
	private XMLStreamReader reader;
	private String docName;
	private int tuId;
	private int otherId;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private String srcLangCode;
	private String trgLangCode;
	private String trgDefFont;
	private LinkedList<Event> queue;
	private boolean canceled;
	private GenericSkeleton skel;
	private TextUnit tu;
	private Parameters params;
	//private boolean sourceDone;
	//private boolean targetDone;
	private String encoding;
	private String lineBreak;
	private boolean hasUTF8BOM;
	private StringBuilder buffer;
	private boolean useDF;
	private boolean insideContent;
	private TTXSkeletonWriter skelWriter;
	
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
			"Configuration for Trados TTX documents."));
		list.add(new FilterConfiguration(getName()+"-noForcedTuv",
			MimeTypeMapper.TTX_MIME_TYPE,
			getClass().getName(),
			"TTX (without forced Tuv in output)",
			"Configuration for Trados TTX documents without forcing Tuv in output.",
			"noForcedTuv.fprm"));
		return list;
	}
	
	public EncoderManager createEncoderManager () {
		EncoderManager em = new EncoderManager();
		em.setMapping(MimeTypeMapper.TTX_MIME_TYPE, "net.sf.okapi.common.encoder.XMLEncoder");
		return em;
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
			
			//fact.setXMLResolver(new DefaultXMLResolver());
			//TODO: Resolve the re-construction of the DTD, for now just skip it
			fact.setProperty(XMLInputFactory.SUPPORT_DTD, false);

			// Determine encoding based on BOM, if any
			input.setEncoding("UTF-8"); // Default for XML, other should be auto-detected
			BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
			detector.detectBom();
			if ( detector.isAutodetected() ) {
				reader = fact.createXMLStreamReader(input.getStream(), detector.getEncoding());
			}
			else {
				reader = fact.createXMLStreamReader(input.getStream());
			}

			String realEnc = reader.getCharacterEncodingScheme();
			if ( realEnc != null ) encoding = realEnc;
			else encoding = input.getEncoding();

			// Set the language codes for the skeleton writer
			if ( skelWriter == null ) {
				skelWriter = new TTXSkeletonWriter(params.getForceSegments());
			}

			srcLoc = input.getSourceLocale();
			if ( srcLoc == null ) throw new NullPointerException("Source language not set.");
			srcLangCode = srcLoc.toString().toUpperCase();
			skelWriter.setSourceLanguageCode(srcLangCode);
			
			trgLoc = input.getTargetLocale();
			if ( trgLoc == null ) throw new NullPointerException("Target language not set.");
			trgLangCode = trgLoc.toString().toUpperCase(); // Default to create new entries
			skelWriter.setTargetLanguageCode(trgLangCode);
			
			hasUTF8BOM = detector.hasUtf8Bom();
			lineBreak = detector.getNewlineType().toString();
			if ( input.getInputURI() != null ) {
				docName = input.getInputURI().getPath();
			}

			insideContent = false;
			tuId = 0;
			otherId = 0;
			// Set the start event
			hasNext = true;
			queue = new LinkedList<Event>();
			buffer = new StringBuilder();
			trgDefFont = null;
			
			useDF = false;
			// By default, for now, use DF for CJK only
			if ( trgLoc.sameLanguageAs("ko")
				|| trgLoc.sameLanguageAs("zh")
				|| trgLoc.sameLanguageAs("ja") ) {
				useDF = true;
			}
			
			StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
			startDoc.setName(docName);
			startDoc.setEncoding(encoding, hasUTF8BOM);
			startDoc.setLocale(srcLoc);
			startDoc.setFilterParameters(getParameters());
			startDoc.setFilterWriter(createFilterWriter());
			startDoc.setType(MimeTypeMapper.TTX_MIME_TYPE);
			startDoc.setMimeType(MimeTypeMapper.TTX_MIME_TYPE);
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
		if ( skelWriter == null ) {
			skelWriter = new TTXSkeletonWriter(params.getForceSegments());
		}
		return skelWriter;
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	private boolean whitespacesOnly (String text) {
		for ( int i=0; i<text.length(); i++ ) {
			if ( !Character.isWhitespace(text.charAt(i)) ) return false;
		}
		return true;
	}
	
	private boolean read () throws XMLStreamException {
		skel = new GenericSkeleton();
		buffer.setLength(0);

		while ( true ) {
			switch ( reader.getEventType() ) {
			case XMLStreamConstants.START_ELEMENT:
				String name = reader.getLocalName();
				if ( "Tu".equals(name) || "ut".equals(name) || "df".equals(name) ) {
					if ( processTextUnit(name) ) return true;
					buildStartElement(true);
					// The element at the exit may be different than at the call
					// so we refresh the name here to store the correct ending
					name = reader.getLocalName(); 
					storeUntilEndElement(name);
					continue; // reader.next() was called
				}
				else if ( "UserSettings".equals(name) ){
					processUserSettings();
				}
				else if ( "Raw".equals(name) ) {
					insideContent = true;
					buildStartElement(true);
				}
				else {
					buildStartElement(true);
				}
				break;
				
			case XMLStreamConstants.END_ELEMENT:
				buildEndElement(true);
				break;
				
			case XMLStreamConstants.SPACE: // Non-significant spaces
				skel.append(reader.getText().replace("\n", lineBreak));
				break;

			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
				if ( insideContent && !whitespacesOnly(reader.getText()) ) {
					if ( processTextUnit(null) ) return true;
					continue; // next() was called
				}
				else {
					skel.append(Util.escapeToXML(reader.getText().replace("\n", lineBreak), 0, true, null));
				}
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
			
			if ( reader.hasNext() ) reader.next();
			else return false;
		}
	}

	/* A text unit starts with either non-whitespace text, internal ut, df, or Tu.
	 * It ends with end of Raw, external ut, or end of df not corresponding to 
	 * a df included in the text unit. Tuv elements are segments.
	 */
	// Returns true if it is a text unit we need to return now
	private boolean processTextUnit (String startTag) {
		try {
			// Send any previous tag as document part
			createDocumentPartIfNeeded();

			// Initialize variable for this text unit
			boolean inTarget = false;
			tu = new TextUnit(null); // No id yet
			TextContainer srcCont = tu.getSource();
			TextFragment srcSegFrag = null;
			TextFragment trgSegFrag = null;
			TextFragment current = srcCont.getContent();
			ArrayList<Segment> trgSegments = new ArrayList<Segment>();
			boolean returnValueAfterTextUnitDone = true;

			String tmp;
			String name;
			boolean moveToNext = false;
			int dfCount = 0;
			boolean done = false;
			
			while ( !done ) {
				// Move to next event if required 
				if ( moveToNext ) reader.next();
				else moveToNext = true;
				
				// Process the event
				switch ( reader.getEventType() ) {
				
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					current.append(reader.getText());
					break;
					
				case XMLStreamConstants.START_ELEMENT:
					name = reader.getLocalName();
					if ( name.equals("ut") ) {
						if ( !isInline(name) ) { // Non-inline ut
							done = true;
							returnValueAfterTextUnitDone = false;
							continue;
						}
					}
					else if ( name.equals("Tu") ) { // New segment
						inTarget = false;
						srcSegFrag = new TextFragment();
						trgSegFrag = new TextFragment();
						current = srcSegFrag;
						trgSegFrag = null;
						//TODO: match info, etc.
						continue;
					}
					else if ( name.equals("Tuv") ) { // New language content
						tmp = reader.getAttributeValue(null, "Lang");
						if ( tmp != null ) {
							inTarget = trgLoc.equals(tmp);
						}
						else { // Just in case we don't have Lang
							inTarget = !inTarget;
						}
						if ( inTarget ) {
							trgSegFrag = new TextFragment();
							current = trgSegFrag;
						}
						// Else: source is already set
						continue;
					}
					else if ( name.equals("df") ) {
						// We have to use placeholder for df because they don't match ut nesting order
						dfCount++;
						Code code = current.append(TagType.PLACEHOLDER, "x-df", "", -1);
						code.setOuterData(buildStartElement(false));
						continue;
					}
					// Inline to include in this segment
					TagType tagType = TagType.PLACEHOLDER;
					String type = "ph";
					int idToUse = -1;
					tmp = reader.getAttributeValue(null, "Type");
					if ( tmp != null ) {
						if ( tmp.equals("start") ) {
							tagType = TagType.OPENING;
							type = "Xpt";
						}
						else if ( tmp.equals("end") ) {
							tagType = TagType.CLOSING;
							type = "Xpt";
							idToUse = -1;
						}
					}
					appendCode(tagType, idToUse, name, type, false, current);
					break;

				case XMLStreamConstants.END_ELEMENT:
					name = reader.getLocalName();
					if ( name.equals("Raw") ) { // End of document
						done = true;
					}
					else if ( name.equals("df") ) {
//						if ( --dfCount < 0 ) { // External DF
//							done = true;
//						}
//						else {
							// We have to use placeholder for df because they don't match ut nesting order
							Code code = current.append(TagType.PLACEHOLDER, "x-df", "", -1); //(inTarget ? ++trgId : ++srcId));
							code.setOuterData(buildEndElement(false));
//						}
						continue;
					}
					// Possible end of segment
					if ( done || name.equals("Tu") ) {
						if ( srcSegFrag != null ) { // Add the segment if we have one
							srcCont.appendSegment(srcSegFrag);
							trgSegments.add(new Segment(String.valueOf(srcCont.getSegmentCount()-1), trgSegFrag));
							srcSegFrag = null;
							trgSegFrag = null;
							current = srcCont.getContent();
							// A Tu stops the current segment, but not the text unit
						}
						continue; // Stop here
					}
					break;
				}
			}

			// Check if this it is worth sending as text unit
			if ( !tu.getSource().hasText(true, false) ) {
				 if ( skelWriter == null ) {
					 skelWriter = new TTXSkeletonWriter(params.getForceSegments());
				 }
				 skelWriter.checkForFilterInternalUse(lineBreak);
				// Not really a text unit: convert to skeleton
				// Use the skeleton writer processFragment() to get the output
				// so any outer data is generated.
				skel.append(skelWriter.processFragment(tu.getSourceContent()));
				tu = null;
				return false; // No return from filter
			}
			
			// Else genuine text unit, finalize and send
			TextContainer cont = tu.getSource().clone();
			if ( cont.isSegmented() ) {
				cont.setSegments(trgSegments);
				tu.setTarget(trgLoc, cont);
			}
			tu.setId(String.valueOf(++tuId));
			skel.addContentPlaceholder(tu); // Used by the TTXFilterWriter
			tu.setSkeleton(skel);
			tu.setPreserveWhitespaces(true);
			tu.setMimeType(MimeTypeMapper.TTX_MIME_TYPE);
			queue.add(new Event(EventType.TEXT_UNIT, tu));
			skel = new GenericSkeleton();
			return returnValueAfterTextUnitDone;
		}
		catch ( IndexOutOfBoundsException e ) {
			throw new OkapiIOException("Out of bounds.", e);
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException("Error processing top-level ut element.", e);
		}
	}
	
	private String buildStartElement (boolean store) {
		StringBuilder tmp = new StringBuilder();
		String prefix = reader.getPrefix();
		if (( prefix == null ) || ( prefix.length()==0 )) {
			tmp.append("<"+reader.getLocalName());
		}
		else {
			tmp.append("<"+prefix+":"+reader.getLocalName());
		}

		int count = reader.getNamespaceCount();
		for ( int i=0; i<count; i++ ) {
			prefix = reader.getNamespacePrefix(i);
			tmp.append(String.format(" xmlns%s=\"%s\"",
				((prefix!=null) ? ":"+prefix : ""),
				reader.getNamespaceURI(i)));
		}
		String attrName;
		
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i);
			attrName = String.format("%s%s",
				(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
				reader.getAttributeLocalName(i));
			tmp.append(String.format(" %s=\"%s\"", attrName,
				Util.escapeToXML(reader.getAttributeValue(i).replace("\n", lineBreak), 3, true, null)));
		}
		tmp.append(">");
		if ( store ) skel.append(tmp.toString());
		return tmp.toString();
	}
	
	private String buildEndElement (boolean store) {
		StringBuilder tmp = new StringBuilder();
		String prefix = reader.getPrefix();
		if (( prefix != null ) && ( prefix.length()>0 )) {
			tmp.append("</"+prefix+":"+reader.getLocalName()+">");
		}
		else {
			tmp.append("</"+reader.getLocalName()+">");
		}
		if ( store ) skel.append(tmp.toString());
		return tmp.toString();
	}

	private void processUserSettings () {
		// Check source language
		String tmp = reader.getAttributeValue(null, "SourceLanguage");
		if ( !Util.isEmpty(tmp) ) {
			 if ( !srcLoc.equals(tmp) ) {
				 logger.warning(String.format("Specified source was '%s' but source language in the file is '%s'.\nUsing '%s'.",
					srcLoc.toString(), tmp, tmp));
				 srcLoc = LocaleId.fromString(tmp);
				 srcLangCode = tmp;
			 }
		}

		// Check target language
		tmp = reader.getAttributeValue(null, "TargetLanguage");
		if ( !Util.isEmpty(tmp) ) {
			 if ( !trgLoc.equals(tmp) ) {
				 logger.warning(String.format("Specified target was '%s' but target language in the file is '%s'.\nUsing '%s'.",
					trgLoc.toString(), tmp, tmp));
				 trgLoc = LocaleId.fromString(tmp);
				 trgLangCode = tmp;
				 // Update skeleton writer value
				 if ( skelWriter == null ) {
					 skelWriter = new TTXSkeletonWriter(params.getForceSegments());
				 }
				 skelWriter.setSourceLanguageCode(srcLangCode);
			 }
		}

		trgDefFont = reader.getAttributeValue(null, "TargetDefaultFont");
		if ( Util.isEmpty(trgDefFont) ) {
			trgDefFont = "Arial"; // Default
		}

		buildStartElement(true);
	}

	// Case of a UT element outside a TUV, that is an un-segmented/translate code.
//	private void processTopSpecialElement (String tagName) {
//		try {
//			boolean isInline = isInline(tagName);
//			if ( isInline ) {
//				// It's internal, and not in a TU/TUV yet
//				processNewTU();
//				// reader.next() has been called already 
//			}
//			else {
//				if ( tagName.equals("ut") ) { // UT that should not be inline
//					// Keep copying into the skeleton until end of element
//					storeStartElement();
//					storeUntilEndElement("ut"); // Includes the closing tag
//				}
//				else { // DF external
//					storeStartElement();
//					reader.next();
//				}
//			}
//		}
//		catch ( XMLStreamException e) {
//			throw new OkapiIOException("Error processing top-level ut element.", e);
//		}
//	}
	
	private void storeUntilEndElement (String name) throws XMLStreamException {
		int eventType;
		while ( reader.hasNext() ) {
			eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				buildStartElement(true);
				break;
			case XMLStreamConstants.END_ELEMENT:
				if ( name.equals(reader.getLocalName()) ) {
					buildEndElement(true);
					reader.next(); // Move forward
					return;
				}
				// Else: just store the end
				buildEndElement(true);
				break;
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.CHARACTERS:
				//TODO: escape unsupported chars
				skel.append(Util.escapeToXML(reader.getText().replace("\n", lineBreak), 0, true, null));
				break;
			case XMLStreamConstants.COMMENT:
				//addTargetIfNeeded();
				skel.append("<!--"+ reader.getText().replace("\n", lineBreak) + "-->");
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				skel.append("<?"+ reader.getPITarget() + " " + reader.getPIData() + "?>");
				break;
			}
		}
	}

	private boolean isInline (String tagName) {
		if ( tagName.equals("df") ) {
			return true;
		}
		String tmp = reader.getAttributeValue(null, "Style");
		if ( tmp != null ) {
			return  !"external".equals(tmp);
		}
		else {
			// If no Style attribute: check for Class as some are indicator of external type.
			tmp = reader.getAttributeValue(null, "Class");
			if ( tmp != null ) {
				return !"procinstr".equals(tmp);
			}
		}
		return true; // Default is internal
	}

	private void createDocumentPartIfNeeded () {
		// Make a document part with skeleton between the previous event and now.
		// Spaces can go with Tu to reduce the number of events.
		// This allows to have only the Tu skeleton parts with the TextUnit event
		if ( !skel.isEmpty(true) ) {
			DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
			queue.add(new Event(EventType.DOCUMENT_PART, dp));
			skel = new GenericSkeleton(); // And create a new skeleton for the next event
		}
	}
	
//	private void processNewTU () {
//		createDocumentPartIfNeeded();
//		skel.append("<Tu PercentageMatch=\"0\">"); // Start segment
//		skel.append(String.format("<Tuv Lang=\"%s\">", srcLangCode));
//
//		tu = new TextUnit(String.valueOf(++tuId));
//		// Get the content. We should stop when we reach
//		// any of the following: </Raw>, <Tu>, or non-inline <ut>,
//		// or </df> of external <df>
////TODO: resolev the DF dilema
//		TextContainer tc = processContent(null, true);
//		tu.setSource(tc);
//		// Note that upon this return we have reader.next() already called
//		// So we need to make sure it is not called again
//
//		skel.append("</Tuv>"); // End source
//		
//		targetDone = false; // Add the target
//		addTargetIfNeeded();
//		skel.append("</Tu>"); // End =segment
//
//		tu.setSkeleton(skel);
//		tu.setPreserveWhitespaces(true);
//		tu.setMimeType(MimeTypeMapper.TTX_MIME_TYPE);
//		queue.add(new Event(EventType.TEXT_UNIT, tu));
//		skel = new GenericSkeleton();
//	}
	
//	private boolean processTU () {
//		try {
//			createDocumentPartIfNeeded();
//			// Process Tu
//			sourceDone = false;
//			targetDone = false;
//			tu = new TextUnit(String.valueOf(++tuId));
//			storeStartElement();
//
//			String tmp = reader.getAttributeValue(null, "PercentageMatch");
//			if ( tmp != null ) {
//				//TODO
//			}
//
//			// Get the content
//			int eventType;
//			while ( reader.hasNext() ) {
//				eventType = reader.next();
//				String name;
//				switch ( eventType ) {
//				case XMLStreamConstants.START_ELEMENT:
//					name = reader.getLocalName();
//					if ( "Tuv".equals(name) ) {
//						storeStartElement();
//						processTUV();
//						storeEndElement();
//					}
//					//TODO: Deal with df
//					break;
//				
//				case XMLStreamConstants.END_ELEMENT:
//					name = reader.getLocalName();
//					if ( "Tu".equals(name) ) {
//						addTargetIfNeeded();
//						storeEndElement();
//						tu.setSkeleton(skel);
//						tu.setPreserveWhitespaces(true);
//						tu.setMimeType(MimeTypeMapper.TTX_MIME_TYPE);
//						queue.add(new Event(EventType.TEXT_UNIT, tu));
//						reader.next(); // Move the cursor forward
//						return true;
//					}
//					// Else: just store the end
//					storeEndElement();
//					break;
//				
//				case XMLStreamConstants.SPACE:
//				case XMLStreamConstants.CDATA:
//				case XMLStreamConstants.CHARACTERS:
//					if ( !targetDone ) {
//						tmp = reader.getText();
//						for ( int i=0; i<tmp.length(); i++ ) {
//							if ( !Character.isWhitespace(tmp.charAt(i)) ) {
//								addTargetIfNeeded();
//								break;
//							}
//						}
//					}
//					//TODO: escape unsupported chars
//					skel.append(Util.escapeToXML(reader.getText().replace("\n", lineBreak), 0, params.getEscapeGT(), null));
//					break;
//					
//				case XMLStreamConstants.COMMENT:
//					//addTargetIfNeeded();
//					skel.append("<!--"+ reader.getText().replace("\n", lineBreak) + "-->");
//					break;
//				
//				case XMLStreamConstants.PROCESSING_INSTRUCTION:
//					//addTargetIfNeeded();
//					skel.append("<?"+ reader.getPITarget() + " " + reader.getPIData() + "?>");
//					break;
//				}
//			}
//		}
//		catch ( XMLStreamException e) {
//			throw new OkapiIOException("Error processing Tu element.", e);
//		}
//		return false;
//	}
	
//	private void processTUV () {
//		TextContainer tc;
//
//		// Detect whether it is source or target 
//		LocaleId locId;
//		boolean isTarget = sourceDone;
//		String tmp = reader.getAttributeValue(null, "Lang");
//		if ( tmp != null ) {
//			locId = LocaleId.fromString(tmp);
//		}
//		else {
//			if ( isTarget ) locId = trgLoc;
//			else locId = srcLoc;
//		}
//		//TODO: check actual languages against expected languages
//		
//		// Add the placeholder for the content
//		if ( isTarget ) {
//			skel.addContentPlaceholder(tu, locId);
//		}
//		else {
//			skel.addContentPlaceholder(tu);
//		}
//
//		// Get the content
//		tc = processContent("Tuv", false);
//		
//		// Finalize the content
//		if ( isTarget ) {
//			tu.setTarget(locId, tc);
//			targetDone = true;
//		}
//		else {
//			tu.setSource(tc);
//			sourceDone = true;
//		}
//	}
	
//	private void addTargetIfNeeded () {
//		if ( targetDone ) return; // Nothing to add
//		// If the target language is the same as the source, we should not create new <target>
//		if ( srcLoc.equals(trgLoc) ) return; 
//		//Else: this trans-unit has no target, we add it here in the skeleton
//		// so we can merge target data in it when writing out the skeleton
//		skel.append(String.format("<Tuv Lang=\"%s\">", trgLangCode));
//		
//		// target placeholder, with optional df around it if requested
//		if ( useDF ) {
//			skel.append(String.format("<df Font=\"%s\">", trgDefFont));
//		}
//		skel.addContentPlaceholder(tu, trgLoc);
//		if ( useDF ) {
//			skel.append("</df>");
//		}
//
//		skel.append("</Tuv>");
//		targetDone = true;
//	}
	
	/**
	 * Processes a segment content.
	 * @param tagName the name of the element content that is being processed or null for processing
	 * a new segment (current event is processed and end conditions are different).
	 * @param store true if the data must be stored in the skeleton. This is used to merge later on.
	 * @return a new TextContainer object with the parsed content.
	 */
//	private TextContainer processContent (String tagName,
//		boolean store)
//	{
//		try {
//			TextContainer cont = new TextContainer();
//			TextFragment current = cont.getContent();
//			int id = 0;
//			Stack<Integer> idStack = new Stack<Integer>();
//			idStack.push(id);
//			String name;
//			String tmp;
//			boolean moveToNext = (tagName != null);
//			int dfCount = 0;
//			
//			while ( reader.hasNext() ) {
//				
//				if ( moveToNext ) {
//					reader.next();
//				}
//				else {
//					moveToNext = true;
//				}
//				
//				switch ( reader.getEventType() ) {
//				
//				case XMLStreamConstants.CHARACTERS:
//				case XMLStreamConstants.CDATA:
//				case XMLStreamConstants.SPACE:
//					current.append(reader.getText());
//					if ( store ) { //TODO: escape unsupported chars
//						skel.append(Util.escapeToXML(reader.getText().replace("\n", lineBreak), 0, params.getEscapeGT(), null));
//					}
//					break;
//		
//				case XMLStreamConstants.END_ELEMENT:
//					name = reader.getLocalName();
//					if ( tagName == null ) { // new segment case
//						if ( name.equals("Raw") ) {
//							// End of document
//							return cont;
//						}
//						if ( name.equals("df") ) {
//							if ( --dfCount < 0 ) {
//								// External DF
//								return cont;
//							}
//						}
//					}
//					else {
//						if ( name.equals(tagName) ) {
//							return cont;
//						}
//						if ( name.equals("df") ) {
//							dfCount--;
//						}
//					}
//					// TODO: df
//					break;
//					
//				case XMLStreamConstants.START_ELEMENT:
//					name = reader.getLocalName();
//					if ( name.equals("ut") ) {
//						if ( tagName == null ) { // New segment
//							if ( !isInline(name) ) {
//								// Stop the new segment here
//								return cont;
//							}
//							// Else: inline to include in this segment
//						}
//						TagType tagType = TagType.PLACEHOLDER;
//						String type = "ph";
//						int idToUse = ++id;
//						tmp = reader.getAttributeValue(null, "Type");
//						if ( tmp != null ) {
//							if ( tmp.equals("start") ) {
//								tagType = TagType.OPENING;
//								type = "Xpt";
//							}
//							else if ( tmp.equals("end") ) {
//								tagType = TagType.CLOSING;
//								type = "Xpt";
//								idToUse = -1; id--;
//							}
//						}
//						if ( store ) storeStartElement();
//						appendCode(tagType, idToUse, name, type, store, current);
//					}
//					else if ( tagName == null ) { // New segment
//						if ( name.equals("Tu") ) {
//							// Another segment starts
//							return cont;
//						}
//					}
//					if ( name.equals("df") ) dfCount++;
//					break;
//				}
//			}
//			
//			// current should be content at the end
//			return cont;
//		}
//		catch ( XMLStreamException e) {
//			throw new OkapiIOException(e);
//		}
//	}

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
					Util.escapeToXML(reader.getAttributeValue(i).replace("\n", lineBreak), 3, true, null)));
			}
			outerCode.append(">");
			
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					if ( store ) buildStartElement(store);
					StringBuilder tmpg = new StringBuilder();
					if ( tagName.equals(reader.getLocalName()) ) {
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
							Util.escapeToXML(reader.getAttributeValue(i).replace("\n", lineBreak), 3, true, null)));
					}
					tmpg.append(">");
					innerCode.append(tmpg.toString());
					outerCode.append(tmpg.toString());
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					if ( store ) buildEndElement(store);
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
					outerCode.append(Util.escapeToXML(reader.getText(), 0, true, null));
					if ( store ) //TODO: escape unsupported chars
						skel.append(Util.escapeToXML(reader.getText(), 0, true, null));
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}
	
}
