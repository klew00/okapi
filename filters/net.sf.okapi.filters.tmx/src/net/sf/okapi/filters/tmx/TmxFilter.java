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
============================================================================*/

package net.sf.okapi.filters.tmx;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

import org.codehaus.stax2.XMLInputFactory2;

public class TmxFilter implements IFilter {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private boolean hasNext;
	private XMLStreamReader reader;	
	private String docName;
	private int tuId;
	private int otherId; 
	private String srcLang;
	private String trgLang;
	private LinkedList<Event> queue;	
	private boolean canceled;
	private GenericSkeleton skel;	
	private TextUnit tu;
	private String encoding;	
	private Parameters params;
	private Stack<Boolean> preserveSpaces;
	private String lineBreak;
	private boolean hasUTF8BOM;
	
	private enum TuvXmlLang {UNDEFINED,SOURCE,TARGET,OTHER}
	private TuvXmlLang tuvTrgType = TuvXmlLang.UNDEFINED;
	private String currentLang;					//--current language processed in the TU
	private boolean targetExists= false;
	
	private HashMap<String,String> rulesMap = new HashMap<String,String>();
	private Stack<String> elemStack=new Stack<String>();
	
	public TmxFilter () {
		params = new Parameters();
		
		rulesMap.put("<seg>", "<bpt><ept><it><ph><hi>");
		rulesMap.put("<sub>", "<bpt><ept><it><ph><hi>");
		rulesMap.put("<hi>", "<bpt><ept><it><ph><hi>");
		rulesMap.put("<bpt>","<sub>");
		rulesMap.put("<ept>","<sub>");
		rulesMap.put("<it>","<sub>");
		rulesMap.put("<ph>","<sub>");		
	}
	
	public void cancel() {
		canceled = true;
	}

