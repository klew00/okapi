/*===========================================================================
  Copyright (C) 2008-2013 by the Okapi Framework contributors
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

package net.sf.okapi.filters.xliff;

import java.io.InputStreamReader;
import java.net.URI;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.FileUtil;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.IdGenerator;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.NSContextManager;
import net.sf.okapi.common.Namespaces;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.filterwriter.ITSContent;
import net.sf.okapi.common.filterwriter.XLIFFWriter;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@UsingParameters(Parameters.class)
public class XLIFFFilter implements IFilter {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String PROP_BUILDNUM = "build-num";
	public static final String PROP_EXTRADATA = "extradata";
	public static final String PROP_WASSEGMENTED = "wassegmented";
	
	private static final String ALTTRANSTYPE_PROPOSAL = "proposal";
	
	private static final int FOR_TU = 0;
	private static final int FOR_TC = 1;
	private static final int FOR_IC = 2;
	
	private boolean hasNext;
	private XMLStreamReader reader;
	private RawDocument input;
	private String docName;
	private int tuId;
	private IdGenerator otherId;
	private IdGenerator groupId;
	private String startDocId;
	private LocaleId srcLang;
	private LocaleId trgLang;
	private LinkedList<Event> queue;
	private boolean canceled;
	private GenericSkeleton skel;
	private ITextUnit tu;
	private int approved; // -1=no property, 0=no, 1=yes
	private Parameters params;
	private boolean sourceDone;
	private boolean targetDone;
	private boolean altTransDone;
	private boolean segSourceDone;
	private URI docURI;
	private String encoding;
	private Stack<String> parentIds;
	private List<String> groupUsedIds;
	private AltTranslationsAnnotation altTrans;
	private int altTransQuality;
	private MatchType altTransMatchType;
	private String altTransOrigin;
	private boolean inAltTrans;
	private boolean processAltTrans;
	private Stack<Boolean> preserveSpaces;
	private String lineBreak;
	private boolean hasUTF8BOM;
	private EncoderManager encoderManager;
	private int autoMid;

	// Variable used to fetch stand-off markup
	private Document standoffDoc;
	private XPath standoffPath;
	private DocumentBuilderFactory xmlFactory;	
	private XPathFactory xpFact;
	private String standoffRef;
	
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
			if ( input != null ) {
				input.close();
				input = null;
			}
			hasNext = false;
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}

	public String getName () {
		return "okf_xliff";
	}

	public String getDisplayName () {
		return "XLIFF Filter";
	}

	public String getMimeType () {
		return MimeTypeMapper.XLIFF_MIME_TYPE;
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MimeTypeMapper.XLIFF_MIME_TYPE,
			getClass().getName(),
			"XLIFF",
			"Configuration for XML Localisation Interchange File Format (XLIFF) documents.",
			null,
			".xlf;.xliff;.sdlxliff;"));
		return list;
	}
	
	public EncoderManager getEncoderManager () {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setMapping(MimeTypeMapper.XLIFF_MIME_TYPE, "net.sf.okapi.common.encoder.XMLEncoder");
		}
		return encoderManager;
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
					Ending ending = new Ending(otherId.createId());
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
			this.input = input;

			XMLInputFactory fact = null;
			if ( params.getUseCustomParser() ) {
				Class<?> factClass = ClassUtil.getClass(params.getFactoryClass());
				fact = (XMLInputFactory) factClass.newInstance();
			}
			else {
				fact = XMLInputFactory.newInstance();
			}

			fact.setProperty(XMLInputFactory.IS_COALESCING, true);
			//Removed for Java 1.6: fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			
			//fact.setXMLResolver(new DefaultXMLResolver());
			//TODO: Resolve the re-construction of the DTD, for now just skip it
			fact.setProperty(XMLInputFactory.SUPPORT_DTD, false);

			// Determine encoding based on BOM, if any
			input.setEncoding("UTF-8"); // Default for XML, other should be auto-detected
			BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
			detector.detectBom();

			String inStreamCharset = "UTF-8";
			if ( detector.isAutodetected() ) {
				inStreamCharset = detector.getEncoding();
			}
			InputStreamReader inStreamReader;
			try {
				inStreamReader = new InputStreamReader(input.getStream(), inStreamCharset);
			}
			catch ( java.io.UnsupportedEncodingException e ) {
				logger.warn("Invalid encoding '{}', using default.", inStreamCharset);
				inStreamReader = new InputStreamReader(input.getStream());
			}
			// When possible, make sure we have a filename associated with the stream
			if ( null != input.getInputURI() ) {
				reader = fact.createXMLStreamReader(input.getInputURI().toString(), inStreamReader);
			}
			else {
				reader = fact.createXMLStreamReader(inStreamReader);
			}

			String realEnc = reader.getCharacterEncodingScheme();
			if ( realEnc != null ) encoding = realEnc;
			else encoding = input.getEncoding();

			srcLang = input.getSourceLocale();
			if ( srcLang == null ) throw new NullPointerException("Source language not set.");
			trgLang = input.getTargetLocale();
			if ( trgLang == null ) throw new NullPointerException("Target language not set.");
			hasUTF8BOM = detector.hasUtf8Bom();
			lineBreak = detector.getNewlineType().toString();
			if ( input.getInputURI() != null ) {
				docName = input.getInputURI().getPath();
			}
			docURI = input.getInputURI();

			preserveSpaces = new Stack<Boolean>();
			preserveSpaces.push(false);
			parentIds = new Stack<String>();
			parentIds.push("p0"); // Base parent
			tuId = 0;
			groupId = new IdGenerator(null, "g");
			otherId = new IdGenerator(null, "d");
			// Set the start event
			hasNext = true;
			queue = new LinkedList<Event>();
			groupUsedIds = new ArrayList<String>();
			standoffRef = ""; // Empty rather than null to allow compare
			
			startDocId = otherId.createId();
			StartDocument startDoc = new StartDocument(startDocId);
			startDoc.setName(docName);
			startDoc.setEncoding(encoding, hasUTF8BOM);
			startDoc.setLocale(srcLang);
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
			throw new OkapiIOException("Cannot open XML document.\n"+e.getMessage(), e);
		}
		catch ( InstantiationException e ) {
			throw new OkapiIOException("Cannot open XML document.\n"+e.getMessage(), e);
		}
		catch ( IllegalAccessException e ) {
			throw new OkapiIOException("Cannot open XML document.\n"+e.getMessage(), e);
		}
	}
	
	@Override
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new XLIFFSkeletonWriter(params);
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter(), getEncoderManager());
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
				else if ( "group".equals(name) ) {
					if ( processStartGroup() ) return true;
				}
				else if ( "bin-unit".equals(name) ) {
					if ( processStartBinUnit() ) return true;
				}
				else storeStartElement(false, false);
				break;
				
			case XMLStreamConstants.END_ELEMENT:
				storeEndElement();
				if ( "file".equals(reader.getLocalName()) ) {
					return processEndFile();
				}
				else if ( "group".equals(reader.getLocalName()) ) {
					return processEndGroup();
				}
				else if ( "bin-unit".equals(reader.getLocalName()) ) {
					return processEndBinUnit();
				}
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

	private boolean processStartFile () {
		// Make a document part with skeleton between the previous event and now.
		// Spaces can go with the file element to reduce the number of events.
		// This allows to have only the file skeleton parts with the sub-document event
		if ( !skel.isEmpty(true) ) {
			DocumentPart dp = new DocumentPart(otherId.createId(), false, skel);
			skel = new GenericSkeleton(); // And create a new skeleton for the next event
			queue.add(new Event(EventType.DOCUMENT_PART, dp));
		}
		
		StartSubDocument startSubDoc = new StartSubDocument(startDocId, otherId.createId());
		storeStartElementFile(startSubDoc);
		
		String tmp = reader.getAttributeValue(null, "original");
		if ( tmp == null ) throw new OkapiIllegalFilterOperationException("Missing attribute 'original'.");
		else startSubDoc.setName(tmp);
		
		// Check the source language
		tmp = reader.getAttributeValue(null, "source-language");
		if ( tmp == null ) throw new OkapiIllegalFilterOperationException("Missing attribute 'source-language'.");
		LocaleId tmpLang = LocaleId.fromString(tmp); 
		if ( !tmpLang.equals(srcLang) ) { // Warn about source language
			logger.warn("The source language declared in <file> is '{}' not '{}'.", tmp, srcLang);
		}
		
		// Check the target language
		Property prop = startSubDoc.getProperty("targetLanguage");
		if ( prop != null ) {
			tmpLang = LocaleId.fromString(prop.getValue());
			if ( params.getOverrideTargetLanguage() ) {
				prop.setValue(trgLang.toBCP47());
			}
			else { // If we do not override the target
				if ( !tmpLang.sameLanguageAs(trgLang) ) { // Warn about target language
					logger.warn("The target language declared in <file> is '{}' not '{}'. '{}' will be used.",
						prop.getValue(), trgLang, prop.getValue());
					trgLang = tmpLang;
				}
			}
		}
		
		// Get datatype property to use for mime-type
		tmp = reader.getAttributeValue(null, "datatype");
		if ( tmp != null ) {
			// make sure this is in-synch with XLIFFWriter
			if ( tmp.equals("x-undefined") ) tmp = null;
			else if ( tmp.equals("html") ) tmp = "text/html";
			else if ( tmp.equals("xml") ) tmp = "text/xml";
			//else if ( tmp.startsWith("x-") ) {
			//	tmp = tmp.substring(2);
			//}
			startSubDoc.setMimeType(tmp);
		}

		// Get build-num as read-only property
		tmp = reader.getAttributeValue(null, PROP_BUILDNUM);
		if ( tmp != null ) {
			startSubDoc.setProperty(new Property(PROP_BUILDNUM, tmp, true));
		}
		
		startSubDoc.setSkeleton(skel);
		queue.add(new Event(EventType.START_SUBDOCUMENT, startSubDoc));
		return true;
	}

	private boolean processEndFile () {
		Ending ending = new Ending(otherId.createId());
		ending.setSkeleton(skel);
		queue.add(new Event(EventType.END_SUBDOCUMENT, ending));
		return true;
	}
	
	private void storeStartElement (boolean updateLangWithTarget,
		boolean addApprovedIfNeeded)
	{
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
			skel.append(" xmlns");
			if (!Util.isEmpty(prefix))
				skel.append(":"+prefix);
			skel.append("=\"");
			skel.append(reader.getNamespaceURI(i));
			skel.append("\"");
		}
		String attrName;
		String attrValue;
		boolean ps = preserveSpaces.peek();
		
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i);
			attrName = (((prefix==null)||(prefix.length()==0)) ? "" : prefix+":")
				+ reader.getAttributeLocalName(i);
			attrValue = reader.getAttributeValue(i);
			
			if ( updateLangWithTarget && attrName.equals("xml:lang") ) {
				attrValue = trgLang.toBCP47(); 
			}

			if ( reader.getAttributeLocalName(i).equals(Property.APPROVED) ) {
				skel.addValuePlaceholder(tu, Property.APPROVED, trgLang);
				addApprovedIfNeeded = false;
			}
			else {
				skel.append(" ");
				skel.append(attrName);
				skel.append("=\"");
				skel.append(Util.escapeToXML(attrValue.replace("\n", lineBreak), 3, params.getEscapeGT(), null));
				skel.append("\"");
			}
			
			if ( attrName.equals("xml:space") ) {
				ps = reader.getAttributeValue(i).equals("preserve");
			}
		}
		
		// Add properties not set but that are writeable
		if ( addApprovedIfNeeded ) {
			skel.addValuePlaceholder(tu, Property.APPROVED, trgLang);
		}
		
		skel.append(">");
		preserveSpaces.push(ps);
	}
	
	private void storeStartElementFile (StartSubDocument startSubDoc) {
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
			skel.append(" xmlns");
			if (!Util.isEmpty(prefix))
				skel.append(":" + prefix);
			skel.append("=\"");
			skel.append(reader.getNamespaceURI(i));
			skel.append("\"");
		}
		String attrName;
		boolean ps = preserveSpaces.peek();
		boolean hasTargetlanguage = false;
		
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i);
			attrName = (((prefix==null)||(prefix.length()==0)) ? "" : prefix+":")
				+ reader.getAttributeLocalName(i);
			
			if ( reader.getAttributeLocalName(i).equals("target-language") ) {
				// Create a property
				hasTargetlanguage = true;
				startSubDoc.setProperty(new Property("targetLanguage", reader.getAttributeValue(i), false));
				skel.append(" ");
				skel.append(attrName);
				skel.append("=\"");
				skel.addValuePlaceholder(startSubDoc, "targetLanguage", LocaleId.EMPTY);
				skel.append("\"");
			}
			else {
				skel.append(" ");
				skel.append(attrName);
				skel.append("=\"");
				skel.append(Util.escapeToXML(reader.getAttributeValue(i).replace("\n", lineBreak), 3, params.getEscapeGT(), null));
				skel.append("\"");
				if ( attrName.equals("xml:space") ) {
					ps = reader.getAttributeValue(i).equals("preserve");
				}
			}
		}
		
		if ( params.getAddTargetLanguage() && !hasTargetlanguage ) {
			// Create the attribute (as a property) if not there yet
			startSubDoc.setProperty(new Property("targetLanguage", trgLang.toBCP47(), false));
			skel.append(" target-language=\"");
			skel.addValuePlaceholder(startSubDoc, "targetLanguage", LocaleId.EMPTY);
			skel.append("\"");
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

	private boolean processTransUnit () {
		try {
			// Make a document part with skeleton between the previous event and now.
			// Spaces can go with trans-unit to reduce the number of events.
			// This allows to have only the trans-unit skeleton parts with the TextUnit event
			if ( !skel.isEmpty(true) ) {
				DocumentPart dp = new DocumentPart(otherId.createId(), false, skel);
				skel = new GenericSkeleton(); // And create a new skeleton for the next event
				queue.add(new Event(EventType.DOCUMENT_PART, dp));
			}
			
			// Process trans-unit
			sourceDone = false;
			targetDone = false;
			altTransDone = false;
			segSourceDone = false;
			altTrans = null;
			processAltTrans = false;
			inAltTrans = false;
			segSourceDone = false;
			tu = new TextUnit(String.valueOf(++tuId));
			storeStartElement(false, true);
			
			String tmp = reader.getAttributeValue(null, "translate");
			if ( tmp != null ) tu.setIsTranslatable(tmp.equals("yes"));

			tmp = reader.getAttributeValue(null, "id");
			if ( tmp == null ) throw new OkapiIllegalFilterOperationException("Missing attribute 'id'.");
			tu.setId(tmp);
			
			tmp = reader.getAttributeValue(null, "resname");
			if ( tmp != null ) tu.setName(tmp);
			else if ( params.getFallbackToID() ) {
				tu.setName(tu.getId());
			}
			
			tmp = reader.getAttributeValue(null, PROP_EXTRADATA);
			if ( tmp != null ) {
				tu.setProperty(new Property(PROP_EXTRADATA, tmp, true));
			}

			approved = -1;
			tmp = reader.getAttributeValue(null, Property.APPROVED);
			if ( tmp != null ) {
				approved = 0;
				if ( tmp.equals("yes") ) {
					approved = 1;
				}
			}

			// Process the text unit-level ITS attributes (attached them as annotations)
			GenericAnnotations.addAnnotations(tu, readITSAttributes(FOR_TU));
			
			// Set restype (can be null)
			tu.setType(reader.getAttributeValue(null, "restype"));
			
			// Get the content
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				String name;
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					name = reader.getLocalName();
					if ( "source".equals(name) ) {
						storeStartElement(false, false);
						processSource(false);
						storeEndElement();
					}
					else if ( "target".equals(name) ) {
						addSegSourceIfNeeded();
						storeStartElement(params.getOverrideTargetLanguage(), false);
						processTarget();
						storeEndElement();
					}
					else if ( "seg-source".equals(name) ) {
						// Store the seg-source skeleton in a isolated part
						skel.add(XLIFFSkeletonWriter.SEGSOURCEMARKER);
						skel.attachParent(tu);
						storeStartElement(false, false);
						processSource(true);
						storeEndElement();
						skel.flushPart(); // Close the part for the seg-source
						segSourceDone = true;
						if ( tu.getSource().hasBeenSegmented() ) {
							tu.setProperty(new Property(PROP_WASSEGMENTED, "true", true));
						}
					}
					else if ( "note".equals(name) ) {
						addTargetIfNeeded();
						storeStartElement(false, false);
						processNote();
						storeEndElement();
					}
					else if ( "alt-trans".equals(name) ) {
						addTargetIfNeeded();
						storeStartElement(false, false);
						processStartAltTrans();
					}
					else {
						addTargetIfNeeded();
						storeStartElement(false, false);
					}
					break;
				
				case XMLStreamConstants.END_ELEMENT:
					name = reader.getLocalName();
					//addTargetIfNeeded();
					if ( "trans-unit".equals(name) ) {
						addTargetIfNeeded();
						storeEndElement();
						if ( altTrans != null ) {
							// make sure the entries are ordered
							altTrans.sort();
						}
						if ( params.getIgnoreInputSegmentation() ) {
							tu.removeAllSegmentations();
						}
						tu.setSkeleton(skel);
						tu.setMimeType(MimeTypeMapper.XLIFF_MIME_TYPE);
						queue.add(new Event(EventType.TEXT_UNIT, tu));
						return true;
					}
					else if ( "alt-trans".equals(name) ) {
						inAltTrans = false;
					}
					// Just store the end
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
			throw new OkapiIOException(e);
		}
		return false;
	}
	
	/**
	 * Reads the ITS local attribute in the current element.
	 * @param type type of processing to do (use: {@link #FOR_TU}, {@link #FOR_TC} or {@link #FOR_IC}).
	 * @return An annotations set with all the read annotations.
	 */
	private GenericAnnotations readITSAttributes (int type) {
		boolean found = false;
		int i;
		for ( i=0; i<reader.getAttributeCount(); i++ ) {
			String ns = reader.getAttributeNamespace(i);
			if (( ns != null ) && ( ns.equals(Namespaces.ITS_NS_URI) || ns.equals(Namespaces.ITSX_NS_URI) )) {
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Last check for XLIFF native attributes that could be mappings
			String mtype = reader.getAttributeValue(null, "mtype");
			if (( mtype == null ) || ( !mtype.equals("term") && !mtype.equals("x-its") )) {
				return null; // No annotations
			}
		}

		// At least one ITS info
		GenericAnnotations anns = new GenericAnnotations();
		
		// Check for LQI
		String val1 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "locQualityIssuesRef");
		if ( val1 != null ) {
			// fetch the standoff markup
			anns.addAll(fetchLocQualityStandoffData(val1, val1));
		}
		else { // Otherwise check for on-element LQI attributes
			val1 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "locQualityIssueComment");
			String val2 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "locQualityIssueType");
			if (( val1 != null ) || ( val2 != null )) {
				if (( type == FOR_TC ) || ( type == FOR_IC )) {
					// OK to create with one null value
					GenericAnnotation ann = new GenericAnnotation(GenericAnnotationType.LQI,
						GenericAnnotationType.LQI_COMMENT, val1,
						GenericAnnotationType.LQI_TYPE, val2);
					if ( (val1 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "locQualityIssueSeverity")) != null ) {
						ann.setDouble(GenericAnnotationType.LQI_SEVERITY, Double.parseDouble(val1));
					}
					if ( (val1 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "locQualityIssueProfileRef")) != null ) {
						ann.setString(GenericAnnotationType.LQI_PROFILEREF, val1);
					}
					if ( (val1 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "locQualityIssueEnabled")) != null ) {
						ann.setBoolean(GenericAnnotationType.LQI_ENABLED, val1.equals("yes"));
					}
					// Add the annotation to the list
					anns.add(ann);
				}
				else {
					logger.warn("ITS Localization Quality Issue data category is to be used only on the main source and target and on mrk.");
				}
			}
		}
		
		// ITS Allowed characters
		if ( (val1 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "allowedCharacters")) != null ) {
			if (( type == FOR_TU ) || ( type == FOR_IC )) {
				anns.add(new GenericAnnotation(GenericAnnotationType.ALLOWEDCHARS,
					GenericAnnotationType.ALLOWEDCHARS_VALUE, val1));
			}
			else {
				logger.warn("ITS Allowed Characters data category is to be used only on trans-unit and mrk.");
			}
		}
	
		// ITS Storage size
		if ( (val1 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "storageSize")) != null ) { // Get encoding info
			if (( type == FOR_TU ) || ( type == FOR_IC )) {
				String enc = reader.getAttributeValue(Namespaces.ITS_NS_URI, "storageEncoding");
				if ( enc == null ) enc = "UTF-8";
				String lb = reader.getAttributeValue(Namespaces.ITS_NS_URI, "lineBreakType");
				if ( lb == null ) lb = "lf";
				anns.add(new GenericAnnotation(GenericAnnotationType.STORAGESIZE,
					GenericAnnotationType.STORAGESIZE_SIZE, Integer.parseInt(val1),
					GenericAnnotationType.STORAGESIZE_ENCODING, enc,
					GenericAnnotationType.STORAGESIZE_LINEBREAK, lb));
			}
			else {
				logger.warn("ITS Storage Size data category is to be used only on trans-unit and mrk.");
			}
		}
		
		// ITS Domain
		if ( (val1 = reader.getAttributeValue(Namespaces.ITSX_NS_URI, "domain")) != null ) {
			if ( type == FOR_TU ) {
				anns.add(new GenericAnnotation(GenericAnnotationType.DOMAIN,
					GenericAnnotationType.DOMAIN_VALUE, val1));
			}
			else {
				logger.warn("ITS Domain data category is to be used only on trans-unit.");
			}
		}

		// External Resource
		if ( (val1 = reader.getAttributeValue(Namespaces.ITSX_NS_URI, "externalResourceRef")) != null ) {
			if (( type == FOR_TU ) || ( type == FOR_IC )) {
				anns.add(new GenericAnnotation(GenericAnnotationType.EXTERNALRES,
					GenericAnnotationType.EXTERNALRES_VALUE, val1));
			}
			else {
				logger.warn("ITS External Resource data category is to be used only on trans-unit and mrk.");
			}
		}
		
		String mtype = reader.getAttributeValue(null, "mtype");
		if (( type == FOR_IC ) && ( mtype != null )) {
			
			// Terminology
			if ( mtype.equals("term") ) {
				String info = reader.getAttributeValue(Namespaces.ITSX_NS_URI, "termInfo");
				String infoRef = reader.getAttributeValue(Namespaces.ITSX_NS_URI, "termInfoRef");
				if (( info != null ) && ( infoRef != null )) {
					logger.error("Cannot have both termInfo and termInfoRef on the same element. termInfo will be used.");
				}
				else if ( infoRef != null ) {
					info = ITSContent.REF_PREFIX+infoRef;
				}
				
				val1 = reader.getAttributeValue(Namespaces.ITSX_NS_URI, "termConfidence");
				Double conf = null;
				if ( val1 != null ) {
					conf = Double.parseDouble(val1);
				}
				anns.add(new GenericAnnotation(GenericAnnotationType.TERM,
					GenericAnnotationType.TERM_INFO, info,
					GenericAnnotationType.TERM_CONFIDENCE, conf));
			}
			
			// Text Analysis
			String taClassRef = reader.getAttributeValue(Namespaces.ITS_NS_URI, "taClassRef");
			if ( taClassRef != null ) {
				taClassRef = ITSContent.REF_PREFIX+taClassRef;
			}
			String taSource = null;
			String taIdent = reader.getAttributeValue(Namespaces.ITS_NS_URI, "taIdentRef");
			if ( taIdent != null ) {
				taIdent = ITSContent.REF_PREFIX+taIdent;
			}
			else {
				taIdent = reader.getAttributeValue(Namespaces.ITS_NS_URI, "taIdent");
				taSource = reader.getAttributeValue(Namespaces.ITS_NS_URI, "taSource");
			}
			if (( taClassRef != null ) || ( taIdent != null )) {
				val1 = reader.getAttributeValue(Namespaces.ITS_NS_URI, "taConfidence");
				Double conf = null;
				if ( val1 != null ) {
					conf = Double.parseDouble(val1);
				}
				anns.add(new GenericAnnotation(GenericAnnotationType.TA,
					GenericAnnotationType.TA_CLASS, taClassRef,
					GenericAnnotationType.TA_SOURCE, taSource,
					GenericAnnotationType.TA_IDENT, taIdent,
					GenericAnnotationType.TA_CONFIDENCE, conf));
			}
		
			// Localization Note
			val1 = reader.getAttributeValue(null, "comment");
			if ( val1 != null ) {
				anns.add(new GenericAnnotation(GenericAnnotationType.LOCNOTE,
						GenericAnnotationType.LOCNOTE_VALUE, val1));
			}
		}
		
		// Just in case there was no annotation, make sure we return null
		return (anns.size() == 0 ? null : anns);
	}
	
	private void processSource (boolean isSegSource) {
		TextContainer tc;
		if ( sourceDone ) { // Case of an alt-trans entry
			// Get the language
			String tmp = reader.getAttributeValue(XMLConstants.XML_NS_URI, "lang");
			LocaleId lang;
			if ( tmp == null ) lang = srcLang; // Use default
			else lang = LocaleId.fromString(tmp);
			// Get the text content
			tc = processContent(isSegSource ? "seg-source" : "source", true);
			// Put the source in the alt-trans annotation
			if ( !preserveSpaces.peek() ) {
				tc.unwrap(true, false);
			}
			// Store in altTrans only when we are within alt-trans
			if ( inAltTrans ) {
				if ( processAltTrans ) {
					if ( isSegSource ) {
						//TODO: handle seg-source
						//TODO: content of seg-source should be the one to use???
						//TODO: what if they are different?
					}
					else {
						// Add the source, no target yet
						AltTranslation alt = altTrans.add(lang, null, null, tc.getFirstContent(), null,
							altTransMatchType, 0, altTransOrigin);
						alt.getEntry().setPreserveWhitespaces(preserveSpaces.peek());
						if ( altTransQuality > 0 ) {
							alt.setCombinedScore(altTransQuality);
						}
					}
				}
			}
			else { // It's seg-source just after a <source> (not in alt-trans)
				TextContainer cont = tc.clone();
				cont.getSegments().joinAll();
				if ( cont.compareTo(tu.getSource(), true) != 0 ) {
					logger.warn("The <seg-source> content for the entry id='{}' is different from its <source>. The un-segmented content of <source> will be used.", tu.getId());
				}
				else { // Same content: use the segmented one
					tc.setHasBeenSegmentedFlag(true); // Force entries without mrk to single segment entries
					tu.setSource(tc);
				}
			}
		}
		else { // Main source of the trans-unit
			// Get the coord attribute if available
			String tmp = reader.getAttributeValue(null, "coord");
			if ( tmp != null ) {
				tu.setSourceProperty(new Property(Property.COORDINATES, tmp, true));
			}
			// Get the ITS annotations for the source
			GenericAnnotations anns = readITSAttributes(FOR_TC);
			
			skel.addContentPlaceholder(tu);
			tc = processContent(isSegSource ? "seg-source" : "source", false);
			if ( !preserveSpaces.peek() ) {
				tc.unwrap(true, false);
			}
			tu.setPreserveWhitespaces(preserveSpaces.peek());
			// Attach the annotation if needed
			GenericAnnotations.addAnnotations(tc, anns);
			
			tu.setSource(tc);
			sourceDone = true;
		}
	}
	
	private void processTarget () {
		TextContainer tc;
		if ( targetDone ) { // Case of an alt-trans entry
			// Get the language
			String tmp = reader.getAttributeValue(XMLConstants.XML_NS_URI, "lang");
			LocaleId lang;
			if ( tmp == null ) lang = trgLang; // Use default
			else lang = LocaleId.fromString(tmp);
			// Get the text content
			tc = processContent("target", true);
			// Put the target in the alt-trans annotation
			if ( !preserveSpaces.peek() ) {
				tc.unwrap(true, false);
			}
			if ( inAltTrans ) {
				if ( processAltTrans ) {
					// Set the target alternate entry
					AltTranslation alt = altTrans.getLast();
					// If we have a target locale already set, it means that entry was used already
					// and we are in an entry without source, so we need to create a new entry
					if (( alt != null ) && ( alt.getTargetLocale() != null )) {
						alt = null; // Behave like it's a first entry
					}
					if ( alt == null ) {
						alt = altTrans.add(srcLang, null, null, null, null,
							altTransMatchType, 0, altTransOrigin);
						alt.getEntry().setPreserveWhitespaces(preserveSpaces.peek());
						if ( altTransQuality > 0 ) {
							alt.setCombinedScore(altTransQuality);
						}
					}
					if ( tc.contentIsOneSegment() ) {
						alt.setTarget(lang, tc.getFirstContent());
					}
					else {
						alt.setTarget(lang, tc.getUnSegmentedContentCopy());
					}
					alt.getEntry().setPreserveWhitespaces(preserveSpaces.peek());
					alt.setFromOriginal(true);
				}
			}
		}
		else {
			// Get the state attribute if available
			//TODO: Need to standardize target-state properties
			String stateValue = reader.getAttributeValue(null, "state");
			// Get the coord attribute if available
			String coordValue = reader.getAttributeValue(null, "coord");

			// Get the ITS annotations for the target
			GenericAnnotations anns = readITSAttributes(FOR_TC);
			
			// Get the target itself
			skel.addContentPlaceholder(tu, trgLang);
			tc = processContent("target", false);
			// Set the target, even if it's an empty one.
			if ( !preserveSpaces.peek() ) {
				tc.unwrap(true, false);
			}
			tu.setPreserveWhitespaces(preserveSpaces.peek());
			// Attach the annotation if needed
			GenericAnnotations.addAnnotations(tc, anns);

			tu.setTarget(trgLang, tc);
			
			// Set the target properties (after the target container has been set)
			if ( stateValue != null ) {
				tu.setTargetProperty(trgLang, new Property("state", stateValue, true)); // Read-only for now
			}
		
			if ( coordValue != null ) {
				tu.setTargetProperty(trgLang, new Property(Property.COORDINATES, coordValue, true)); // Read-only for now
			}

			if ( approved > -1 ) {
				// Note that this property is set to the target at the resource-level
				tu.setTargetProperty(trgLang, new Property(Property.APPROVED, (approved==1 ? "yes" : "no"), false));
			}
			
			targetDone = true;
		}
	}
	
	private void processStartAltTrans () {
		inAltTrans = true;
		processAltTrans = true;
		// Check if this is a proposal-type alt-trans element
		String tmp = reader.getAttributeValue(null, "alttranstype");
		if ( tmp != null ) { // If null, it's a proposal (default)
			if ( !tmp.equals(ALTTRANSTYPE_PROPOSAL) ) {
				// Read only the proposals
				processAltTrans = false;
				return;
			}
		}
		
		// Get possible mid for segment
		String mid = reader.getAttributeValue(null, "mid");
		// Get possible score (it will be set when we create the entry) -1 or 0 means: don't set it
		altTransQuality = -1;
		tmp = reader.getAttributeValue(null, "match-quality");
		if ( !Util.isEmpty(tmp) ) {
			if ( Character.isDigit(tmp.charAt(0)) ) {
				if ( tmp.endsWith("%") ) tmp = tmp.substring(0, tmp.length()-1);
				try {
					altTransQuality = Integer.valueOf(tmp);
					if ( altTransQuality < 1 ) altTransQuality = -1;
				}
				catch ( NumberFormatException e ) {
					// Do nothing
				}
			}
		}
		
		// Get the Okapi match-type if one is present
		altTransMatchType = MatchType.UKNOWN;
		tmp = reader.getAttributeValue(XLIFFWriter.NS_XLIFFOKAPI, XLIFFWriter.OKP_MATCHTYPE);
		if ( !Util.isEmpty(tmp) ) {
			altTransMatchType = MatchType.valueOf(tmp);
		}
		// Adjust UNKNOWN type if we can
		if ( altTransMatchType.equals(MatchType.UKNOWN)) {
			if ( altTransQuality > 99 ) altTransMatchType = MatchType.EXACT;
			else if ( altTransQuality > 0 ) altTransMatchType = MatchType.FUZZY;
		}
		
		// Get the origin if present
		altTransOrigin = AltTranslation.ORIGIN_SOURCEDOC;
		tmp = reader.getAttributeValue(null, "origin");
		if ( !Util.isEmpty(tmp) ) {
			altTransOrigin = tmp;
		}
		
		// Look where the annotation needs to go: segment or container?
		// Get the target (and possibly creates it if needed)
		TextContainer tc = tu.getTarget(trgLang);
		if ( tc == null ) {
			// Create a target from the source if needed
			tc = tu.createTarget(trgLang, false, IResource.COPY_SEGMENTATION); // was COPY_CONTENT before ITextUnit
//			// Make sure it's empty, but that segments are preserved
//			for ( Segment seg : tc.getSegments() ) {
//				seg.text.clear();
//			}
		}
		
		// Decide where to attach the annotation: the segment or the container
		if ( mid == null ) { // Annotation should be attached on the container
			altTrans = tc.getAnnotation(AltTranslationsAnnotation.class);
			if ( altTrans == null ) {
				// If none exists: create one
				altTrans = new AltTranslationsAnnotation();
				tc.setAnnotation(altTrans);
			}
		}
		else { // Annotation should be attached to its corresponding segment 
			Segment seg = tc.getSegments().get(mid);
			if ( seg == null ) {
				// No corresponding segment found. We drop that entry
				logger.warn("An <alt-trans> element for an unknown segment '{}' was detected. It will be ignored.", mid);
				processAltTrans = false;
				return;
			}
			// Else: get possible existing annotation
			altTrans = seg.getAnnotation(AltTranslationsAnnotation.class);
			if ( altTrans == null ) {
				// If none exists: create one
				altTrans = new AltTranslationsAnnotation();
				seg.setAnnotation(altTrans);
			}
		}
	}
	
	private void addSegSourceIfNeeded () {
		// Add skeleton part for the seg-source if it's was not there
		if ( !segSourceDone ) {
			// Add an empty part of the potential seg-source to add
			skel.add(XLIFFSkeletonWriter.SEGSOURCEMARKER);
			skel.attachParent(tu);
			skel.flushPart(); // Close the part for the seg-source
			segSourceDone = true;
		}
	}
	
	private void addAltTransMarker () {
		if ( altTransDone ) return;
		// Add skeleton part for the alt-trans to be added
		// This is in addition to the existing ones
		// Add an empty part of the potential alt-trans to add
		skel.add(XLIFFSkeletonWriter.ALTTRANSMARKER);
		skel.attachParent(tu);
		skel.flushPart(); // Close the part for the seg-source
		altTransDone = true;
	}

	private void addTargetIfNeeded () {
		if ( !sourceDone ) {
			throw new OkapiIllegalFilterOperationException("Element <source> missing or not placed properly.");
		}
		if ( targetDone ) {
			addAltTransMarker();
			return; // Nothing to add
		}

		// Add the seg-source part if needed
		addSegSourceIfNeeded();
		
		// If the target language is the same as the source, we should not create new <target>
		if ( srcLang.equals(trgLang) ) return;
		//Else: this trans-unit has no target, we add it here in the skeleton
		// so we can merge target data in it when writing out the skeleton
		skel.append("<target xml:lang=\"");
		skel.append(trgLang.toString());
		skel.append("\">");
		skel.addContentPlaceholder(tu, trgLang);
		skel.append("</target>");
		skel.append(lineBreak);
		targetDone = true;
		addAltTransMarker();
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
			boolean changeFirstPart = false;
			TextContainer content = new TextContainer();
			ISegments segments = content.getSegments();
			int id = 0;
			autoMid = -1;
			Stack<Integer> idStack = new Stack<Integer>();
			List<Integer> annIds = new ArrayList<Integer>();
			idStack.push(id);
			int eventType;
			String name;
			String tmp;
			Code code;
			Segment segment = null;
			int segIdStack = -1;
			// The current variable points either to content or segment depending on where
			// we are currently storing the parsed data, the segments are part of the content
			// at the end, so all can use the same code/skeleton
			TextFragment current = new TextFragment();
			current.invalidate(); // To handle bracketing open/close cases
			
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
						if ( !current.isEmpty() ) {
							content.append(current, !content.hasBeenSegmented());
						}
						return content;
					}
					if ( name.equals("mrk") ) { // Check of end of segment
						if ( idStack.peek() == segIdStack ) {
							current = new TextFragment(); // Point back to content
							current.invalidate(); // To handle bracketing open/close cases
							idStack.pop(); // Pop only after test is true
							segIdStack = -1; // Reset to not trigger segment ending again
							// Add the segment to the content (no collapsing, except when no segments exist yet. Keep empty segments)
							String oriId = segment.getId();
							segments.append(segment, !content.hasBeenSegmented());
							if ( changeFirstPart && ( content.count()==2 )) {
								// Change the initial part into a non-segment
								changeFirstPart = false;
								content.changePart(0);
								segment.forceId(oriId); // Make sure we use the ID defined in the XLIFF
								// We need to do this because if a non-segment part was before it was seen 9so far)
								// as the first segment and its ID may have been the same as the one of the real
								// first segment
							}
							if ( store ) storeEndElement();
							continue;
						}
					}
					// Other cases
					if ( name.equals("g") || name.equals("mrk") ) {
						if ( store ) storeEndElement();
						// Leave the id set to -1 for balancing
						code = current.append(TagType.CLOSING, name, "");
						// We do know the id since the content must be well-formed
						id = idStack.pop(); code.setId(id);
						tmp = reader.getPrefix();
						if (( tmp != null ) && ( tmp.length()>0 )) {
							code.setOuterData("</"+tmp+":"+name+">");
						}
						else {
							code.setOuterData("</"+name+">");
						}
						if ( name.equals("mrk") ) {
							int n = -1;
							if (( n = annIds.indexOf(id)) != -1 ) {
								annIds.remove(n);
								Code oc = current.getCode(current.getIndex(id));
								GenericAnnotations.addAnnotations(code, oc.getGenericAnnotations());
								code.setType(Code.TYPE_ANNOTATION_ONLY);
							}
						}
					}
					break;
					
				case XMLStreamConstants.START_ELEMENT:
					if ( store ) storeStartElement(false, false);
					name = reader.getLocalName();
					if ( name.equals("mrk") ) { // Check for start of segment
						String type = reader.getAttributeValue(null, "mtype");
						if (( type != null ) && ( type.equals("seg") )) {
							if ( !current.isEmpty() ) { // Append non-segment part
								content.append(current, !content.hasBeenSegmented());
								// If this is have a first part that was not a segment, appending it
								// will make it a segment because a container has always one segment.
								// So we need to fix later when closing this first segment. 
								changeFirstPart = !content.hasBeenSegmented(); //(content.count() == 1); 
							}
							idStack.push(++id);
							segIdStack = id;
							segment = new Segment();
							segment.id = reader.getAttributeValue(null, "mid");
							current = segment.text; // Segment is now being built
							current.invalidate(); // To handle bracketing open/close cases							
							continue;
						}
						else if (( type != null ) && type.equals("protected") ) {
							int mid = retrieveId(id, reader.getAttributeValue(null, "mid"), false, true);
							code = appendCode(TagType.PLACEHOLDER, mid, name, name, store, current);
							code.setDeleteable(false);
							continue;
						}
					}
					// Other cases
					if ( name.equals("g") ) {
						id = retrieveId(id, reader.getAttributeValue(null, "id"), false, false);
						idStack.push(id);
						code = current.append(TagType.OPENING, name, "", id);
						// Get the outer code
						code.setOuterData(buildStartCode());
					}
					else if ( name.equals("mrk") ) {
						int mid = retrieveId(id, reader.getAttributeValue(null, "mid"), false, true);
						idStack.push(mid);
						code = current.append(TagType.OPENING, name, "", mid);
						// Get the annotations
						GenericAnnotations anns = readITSAttributes(FOR_IC);
						if ( anns != null ) {
							annIds.add(mid);
							GenericAnnotations.addAnnotations(code, anns);
							code.setType(Code.TYPE_ANNOTATION_ONLY);
						}
						// Get the outer code
						code.setOuterData(buildStartCode());
					}
					else if ( name.equals("x") ) {
						id = retrieveId(id, reader.getAttributeValue(null, "id"), false, false);
						appendCode(TagType.PLACEHOLDER, id, name, name, store, current);
					}
					else if ( name.equals("bx") ) {
						id = retrieveId(id, reader.getAttributeValue(null, "id"), false, false);
						appendCode(TagType.OPENING, id, name, "Xpt", store, current);
					}
					else if ( name.equals("ex") ) {
						// No support for overlapping codes (use -1 as default)
						id = retrieveId(id, reader.getAttributeValue(null, "id"), true, false);
						appendCode(TagType.CLOSING, id, name, "Xpt", store, current);
					}
					else if ( name.equals("bpt") ) {
						id = retrieveId(id, reader.getAttributeValue(null, "id"), false, false);
						appendCode(TagType.OPENING, id, name, "Xpt", store, current);
					}
					else if ( name.equals("ept") ) {
						// No support for overlapping codes (use -1 as default)
						id = retrieveId(id, reader.getAttributeValue(null, "id"), true, false);
						appendCode(TagType.CLOSING, id, name, "Xpt", store, current);
					}
					else if ( name.equals("ph") ) {
						id = retrieveId(id, reader.getAttributeValue(null, "id"), false, false);
						appendCode(TagType.PLACEHOLDER, id, name, name, store, current);
					}
					else if ( name.equals("it") ) {
						id = retrieveId(id, reader.getAttributeValue(null, "id"), false, false);
						tmp = reader.getAttributeValue(null, "pos");
						TagType tt = TagType.PLACEHOLDER;
						if ( tmp == null ) {
							logger.error("Missing pos attribute for <it> element.");
						}
						else if ( tmp.equals("close") ) {
							tt = TagType.CLOSING;
						}
						else if ( tmp.equals("open") ) {
							tt = TagType.OPENING;
						}
						else {
							logger.error("Invalid value '{}' for pos attribute.", tmp);
						}
						appendCode(tt, id, name, name, store, current);
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

	private String buildStartCode () {
		String prefix = reader.getPrefix();
		StringBuilder tmpg = new StringBuilder();
		if (( prefix != null ) && ( prefix.length()>0 )) {
			tmpg.append("<"+prefix+":"+reader.getLocalName());
		}
		else {
			tmpg.append("<"+reader.getLocalName());
		}
		int count = reader.getNamespaceCount();
		for ( int i=0; i<count; i++ ) {
			prefix = reader.getNamespacePrefix(i);
			tmpg.append(" xmlns");
			if (!Util.isEmpty(prefix))
				tmpg.append(":" + prefix);
			tmpg.append("=\"");
			tmpg.append(reader.getNamespaceURI(i));
			tmpg.append("\"");
		}
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i); 
			tmpg.append(" ");
			if ((prefix!=null) && (prefix.length()!=0))
				tmpg.append(prefix + ":");
			tmpg.append(reader.getAttributeLocalName(i));
			tmpg.append("=\"");
			tmpg.append(Util.escapeToXML(reader.getAttributeValue(i), 3, params.getEscapeGT(), null));
			tmpg.append("\"");
		}
		tmpg.append(">");
		return tmpg.toString();
	}
	
	private int retrieveId (int currentIdValue,
		String id,
		boolean useMinusOneasDefault,
		boolean useAutoMid)
	{
		if (( id == null ) || ( id.length() == 0 )) {
			if ( useAutoMid ) return --autoMid;
			else return (useMinusOneasDefault ? -1 : ++currentIdValue);
		}
		try {
			return Integer.valueOf(id);
		}
		catch ( NumberFormatException e ) {
			// Falls back to the hash-code
			//TODO: At some point code id needs to support a string
			return id.hashCode();
		}
	}
	
	/**
	 * Appends a code, using the content of the node. Do not use for <g>-type tags.
	 * @param tagType The type of in-line code.
	 * @param id the id of the code to add.
	 * @param tagName the tag name of the in-line element to process.
	 * @param type the type of code (bpt and ept must use the same one so they can match!) 
	 * @param store true if we need to store the data in the skeleton.
	 * @param content the object where to put the code.
	 * @return the code that was appended.
	 */
	private Code appendCode (TagType tagType,
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
				outerCode.append(" ");
				if ((prefix!=null) && (prefix.length()!=0))
					outerCode.append(prefix + ":");
				outerCode.append(reader.getAttributeLocalName(i));
				outerCode.append("=\"");
				outerCode.append(Util.escapeToXML(reader.getAttributeValue(i), 3, params.getEscapeGT(), null));
				outerCode.append("\"");
			}
			outerCode.append(">");
			
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					if ( store ) storeStartElement(false, false);
					StringBuilder tmpg = new StringBuilder();
					if ( reader.getLocalName().equals("sub") ) {
						logger.warn("A <sub> element was detected. It will be included in its parent code as <sub> is currently not supported.");
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
						tmpg.append(" xmlns");
						if (!Util.isEmpty(prefix))
							tmpg.append(":" + prefix);
						tmpg.append("=\"");
						tmpg.append(reader.getNamespaceURI(i));
						tmpg.append("\"");
					}
					count = reader.getAttributeCount();
					for ( int i=0; i<count; i++ ) {
						if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
						prefix = reader.getAttributePrefix(i); 
						tmpg.append(" ");
						if ((prefix!=null) && (prefix.length()!=0))
							tmpg.append(prefix + ":");
						tmpg.append(reader.getAttributeLocalName(i));
						tmpg.append("=\"");
						tmpg.append(Util.escapeToXML(reader.getAttributeValue(i), 3, params.getEscapeGT(), null));
						tmpg.append("\"");
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
							if ( innerCode.length() == 0 ) {
								// Replace '>' by '/>'
								outerCode.insert(outerCode.length()-1, '/');
							}
							else outerCode.append("</"+tagName+">");
							code.setOuterData(outerCode.toString());
							return code;
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
		return null; // Not used as the exit is in the loop.
	}
	
	private void processNote () {
		try {
			// Check the destination of the property
			String dest = reader.getAttributeValue(null, "annotates");
			if ( dest == null ) dest = ""; // like 'general'
			Property prop = null;
			StringBuilder tmp = new StringBuilder();
			if ( dest.equals("source") ) {
				prop = tu.getSourceProperty(Property.NOTE);
			}
			else if ( dest.equals("target") ) {
				prop = tu.getTargetProperty(trgLang, Property.NOTE);
			}
			else {
				prop = tu.getProperty(Property.NOTE);
			}
			if ( prop == null ) {
				prop = new Property(Property.NOTE, "", true);
			}
			else {
				tmp.append(prop.getValue());
				tmp.append("\n---\n");
			}

			// Get the content
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE: //TODO: escape unsupported chars
					skel.append(Util.escapeToXML(reader.getText(), 0, params.getEscapeGT(), null));
					tmp.append(reader.getText());
					break;
				case XMLStreamConstants.END_ELEMENT:
					String name = reader.getLocalName();
					if ( name.equals("note") ) {
						prop.setValue(tmp.toString());
						if ( dest.equals("source") ) {
							tu.setSourceProperty(prop);
						}
						else if ( dest.equals("target") ) {
							tu.setTargetProperty(trgLang, prop);
						}
						else {
							tu.setProperty(prop);
						}
						return;
					}
					// Else: This should be an error as note are text only.
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}

	private boolean processStartGroup () {
		storeStartElement(false, false);
		// Check if it's a 'merge-trans' group (v1.2)
		String tmp = reader.getAttributeValue(null, "merge-trans");
		if ( tmp != null ) {
			// If it's a 'merge-trans' group we do not treat it as a normal group.
			// The group element was not generated by the extractor.
			if ( tmp.compareTo("yes") == 0 ) {
				parentIds.push(null); // Was -1 when Id where numbers
				return false;
			}
		}
		
		// Try to get the existing id from the XLIFF group
		String grpId = reader.getAttributeValue(null, "id");
		if (( grpId == null ) || ( groupUsedIds.contains(grpId) )) {
			// If it does not exists, or if it has been used already
			// we create a new id that is not a duplicate
			grpId = groupId.createIdNotInList(groupUsedIds);
		}
		else {
			groupId.setLastId(grpId);
		}
		// Update the list with the new used identifier
		groupUsedIds.add(grpId);
		
		// Else: it's a structural group
		StartGroup group = new StartGroup(parentIds.peek().toString(), grpId);
		group.setSkeleton(skel);
		parentIds.push(groupId.getLastId());
		queue.add(new Event(EventType.START_GROUP, group));

		// Get resname (can be null)
		tmp = reader.getAttributeValue(null, "resname");
		if ( tmp != null ) group.setName(tmp);
		else if ( params.getFallbackToID() ) {
			// Use the true original id that can be null
			group.setName(reader.getAttributeValue(null, "id"));
		}

		// Get restype (can be null)
		group.setType(reader.getAttributeValue(null, "restype"));
		return true;
	}
	
	private boolean processEndGroup () {
		// Pop and checks the value for this group
		String id = parentIds.pop();
		if ( id == null ) {
			// This closes a 'merge-trans' non-structural group
			return false;
		}

		// Else: it's a structural group
		Ending ending = new Ending(id);
		ending.setSkeleton(skel);
		queue.add(new Event(EventType.END_GROUP, ending));
		return true;
	}

	private boolean processStartBinUnit () {
		storeStartElement(false, false);

		String tmp = reader.getAttributeValue(null, "id");
		if ( tmp == null ) {
			throw new OkapiIllegalFilterOperationException("Missing attribute 'id'.");
		}

		StartGroup group = new StartGroup(parentIds.peek().toString(), groupId.createId());
		group.setSkeleton(skel);
		parentIds.push(groupId.getLastId());
		queue.add(new Event(EventType.START_GROUP, group));

		// Get id for resname
		tmp = reader.getAttributeValue(null, "resname");
		if ( tmp != null ) group.setName(tmp);
		else if ( params.getFallbackToID() ) {
			group.setName(reader.getAttributeValue(null, "id"));
		}

		// Get restype (can be null)
		group.setType(reader.getAttributeValue(null, "restype"));
		return true;
	}

	private boolean processEndBinUnit () {
		// Pop and checks the value for this group
		String id = parentIds.pop();
		// Else: it's a structural group
		Ending ending = new Ending(id);
		ending.setSkeleton(skel);
		queue.add(new Event(EventType.END_GROUP, ending));
		return true;
	}

	private GenericAnnotations fetchLocQualityStandoffData (String ref,
		String originalRef)
	{
		if ( Util.isEmpty(ref) ) {
			throw new InvalidParameterException("The reference URI cannot be null or empty.");
		}
		// Identify the type of reference (internal/external)
		// and get the element
		int pn = ref.lastIndexOf('#');
		String id = null;
		String firstPart = null;
		if ( pn > -1 ) {
			id = ref.substring(pn+1);
			firstPart = ref.substring(0, pn);
		}
		else {
			// No ID in the URI
			throw new RuntimeException(String.format("URI to standoff markup does not have an id: '%s'.", ref));
		}

		if ( !Util.isEmpty(firstPart) ) { // The standoff markup is in an external file
			// Try to resolve relative path
			String baseFolder = "";
			if ( docURI != null) baseFolder = FileUtil.getPartBeforeFile(docURI);
			if ( baseFolder.length() > 0 ) {
				if ( baseFolder.endsWith("/") )
					baseFolder = baseFolder.substring(0, baseFolder.length()-1);
				if ( !ref.startsWith("/") ) ref = baseFolder + "/" + ref;
				else ref = baseFolder + ref;
			}
			// Remove the ID
			ref = ref.substring(0, ref.lastIndexOf('#'));
		}
		else { // Standoff markup is in the document being processed
			if ( docURI == null ) {
				//TODO: try to work around this limitation
				throw new RuntimeException("Cannot load external internal standoff markup from non-URI input");
			}
			ref = docURI.getPath();
		}
		
		// Try to avoid re-parsing the standoff document if we can
		if ( !ref.equals(standoffRef) ) {
			// Parse the document
			standoffDoc = parseXMLDocument(ref);
			// Create the XPath object
			standoffPath = xpFact.newXPath();
			NSContextManager nsc = new NSContextManager();
			nsc.add(Namespaces.ITS_NS_PREFIX, Namespaces.ITS_NS_URI);
			nsc.add(Namespaces.ITSX_NS_PREFIX, Namespaces.ITSX_NS_URI);
			standoffPath.setNamespaceContext(nsc);
			standoffRef = ref;
		}
		
		// Create the new annotation set
		GenericAnnotations anns = new GenericAnnotations();
		anns.setData(id);
		
		// Now get the element holding the list of issues
		Element elem1;
		try {
			String tmp = String.format("//%s:locQualityIssues[@xml:id='%s']", Namespaces.ITS_NS_PREFIX, id);
			XPathExpression expr = standoffPath.compile(tmp);
			elem1 = (Element)expr.evaluate(standoffDoc, XPathConstants.NODE);
		}
		catch ( XPathExpressionException e ) {
			throw new RuntimeException("XPath error.", e);
		}
		if ( elem1 == null ) {
			// Entry not found
			logger.warn("Cannot find standoff markup for '{}'", originalRef);
			GenericAnnotation ann = addIssueItem(anns);
			ann.setString(GenericAnnotationType.LQI_ISSUESREF, originalRef); // For information only
			return anns;
		}
		
		// Then get the list of items in the element
		NodeList items = elem1.getElementsByTagNameNS(Namespaces.ITS_NS_URI, "locQualityIssue");
		for ( int i=0; i<items.getLength(); i++ ) {
			// For each entry 
			Element elem2 = (Element)items.item(i);
			// Add the annotation to the set
			GenericAnnotation ann = addIssueItem(anns);
			ann.setString(GenericAnnotationType.LQI_ISSUESREF, originalRef); // For information only
			// Gather the local information (never in HTML since if it's HTML it's inside a script)
			String[] values = retrieveLocQualityIssueData(elem2, false, false);
			if ( values[0] != null ) {
				logger.warn("Cannot have a standoff reference in a standoff element (reference='{}').", ref);
			}
			if ( values[1] != null ) ann.setString(GenericAnnotationType.LQI_TYPE, values[1]);
			if ( values[2] != null ) ann.setString(GenericAnnotationType.LQI_COMMENT, values[2]);
			if ( values[3] != null ) ann.setDouble(GenericAnnotationType.LQI_SEVERITY, Double.parseDouble(values[3]));
			if ( values[4] != null ) ann.setString(GenericAnnotationType.LQI_PROFILEREF, values[4]);
			if ( values[5] != null ) ann.setBoolean(GenericAnnotationType.LQI_ENABLED, values[5].equals("yes"));
		}

		return anns;
	}

	private void ensureDocumentBuilderFactoryExists () {
		if ( xmlFactory == null ) { 
			xmlFactory = DocumentBuilderFactory.newInstance();
			xmlFactory.setNamespaceAware(true);
			xmlFactory.setValidating(false);
		}
		if ( xpFact == null ) {
			xpFact = XPathFactory.newInstance();
		}
	}
	
	private Document parseXMLDocument (String uriString) {
		ensureDocumentBuilderFactoryExists();
		try {
			return xmlFactory.newDocumentBuilder().parse(uriString);
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error parsing an XML document.\n"+e.getMessage(), e);
		}
	}
	
	/**
	 * Adds an issue annotation to a given set and sets its default values.
	 * @param anns the set where to add the annotation.
	 * @return the annotation that has been added.
	 */
	private GenericAnnotation addIssueItem (GenericAnnotations anns) {
		GenericAnnotation ann = anns.add(GenericAnnotationType.LQI);
		ann.setBoolean(GenericAnnotationType.LQI_ENABLED, true); // default
		return ann;
	}

	/**
	 * Retrieves the non-pointer information of the Localization Quality issue data category.
	 * @param elem the element where to get the data.
	 * @param qualified true if the attributes are expected to be qualified.
	 * @return an array of the value: issues reference, type, comment, severity, profile reference, enabled.
	 */
	private String[] retrieveLocQualityIssueData (Element elem,
		boolean qualified,
		boolean useHTML5)
	{
		String[] data = new String[6];
		
		if ( useHTML5 ) {
			if ( elem.hasAttribute("its-loc-quality-issues-ref") )
				data[0] = elem.getAttribute("its-loc-quality-issues-ref");
			if ( elem.hasAttribute("its-loc-quality-issue-type") )
				data[1] = elem.getAttribute("its-loc-quality-issue-type").toLowerCase();
			if ( elem.hasAttribute("its-loc-quality-issue-comment") )
				data[2] = elem.getAttribute("its-loc-quality-issue-comment");
			if ( elem.hasAttribute("its-loc-quality-issue-severity") )
				data[3] = elem.getAttribute("its-loc-quality-issue-severity");
			if ( elem.hasAttribute("its-loc-quality-issue-profile-ref") )
				data[4] = elem.getAttribute("its-loc-quality-issue-profile-ref");
			if ( elem.hasAttribute("its-loc-quality-issue-enabled") )
				data[5] = elem.getAttribute("its-loc-quality-issue-enabled").toLowerCase();
			else
				data[5] = "yes"; // Default
		}
		else if ( qualified ) {
			if ( elem.hasAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssuesRef") )
				data[0] = elem.getAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssuesRef");
			
			if ( elem.hasAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueType") )
				data[1] = elem.getAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueType");
			
			if ( elem.hasAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueComment") )
				data[2] = elem.getAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueComment");
			
			if ( elem.hasAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueSeverity") )
				data[3] = elem.getAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueSeverity");
			
			if ( elem.hasAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueProfileRef") )
				data[4] = elem.getAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueProfileRef");

			if ( elem.hasAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueEnabled") )
				data[5] = elem.getAttributeNS(Namespaces.ITS_NS_URI, "locQualityIssueEnabled");
			else
				data[5] = "yes"; // Default
		}
		else {
			if ( elem.hasAttribute("locQualityIssuesRef") )
				data[0] = elem.getAttribute("locQualityIssuesRef");
			
			if ( elem.hasAttribute("locQualityIssueType") )
				data[1] = elem.getAttribute("locQualityIssueType");
			
			if ( elem.hasAttribute("locQualityIssueComment") )
				data[2] = elem.getAttribute("locQualityIssueComment");
			
			if ( elem.hasAttribute("locQualityIssueSeverity") )
				data[3] = elem.getAttribute("locQualityIssueSeverity");
			
			if ( elem.hasAttribute("locQualityIssueProfileRef") )
				data[4] = elem.getAttribute("locQualityIssueProfileRef");

			if ( elem.hasAttribute("locQualityIssueEnabled") )
				data[5] = elem.getAttribute("locQualityIssueEnabled");
			else
				data[5] = "yes"; // Default
		}

		return data;
	}

}