	public void close() {
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

	public String getName() {
		return "okf_tmx";
	}
	
	public String getMimeType () {
		return "text/x-tmx";
	}

	public IParameters getParameters () {
		return params;
	}
	
	public boolean hasNext() {
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
		setOptions(input.getSourceLanguage(), input.getTargetLanguage(),
			input.getEncoding(), generateSkeleton);
		
		if ( input.getInputCharSequence() != null ) {
			open(input.getInputCharSequence());
		}
		else if ( input.getInputURI() != null ) {
			open(input.getInputURI());
		}
		else if ( input.getInputStream() != null ) {
			open(input.getInputStream());
		}
		else {
			throw new OkapiBadFilterInputException("RawDocument has no input defined.");
		}
	}
	
	private void open (InputStream input) {
		commonOpen(0, input);
	}
	
	private void open (CharSequence inputText) {
		docName = null;
		encoding = "UTF-16";
		hasUTF8BOM = false;
		lineBreak = BOMNewlineEncodingDetector.getNewlineType(inputText).toString();
		commonOpen(1, new StringReader(inputText.toString()));
	}

	private void open (URI inputURI) {
		try {
			docName = inputURI.getPath();
			commonOpen(0, inputURI.toURL().openStream());
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
	}	
	
	private void commonOpen (int type,
		Object obj)
	{
		try {
			if ( srcLang == null || srcLang.trim().equals("")) throw new NullPointerException("Source language not set.");
			if ( trgLang == null || trgLang.trim().equals("")) throw new NullPointerException("Target language not set.");
			close();
			canceled = false;			
			
			XMLInputFactory fact = XMLInputFactory.newInstance();
			fact.setProperty(XMLInputFactory.IS_COALESCING, true);
			fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			
			//fact.setXMLResolver(new DefaultXMLResolver());
			//TODO: Resolve the re-construction of the DTD, for now just skip it
			fact.setProperty(XMLInputFactory.SUPPORT_DTD, false);			

			BOMNewlineEncodingDetector detector = null;
			try {
				switch ( type ) {
				case 0: // From InputStream
					detector = new BOMNewlineEncodingDetector((InputStream)obj);
					hasUTF8BOM = detector.hasUtf8Bom();
					lineBreak = detector.getNewlineType().toString();
					reader = fact.createXMLStreamReader((InputStream)obj);
					break;
				case 1: // From Reader
					reader = fact.createXMLStreamReader((Reader)obj);
					break;
				}
			}
			catch ( IOException e ) {
				throw new OkapiIOException(e);
			}
			finally {
				if ( detector != null ) {
					detector = null; // Release it
				}
			}			
			preserveSpaces = new Stack<Boolean>();
			preserveSpaces.push(false);
			tuId = 0;
			otherId = 0;			
			hasNext=true;
			queue = new LinkedList<Event>();
			
			//--attempt encoding detection--
			//if(reader.getEncoding()!=null){
			//	encoding = reader.getEncoding();
			//}
			
			StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
			startDoc.setName(docName);
			String realEnc = reader.getCharacterEncodingScheme();
			if ( realEnc != null ) encoding = realEnc;
			startDoc.setEncoding(encoding, hasUTF8BOM); //TODO: UTF8 BOM detection
			startDoc.setLanguage(srcLang);
			startDoc.setFilterParameters(getParameters());
			startDoc.setType("text/x-tmx");
			startDoc.setMimeType("text/x-tmx");
			startDoc.setMultilingual(true);
			startDoc.setLineBreak(lineBreak);			
			queue.add(new Event(EventType.START_DOCUMENT, startDoc));			
			
			// The XML declaration is not reported by the parser, so we need to
			// create it as a document part when starting			
			skel = new GenericSkeleton();
			startDoc.setProperty(new Property(Property.ENCODING, encoding, false));
			skel.append("<?xml version=\"1.0\" encoding=\"");
			skel.addValuePlaceholder(startDoc, Property.ENCODING, "");
			skel.append("\"?>");
			startDoc.setSkeleton(skel);
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}

	public void setParameters(IParameters params) {
		this.params = (Parameters)params;
	}

	private void setOptions(String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLanguage;
		trgLang = targetLanguage;
		
		// Default encoding should be UTF-8, other must be declared
		// And that will be auto-detected and encoding will be updated
		encoding = "UTF-8";
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
		
		while ( reader.hasNext() ) {
			eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				if (reader.getLocalName().equals("tu")){
					return processTranslationUnit();
				}else{
					storeStartElement();
					if (!params.consolidateDpSkeleton) {
						DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
						skel = new GenericSkeleton();
						queue.add(new Event(EventType.DOCUMENT_PART, dp));
					}
					break;
				}
			
			case XMLStreamConstants.END_ELEMENT:
				storeEndElement();
				if (!params.consolidateDpSkeleton) {
					DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
					skel = new GenericSkeleton();
					queue.add(new Event(EventType.DOCUMENT_PART, dp));
				}
				break;				
			
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.CDATA:
				skel.append(reader.getText().replace("\n", lineBreak));
				break;				
			case XMLStreamConstants.CHARACTERS: //TODO: Check if it's ok to not check for unsupported chars
				skel.append(Util.escapeToXML(reader.getText(), 0, params.escapeGT, null));
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
				break;
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
	
	private void storeTuStartElement () {
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
			
			//--set the properties depending on the tuvTrgType--
			if(tuvTrgType == TuvXmlLang.UNDEFINED){
				tu.setProperty(new Property(reader.getAttributeLocalName(i),reader.getAttributeValue(i), true));				
			}else if(tuvTrgType == TuvXmlLang.SOURCE){
				tu.setSourceProperty(new Property(reader.getAttributeLocalName(i), reader.getAttributeValue(i), true));
			}else if(tuvTrgType == TuvXmlLang.TARGET || params.processAllTargets){
				tu.setTargetProperty(currentLang, new Property(reader.getAttributeLocalName(i),reader.getAttributeValue(i), true));
			}			
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
	

	private boolean processTuDocumentPart(){
		storeTuStartElement();		
		String startElement = reader.getLocalName();
		try {
			while(reader.hasNext()){
				int eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
					 //TODO: Check if it's ok to not check for unsupported chars
					skel.append(Util.escapeToXML(reader.getText(), 0, params.escapeGT, null));
					//--set the properties depending on the tuvTrgType--
					if(tuvTrgType == TuvXmlLang.UNDEFINED){
						tu.setProperty(new Property(startElement, reader.getText(), true));
					}else if(tuvTrgType == TuvXmlLang.SOURCE){
						tu.setSourceProperty(new Property(startElement, reader.getText(), true));	
					}else if(tuvTrgType == TuvXmlLang.TARGET || params.processAllTargets){
						tu.setTargetProperty(currentLang, new Property(startElement, reader.getText(), true));
					}else if(tuvTrgType == TuvXmlLang.OTHER){
						tu.setProperty(new Property(startElement, reader.getText(), true));
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if(reader.getLocalName().equals(startElement)){
						storeEndElement();
						return true;
					}
					break;
				}
			}
			return false;
		} catch (XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}	
	
	/**
	 * Process a segment <seg>*</seg>, appending the skeleton to skel and adding the properties to nameable and reference to tu 
	 */			
	private boolean processSeg(TextUnit pTu, GenericSkeleton pSkel){
		
		int id = 0;
		//Stack<Integer> idStack = new Stack<Integer>();
		//idStack.push(id);		
		int eventType;
		String localName;
		
		//--determine which container to use--
		TextContainer tc;
		if(tuvTrgType == TuvXmlLang.SOURCE){
			tc = pTu.getSource();
		}else if(tuvTrgType == TuvXmlLang.TARGET || params.processAllTargets){
			tc = pTu.setTarget(currentLang, new TextContainer());
		}else{
			tc=null;
		}
		
		storeTuStartElement();							//store the <seg> element with it's properties
		String startElement = reader.getLocalName();	//need this variable to locate the ending </seg> element
		try {
			while(reader.hasNext()){					//loop through the <seg> content
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					if(tuvTrgType == TuvXmlLang.SOURCE || tuvTrgType == TuvXmlLang.TARGET || params.processAllTargets){
						//TODO: Check if it's ok to not check for unsupported chars
						tc.append(reader.getText());	//add to source or target container
					}else{ 			
						//TODO: Check if it's ok to not check for unsupported chars
						pSkel.append(Util.escapeToXML(reader.getText(), 0, params.escapeGT, null));	//add to skeleton
					}
					break;
					
				case XMLStreamConstants.START_ELEMENT:		

					localName = reader.getLocalName().toLowerCase();
					if(!isValidElement(elemStack.peek(),localName,true)){
						//--throws OkapiBadFilterInputException if not valid--
					}
			
					elemStack.push(localName);
					
					if(tuvTrgType == TuvXmlLang.SOURCE || tuvTrgType == TuvXmlLang.TARGET || params.processAllTargets){
						if(localName.equals("hi")){
							String typeAttr = getTypeAttribute();
							tc.append(TagType.OPENING, ((typeAttr!=null) ? typeAttr : "hi"),"<hi>");	
						}else if(localName.equals("ph") || localName.equals("it")){
							appendCode(TagType.PLACEHOLDER, ++id, localName, tc);
						}else if(localName.equals("bpt")){
							appendCode(TagType.OPENING, ++id, localName, tc);
						}else if(localName.equals("ept")){
							appendCode(TagType.CLOSING, ++id, localName, tc);
						}
						break;
					}else{
						//TODO: handle any remaining inline tags
						storeStartElement();
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					
					localName = reader.getLocalName().toLowerCase();
					elemStack.pop();					
					
					if(reader.getLocalName().equals(startElement)){
						if(tuvTrgType == TuvXmlLang.SOURCE){
							pSkel.addContentPlaceholder(pTu, null);
						}else if(tuvTrgType == TuvXmlLang.TARGET || params.processAllTargets){
							pSkel.addContentPlaceholder(pTu, currentLang);
						}
						storeEndElement();
						return true;
					}else{
						if(tuvTrgType == TuvXmlLang.SOURCE || tuvTrgType == TuvXmlLang.TARGET || params.processAllTargets){
							if(localName.equals("hi")){
								tc.append(TagType.CLOSING, "hi","</hi>");	
							}						
						}else{
							storeEndElement();
							break;
						}
					}
					
				}
			}
			return false;
		} catch (XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}
	
	/**
	 * Process an entire tu element
	 * @return FilterEvent
	 */		
	private boolean processTranslationUnit(){
		
		int countTuvs=0;
		
		// Make a document part with skeleton between the previous event and now.
		// Spaces can go with trans-unit to reduce the number of events.
		// This allows to have only the trans-unit skeleton parts with the TextUnit event
		if ( !skel.isEmpty(true) ) {
			DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
			skel = new GenericSkeleton(); // And create a new skeleton for the next event
			queue.add(new Event(EventType.DOCUMENT_PART, dp));
		}
		
		tu = new TextUnit(String.valueOf(++tuId));
		
		
		storeTuStartElement();

		String localName;
		try {
			while(reader.hasNext()){
				
				int eventType = reader.next();
				switch ( eventType ) {

				case XMLStreamConstants.COMMENT:
					skel.append("<!--"+ reader.getText().replace("\n", lineBreak) + "-->");
					break;
					
				case XMLStreamConstants.CHARACTERS: //TODO: Check if it's ok to not check for unsupported chars
					skel.append(Util.escapeToXML(reader.getText(), 0, params.escapeGT, null));
					break;
				case XMLStreamConstants.START_ELEMENT:

					localName = reader.getLocalName().toLowerCase(); 
					if(localName.equals("note") || localName.equals("prop")){
						//Todo: handle true/false
						processTuDocumentPart();
					}else if(reader.getLocalName().equals("tuv")){
						
						currentLang = getXmlLangFromCurTuv();

						if(currentLang.toLowerCase().equals(trgLang.toLowerCase())){
							targetExists=true;
						}
						
						tuvTrgType = getTuvTrgType(currentLang);
						//currentLang=reader.getAttributeValue(0).toLowerCase();
						storeTuStartElement();
						
					}else if(reader.getLocalName().equals("seg")){
						elemStack.push("seg");
						processSeg(tu, skel);
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:

					localName = reader.getLocalName().toLowerCase(); 

					if(localName.equals("tu")){
						
						//--TMX RULE: Make sure each <tu> contains at least one <tuv>--
						if(countTuvs<1){
							throw new OkapiBadFilterInputException("Each <tu> requires at least one <tuv>");							
						}
						
						//--add the resname based on tuid--
						if(tu.getProperty("tuid")!=null){
							tu.setName(tu.getProperty("tuid").getValue());
						}

						//--create new skeleton and close source if target does not exist--
						if(!targetExists){
							skel.append("<tuv xml:lang=\""+trgLang+"\"><seg>");
							skel.addContentPlaceholder(tu, trgLang);
							skel.append("</seg></tuv>"+lineBreak);
						}
						
						storeEndElement();
						tu.setSkeleton(skel);
						tu.setMimeType("text/xml");
						
						
						queue.add(new Event(EventType.TEXT_UNIT, tu));
						tuvTrgType = TuvXmlLang.UNDEFINED;
						targetExists=false;
						return true;
					}else if(localName.equals("tuv")){
						countTuvs++;
						storeEndElement();
						//TODO: Add finalizing the tuv
					}else{
						//--TMX RULE: Entering here would mean content other than <note>, <prop>, or <tuv> inside the <tu> which is invalid.
						throw new OkapiBadFilterInputException("Only <note>, <prop>, and <tuv> elements are allowed inside <tu>");
					} 	
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new OkapiIOException(e);
		}
		return false;		
	}
	
	
	/**
	 * Appends a code, using the content of the node. Do not use for <g>-type tags.
	 * @param type The type of in-line code.
	 * @param id The id of the code to add.
	 * @param tagName The tag name of the in-line element to process.
	 * @param content The object where to put the code.
	 * Do not save if this parameter is null.
	 */
	private void appendCode (TagType tagType,
		int id,
		String tagName,
		TextContainer content)
	{
		
		String localName;
		
		try {
			//--BEGIN SUBFLOW--
			//int subLevelCounter = 0;
			//--END SUBFLOW--
			
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
					
					localName = reader.getLocalName().toLowerCase();
					if(!isValidElement(elemStack.peek(),localName, true)){
						//--throws OkapiBadFilterInputException if not valid--
					}
					
					elemStack.push(localName);

					//--warn about subflow--
					if("sub".equals(reader.getLocalName())){
						logger.warning("A <sub> element was detected. It will be included in its parent code as <sub> is currently not supported.");
					}
					
					prefix = reader.getPrefix();
					StringBuilder tmpg = new StringBuilder();
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
					
					elemStack.pop();
					
					//--completed the original placeholder/code and back up to the <seg> level--
					if ( tagName.equals(reader.getLocalName()) && (elemStack.peek().equals("seg"))) {

						Code code = content.append(tagType, tagName, innerCode.toString());
						outerCode.append("</"+tagName+">");
						code.setOuterData(outerCode.toString());
						return;							
					}else{
						
						String ns = reader.getPrefix();
						if (( ns == null ) || ( ns.length()==0 )) {
							innerCode.append("</"+reader.getLocalName()+">");
							outerCode.append("</"+reader.getLocalName()+">");
						}
						else {
							innerCode.append("</"+ns+":"+reader.getLocalName()+">");
							outerCode.append("</"+ns+":"+reader.getLocalName()+">");
						}						
					}
					break;

				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:

					innerCode.append(reader.getText());//TODO: escape unsupported chars
					outerCode.append(Util.escapeToXML(reader.getText(), 0, params.escapeGT, null));
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}	
	

	/**
	 * Gets the TuvXmlLang based on current language and source and specified target lang
	 * @return 	TuvXmlLang.SOURCE, TuvXmlLang.TARGET, and TuvXmlLang.OTHER
	 */		
	private TuvXmlLang getTuvTrgType(String pLang){

		if (pLang.toLowerCase().equals(srcLang.toLowerCase())){
			return TuvXmlLang.SOURCE; 
		}else if (pLang.toLowerCase().equals(trgLang.toLowerCase())){
			return TuvXmlLang.TARGET;
		}else{ 
			return TuvXmlLang.OTHER;
		}
	}
	
	
	/**
	 * Gets the xml:lang attribute from the current <tuv> element
	 * @return 	returns the language or throws exception if xml:lang is missing
	 * @throws 	OkapiBadFilterInputException If xml:Lang is missing
	 */		
	private String getXmlLangFromCurTuv(){

		String prefix;
		int count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			prefix = reader.getAttributePrefix(i);
			if(prefix.equals("xml")){
				if(reader.getAttributeLocalName(i).equals("lang")){
					return reader.getAttributeValue(i);
				}
			}
		}
		throw new OkapiBadFilterInputException("The required xml:lang attribute is missing in <tuv>. The file is not valid TMX.");
	}

	
	/**
	 * Gets the type attribute from the current element
	 * @return 	returns the type or null if it's missing
	 */		
	private String getTypeAttribute(){

		int count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if(reader.getAttributeLocalName(i).equals("lang")){
				return reader.getAttributeValue(i);
			}
		}
		return null;
	}
	
	
	private boolean isValidElement(String curElem, String newElem, boolean throwException){
		String rules = rulesMap.get("<"+curElem+">");

		if(rules!=null && rules.contains("<"+newElem+">")){
			return true;
		}else{
			if(throwException){
				throw new OkapiBadFilterInputException("<"+newElem+"> not allowed in <"+curElem+">. Only "+rules+" allowed.");
			}else{
				logger.warning("<"+newElem+"> not allowed in <"+curElem+">. Only "+rules+" allowed.");
				return false;		
			}
		}
	}
	
}
