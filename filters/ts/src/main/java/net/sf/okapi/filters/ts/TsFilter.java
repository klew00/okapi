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
============================================================================*/

package net.sf.okapi.filters.ts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;


public class TsFilter implements IFilter {

	class TS{
		
		DocumentLocation currentDocumentLocation = DocumentLocation.TS;
		MessageLocation currentMessageLocation = MessageLocation.RESOURCE;
		
		TranslationStatus status = TranslationStatus.UNDETERMINED;
		boolean sourceExists = false;
		boolean targetExists = false;
		String elemBeforeTrg = null;
		boolean sourceIsEmpty = true;
		boolean targetIsEmpty = true;
		String messageId = null;
		
		int obsoletes = 0;
		int approved = 0;
		int unfinished = 0;
		int other = 0;
		
		int resourceId = 0;
		int tuId = 0;
		
		Stack<Integer> contextStack = new Stack<Integer>();
		
		
		public void resetAll(){

			currentDocumentLocation = DocumentLocation.TS;
			currentMessageLocation = MessageLocation.RESOURCE;
			status = TranslationStatus.UNDETERMINED;
			sourceExists = false;
			targetExists = false;
			elemBeforeTrg = null;
			sourceIsEmpty = true;
			targetIsEmpty = true;
			messageId = null;
			
			obsoletes = 0;
			approved = 0;
			unfinished = 0;
			other = 0;
			
			resourceId = 0;
			tuId = 0;
			
			contextStack.clear();
		}
		
		TranslationStatus getTranslationStatus(){
			return status;
		}
		
		boolean isObsolete(){
			if(status == TranslationStatus.OBSOLETE)
				return true;
			else
				return false;
		}
		
		boolean isUnfinished(){
			if(status == TranslationStatus.UNFINISHED)
				return true;
			else
				return false;
		}
		
		boolean missingSourceAndTarget(){
			if ( !ts.sourceExists && !ts.targetExists ){
				return true;
			}else{
				return false;
			}
		}
		
		boolean missingSourceNotTarget(){
			if ( !ts.sourceExists && ts.targetExists){
				return true;
			}else{
				return false;
			}
		}
		
		public void reset(){
			this.status = TranslationStatus.UNDETERMINED;
			sourceExists = false;
			targetExists = false;
			elemBeforeTrg = null;
			sourceIsEmpty = true;
			targetIsEmpty = true;
			messageId = null;
		}
		
		boolean isApproved(){
			if(status == TranslationStatus.APPROVED)
				return true;
			else
				return false;
		}

		public void analyzeMessage() {

			String validBefore = ",source,oldsource,comment,oldcomment,extracomment,translatorcomment,";
			
			for(XMLEvent event: eventList){
				
				if(event.getEventType() == XMLEvent.START_ELEMENT){

					StartElement startElement = event.asStartElement();
					
					if( startElement.getName().getLocalPart().equals("source") ){
						sourceExists = true;						
					}else if( startElement.getName().getLocalPart().equals("translation") ){
						Attribute attr = startElement.getAttributeByName(new QName("type"));
						if(attr == null){
							status = TranslationStatus.APPROVED;	
						}else if (attr.getValue().equals("obsolete")){
							status = TranslationStatus.OBSOLETE;
						}else if (attr.getValue().equals("unfinished")){
							status = TranslationStatus.UNFINISHED;
						}else{
							status = TranslationStatus.OTHER;
						}
						targetExists = true;
					}
				}else if(event.getEventType() == XMLEvent.END_ELEMENT){

					EndElement endElem = event.asEndElement();
					String endElemName = endElem.getName().getLocalPart();

					if( validBefore.contains(","+endElemName+",")){
						elemBeforeTrg = endElemName;
					}
				}
			}
		}

		public boolean msgIsObsolete() {
			if (status == TranslationStatus.OBSOLETE)
				return true;
			else
				return false;
		}
	}
	
	TS ts = new TS(); 
	
	static enum DocumentLocation {TS, CONTEXT, MESSAGE};
	static enum MessageLocation {RESOURCE, SOURCE, TARGET};
	static enum TranslationStatus {UNDETERMINED, UNFINISHED, OBSOLETE, APPROVED, OTHER};

	//boolean decodeByteValues=false;
	
	ArrayList<XMLEvent> eventList = new ArrayList<XMLEvent>();
	GenericSkeleton skel;
	
	XMLEventReader eventReader;


	private String srcLang;
	private String trgLang;
	private Parameters params;
	private boolean hasNext;
	private String docName;
	private boolean canceled;
	private LinkedList<Event> queue;	
	private String lineBreak;
	private String encoding;
	private boolean hasUTF8BOM;
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	public TsFilter(){
		params = new Parameters();
	}
	
	public void cancel() {
		canceled = true;
	}

	public void close() {
		try {
			if ( eventReader != null ) {
				eventReader.close();
				eventReader = null;
				docName = null;
			}
			hasNext = false;
		}
		catch ( XMLStreamException e) {
			throw new OkapiIOException(e);
		}
	}

	public IFilterWriter createFilterWriter() {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public List<FilterConfiguration> getConfigurations() {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MimeTypeMapper.TS_MIME_TYPE,
			getClass().getName(),
			"TS",
			"Configuration for Qt TS files."));
		return list;
	}

	public String getMimeType() {
		return MimeTypeMapper.TS_MIME_TYPE;
	}

	public String getName() {
		return "okf_ts";
	}

	public String getDisplayName () {
		return "TS Filter (ALPHA)";
	}

	public IParameters getParameters() {
		return this.params;
	}

	public boolean hasNext() {
		return hasNext;	
	}

	public Event next() {
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
					Ending ending = new Ending(String.valueOf(++ts.resourceId));
					ending.setSkeleton(skel);
					queue.add(new Event(EventType.END_DOCUMENT, ending));
				}
			}

			// Return the head of the queue
			if ( queue.peek().getEventType() == EventType.END_DOCUMENT ) {
				hasNext = false;
			}
			return queue.poll();	
		}catch ( XMLStreamException e ) {
			throw new OkapiIOException(e);
		}
	}

	public void open(RawDocument input) {
		open(input, true);
	}

	public void open(RawDocument input, boolean generateSkeleton) {
		try {
			close();
			canceled = false;
			
			XMLInputFactory fact = XMLInputFactory.newInstance();
			fact.setProperty(XMLInputFactory.IS_COALESCING, true);
			fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			fact.setProperty(XMLInputFactory.SUPPORT_DTD, false);
					
			// Determine encoding based on BOM, if any
			input.setEncoding("UTF-8"); // Default for XML, other should be auto-detected
			BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
			detector.detectBom();
			//TODO: We need to let the XMLStreamReader detect its encoding, only provide a fall back.
			//We could just not use BOMNewlineEncodingDetector, but then we would not have info on BOM and linebreak type.
			input.setEncoding(detector.getEncoding());
		
			XMLStreamReader reader = fact.createXMLStreamReader(input.getReader());

			String realEnc = reader.getCharacterEncodingScheme();
			if ( realEnc != null ) 
				encoding = realEnc;
			else 
				encoding = input.getEncoding();
			
			eventReader = fact.createXMLEventReader(reader);
			
			srcLang = input.getSourceLanguage();
			if ( srcLang == null ) throw new NullPointerException("Source language not set.");
			trgLang = input.getTargetLanguage();
			if ( trgLang == null ) throw new NullPointerException("Target language not set.");
			hasUTF8BOM = detector.hasUtf8Bom();
			lineBreak = detector.getNewlineType().toString();
			if ( input.getInputURI() != null ) {
				docName = input.getInputURI().getPath();
			}			
			
			
			ts.resetAll();

			// Set the start event
			hasNext = true;
			queue = new LinkedList<Event>();
			
			// Compile code finder rules
			if ( params.useCodeFinder ) {
				params.codeFinder.compile();
			}
			
			StartDocument startDoc = new StartDocument(String.valueOf(++ts.resourceId));
			startDoc.setName(docName);
			startDoc.setEncoding(encoding, hasUTF8BOM);
			startDoc.setLanguage(srcLang);
			startDoc.setFilterParameters(getParameters());
			startDoc.setFilterWriter(createFilterWriter());

			startDoc.setType(MimeTypeMapper.TS_MIME_TYPE);
			startDoc.setMimeType(MimeTypeMapper.TS_MIME_TYPE);
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
	
	
	//--custom methods--
	private boolean read () throws XMLStreamException {
		
		while ( eventReader.hasNext() ) {
			
			XMLEvent event = eventReader.nextEvent();
			
			eventList.add(event);
			
			switch ( event.getEventType() ) {
					
			case XMLStreamConstants.START_ELEMENT:

				StartElement startElem = event.asStartElement();
				String startElemName = startElem.getName().getLocalPart();
				
				if(tsPartReady(startElemName)){
					//TODO validateTs();
					eventList.remove(event);
					IResource resource = generateTsPart(true);
					eventList.clear();
					eventList.add(event);
					queue.add(new Event(EventType.DOCUMENT_PART, resource));
					
				}else if(contextPartReady(startElemName)){
					//TODO validateContext();
					eventList.remove(event);
					IResource resource = generateContextPart(true);
					//printGroup(resource);
					eventList.clear();
					eventList.add(event);
					queue.add(new Event(EventType.START_GROUP, resource));
				}
 
				if(startElemName.equals("context")){
					ts.currentDocumentLocation = DocumentLocation.CONTEXT;
				}else if(startElemName.equals("message")){
					ts.currentDocumentLocation = DocumentLocation.MESSAGE;
				}

				break;
				
			case XMLStreamConstants.END_ELEMENT:

				EndElement endElem = event.asEndElement();
				String endElemName = endElem.getName().getLocalPart();

				if(endElemName.equals("message")){
					//TODO validateMessage();
					ts.analyzeMessage();
					
					if( ts.msgIsObsolete() || ts.missingSourceAndTarget() || ts.missingSourceNotTarget() ){

						StartElement se = getStartElement("message");
						if( ts.missingSourceAndTarget() ){
							logger.warning("Message (Line "+se.getLocation().getLineNumber()+" contains no <translation> and no <source>. Message will be ignored.");
						}else if( ts.missingSourceNotTarget() ){
							logger.warning("Message (Line "+se.getLocation().getLineNumber()+" contains <translation> but no <source>. Message will be ignored.");
						}

						DocumentPart dp = generateObsoleteTu();
						//printDp(dp);
						queue.add(new Event(EventType.DOCUMENT_PART, dp));
						eventList.clear();
						ts.reset();
						
					}else{
						TextUnit tu = generateTu();
						queue.add(new Event(EventType.TEXT_UNIT, tu));
						eventList.clear();
						ts.reset();
						return true;
					}
					

				}else if (endElemName.equals("context")){
					//TODO validateEndContext();
					IResource resource = generateContextPart(false);
					eventList.clear();
					queue.add(new Event(EventType.END_GROUP, resource));
				}

				break;
			
			/*case XMLStreamConstants.START_DOCUMENT:
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.DTD:
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.ENTITY_REFERENCE:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.COMMENT:
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
			case XMLStreamConstants.ENTITY_DECLARATION:
			case XMLStreamConstants.NAMESPACE:
			case XMLStreamConstants.NOTATION_DECLARATION:
			case XMLStreamConstants.ATTRIBUTE:
			case XMLStreamConstants.END_DOCUMENT:*/
			}
		}

		IResource resource = generateTsPart(false);
		queue.add(new Event(EventType.DOCUMENT_PART, resource));
		eventList.clear();
		skel = new GenericSkeleton("");
		return false;
	}	

	private StartElement getStartElement(String string) {

		for(XMLEvent event: eventList){
			if(event.getEventType() == XMLEvent.START_ELEMENT){
				StartElement startElement = event.asStartElement();
				if( startElement.getName().getLocalPart().equals("message") ){
					return startElement;
				}
			}
		}
		return null;
	}

	private boolean tsPartReady(String elemName) {
		if(ts.currentDocumentLocation == DocumentLocation.TS){
			if( elemName.equals("context") || elemName.equals("message") ){
				return true;
			}
		}
		return false;
	}

	
	private boolean contextPartReady(String elemName) {
		if(ts.currentDocumentLocation == DocumentLocation.CONTEXT){
			if( elemName.equals("context") || elemName.equals("message") ){
				return true;
			}
		}
		return false;
	}
	
	
	IResource generateTsPart(boolean start){

		boolean nextIsSkippableEmpty = false;
		DocumentPart resource;
		
		resource = new DocumentPart(String.valueOf(++ts.resourceId),false);
		skel = new GenericSkeleton();
		
		for(XMLEvent event: eventList){
			
			if(nextIsSkippableEmpty){
				nextIsSkippableEmpty = false;
				continue;
			}
			
			if(event.getEventType() == XMLEvent.START_DOCUMENT){
				procStartDoc(event);
			}else if(event.getEventType() == XMLEvent.END_DOCUMENT){
				
			}else if(event.getEventType() == XMLEvent.DTD){
				procDTD(event);
			}else if(event.getEventType() == XMLEvent.COMMENT){
				procComment(event);

			}else if(event.getEventType() == XMLEvent.START_ELEMENT){

				StartElement startElem = event.asStartElement();
				String startElemName = startElem.getName().getLocalPart();
				nextIsSkippableEmpty = nextIsSkippableEmpty(startElem, startElemName);
				
				if( start && startElemName.equals("TS") ){
					procStartElemTS(startElem, resource);
				}else if( startElemName.equals("byte") ){
					procStartElemByte(startElem);	
				}else{
					procStartElemGeneric(startElem, nextIsSkippableEmpty);
				}

			}else if(event.getEventType() == XMLEvent.END_ELEMENT){

				EndElement endElem = event.asEndElement();
				procEndElem(endElem);

			}else if(event.getEventType() == XMLEvent.CHARACTERS){

				Characters chars = event.asCharacters();
				procCharacters(chars);

			}
		}
		resource.setSkeleton(skel);
		return resource;
	}
	
	
	IResource generateContextPart(boolean start){

		boolean nextIsSkippableEmpty = false;
		IResource resource;

		skel = new GenericSkeleton();

		ts.resourceId++;

		if(start){
			
			ts.contextStack.push(ts.resourceId);
			resource = new StartGroup(null,String.valueOf(ts.resourceId));
		}else{
			
			ts.contextStack.pop();
			resource = new Ending(String.valueOf(ts.resourceId));
		}
		
		for(XMLEvent event: eventList){
			
			if(nextIsSkippableEmpty){
				nextIsSkippableEmpty = false;
				continue;
			}
			
			if(event.getEventType() == XMLEvent.DTD){
				procDTD(event);
			}else if(event.getEventType() == XMLEvent.COMMENT){
				procComment(event);
			}else if(event.getEventType() == XMLEvent.START_ELEMENT){

				StartElement startElem = event.asStartElement();
				String startElemName = startElem.getName().getLocalPart();
				nextIsSkippableEmpty = nextIsSkippableEmpty(startElem, startElemName);

				if(start && startElemName.equals("context") ){
					procStartElemContext(startElem, resource);
				}else if( startElemName.equals("byte") ){
					procStartElemByte(startElem);	
				}else{
					procStartElemGeneric(startElem, nextIsSkippableEmpty);
				}

			}else if(event.getEventType() == XMLEvent.END_ELEMENT){

				EndElement endElem = event.asEndElement();
				procEndElem(endElem);

			}else if(event.getEventType() == XMLEvent.CHARACTERS){

				Characters chars = event.asCharacters();
				procCharacters(chars);

			}
		}
		resource.setSkeleton(skel);
		return resource;
	}
	
			
	TextUnit generateTu(){
		
		boolean nextIsSkippableEmpty = false;
		
		skel = new GenericSkeleton();
		TextUnit tu = new TextUnit(String.valueOf(++ts.tuId));
		
		for(XMLEvent event: eventList){
			
			if( nextIsSkippableEmpty ){
				nextIsSkippableEmpty = false;
				continue;
			}
			
			if(event.getEventType() == XMLEvent.START_ELEMENT){

				StartElement startElem = event.asStartElement();
				String startElemName = startElem.getName().getLocalPart();
				
				nextIsSkippableEmpty = nextIsSkippableEmpty(startElem, startElemName);
				
				if( startElemName.equals("message") ){
					procStartElemMessage(startElem, tu);
					
					Attribute msgId = startElem.getAttributeByName(new QName("id"));
					if(msgId != null){
						ts.messageId = msgId.getValue(); 
					}
					
				}else if( startElemName.equals("source") ){
					ts.currentMessageLocation = MessageLocation.SOURCE;
					procStartElemSource(startElem, tu);
				}else if( startElemName.equals("translation") ){
					ts.currentMessageLocation = MessageLocation.TARGET;
					procStartElemTarget(startElem, tu);
				}else if( startElemName.equals("numerusform") ){
					//TODO Handle numerusform
					procStartElemAddToTuContent(startElem, tu);
				}else if( startElemName.equals("lengthvariant") ){
					//TODO Handle lengthvariant
					procStartElemAddToTuContent(startElem, tu);
				}else if( startElemName.equals("byte") ){
					//TODO Handle byte within numberus and lengthvariant
					procStartElemByte(startElem, tu);
				}else{
					procStartElemGeneric(startElem, nextIsSkippableEmpty);
				}
				
			}else if(event.getEventType() == XMLEvent.END_ELEMENT){

				EndElement endElem = event.asEndElement();
				String endElemName = endElem.getName().getLocalPart();
				
				if( endElemName.equals("source") ){
					ts.currentMessageLocation = MessageLocation.RESOURCE;
					if(ts.sourceIsEmpty){
						skel.addContentPlaceholder(tu);	
					}
					procEndElem(endElem);
				}else if( endElemName.equals("translation") ){
					ts.currentMessageLocation = MessageLocation.RESOURCE;
					if(ts.targetIsEmpty){
						skel.addContentPlaceholder(tu, trgLang);	
					}
					procEndElem(endElem);
				}else if( endElemName.equals("numerusform") ){
					//TODO Handle numerusform
					procEndElemAddToTuContent(endElem, tu);
				}else if( endElemName.equals("lengthvariant") ){
					//TODO Handle lengthvariant
					procEndElemAddToTuContent(endElem, tu);
				}else{
					procEndElem(endElem);
				}
				
				if( !ts.targetExists ){
					if (endElemName.equals(ts.elemBeforeTrg)){
						skel.append(lineBreak);
						skel.append("<translation");
						skel.addValuePlaceholder(tu, Property.APPROVED, trgLang);
						tu.setTargetProperty(trgLang, new Property(Property.APPROVED, "no", false));
						tu.setTargetProperty(trgLang, new Property("variants", "no"));
						skel.append(" variants=\"no\">");
						skel.addContentPlaceholder(tu, trgLang);
						skel.append("</translation>");
					}
				}
				
			}else if(event.getEventType() == XMLEvent.CHARACTERS){
				
				Characters chars = event.asCharacters();
				procCharacters(chars, tu);

			}
		}

		if ( params.useCodeFinder )
			params.codeFinder.process(tu.getSourceContent());

		tu.setSkeleton(skel);
		tu.setMimeType(MimeTypeMapper.TS_MIME_TYPE);
		if(ts.messageId != null){
			tu.setName(ts.messageId);
		}
		return tu;
	}

	
	DocumentPart generateObsoleteTu(){
		
		boolean nextIsSkippableEmpty = false;
		
		skel = new GenericSkeleton();
		DocumentPart dp = new DocumentPart(String.valueOf(++ts.resourceId), false);
		
		for(XMLEvent event: eventList){
			
			if(nextIsSkippableEmpty){
				nextIsSkippableEmpty = false;
				continue;
			}
			
			if(event.getEventType() == XMLEvent.START_ELEMENT){

				StartElement startElem = event.asStartElement();
				String startElemName = startElem.getName().getLocalPart();
				
				nextIsSkippableEmpty = nextIsSkippableEmpty(startElem, startElemName);

				if( startElem.getName().getLocalPart().equals("byte") ){
					procStartElemByte(startElem);
				}else{
					procStartElemGeneric(startElem, nextIsSkippableEmpty);
				}
				
			}else if(event.getEventType() == XMLEvent.END_ELEMENT){

				EndElement endElement = event.asEndElement();
				procEndElem(endElement);

			}else if(event.getEventType() == XMLEvent.CHARACTERS){

				Characters chars = event.asCharacters();
				procCharacters(chars);

			}
		}
		dp.setSkeleton(skel);
		return dp;
	}
	

	private void procCharacters(Characters chars, TextUnit tu) {

		if(ts.currentMessageLocation == MessageLocation.RESOURCE){
			
			procCharacters(chars);
		
		}else if(ts.currentMessageLocation == MessageLocation.SOURCE){
			TextContainer tc = tu.getSource();
			if( !tc.hasText() ){
				skel.addContentPlaceholder(tu);	
			}
			tc.append(chars.getData());
			ts.sourceIsEmpty = false;
		}else if(ts.currentMessageLocation == MessageLocation.TARGET){

			TextContainer tc = tu.getTarget(trgLang);
			if( !tc.hasText() ){
				skel.addContentPlaceholder(tu, trgLang);	
			}
			tc.append(chars.getData());
			ts.targetIsEmpty = false;
		}
	}
	
	
	private void procCharacters(Characters chars) {

		String escaped = Util.escapeToXML(chars.getData().replace("\n", lineBreak), 0, true, null); 
		skel.append(escaped);	
	}


	private void procDTD(XMLEvent event) {
		DTD dtd =(DTD)event;
		skel.append(dtd.getDocumentTypeDeclaration().replace("\n", lineBreak));
	}


	private void procComment(XMLEvent event) {
		Comment comment = (Comment)event;
		skel.append("<!--"+comment.getText().replace("\n", lineBreak)+"-->");
		
	}
	
	
	private void procStartDoc(XMLEvent event) {

		javax.xml.stream.events.StartDocument startDoc = (javax.xml.stream.events.StartDocument)event;

		String xmlVersion = startDoc.getVersion();
		if (xmlVersion != null && !xmlVersion.equals("1.0")){
			logger.warning("Filter will use xml version 1.0");	
		}

		String xmlEnc = startDoc.getCharacterEncodingScheme();
		if (xmlEnc != null && !xmlEnc.equalsIgnoreCase(encoding)){
			logger.warning("The xml encoding attribute value "+xmlEnc+" is different from " +
					"what was detected or specified in the settings. Encoding "+encoding+ " will be used.");	
		}
	}


	private void procStartElemTS(StartElement startElement, IResource resource) {
		addStartElemToSkelAddProps(startElement, resource);
	}
	private void procStartElemContext(StartElement startElement, IResource resource) {
		addStartElemToSkelAddProps(startElement, resource);
	}
	private void procStartElemMessage(StartElement startElement, IResource resource) {
		addStartElemToSkelAddProps(startElement, resource);
	}
	private void procStartElemSource(StartElement startElement, TextUnit tu) {
		addStartElemToSkel(startElement);
	}
	@SuppressWarnings("unchecked")
	private void procStartElemTarget(StartElement startElement, TextUnit tu) {

		boolean typeFound = false;
		
		skel.append("<"+startElement.getName().getLocalPart());				

		Iterator<Attribute> attributes = startElement.getAttributes();
		while ( attributes.hasNext() ){
			Attribute attribute = attributes.next();
			if(attribute.getName().getLocalPart().equals("type")){
				
				typeFound = true;
				
				if(attribute.getValue().equals("unfinished")){
					skel.addValuePlaceholder(tu, Property.APPROVED, trgLang);
					tu.setTargetProperty(trgLang, new Property(Property.APPROVED, "no", false));
				}else{
					skel.append(String.format(" %s=\"%s\"", attribute.getName().getLocalPart(), attribute.getValue()));
					Property prop = new Property(attribute.getName().getLocalPart(), attribute.getValue());
					tu.setTargetProperty(trgLang, prop);
				}
			}else{
				skel.append(String.format(" %s=\"%s\"", attribute.getName().getLocalPart(), attribute.getValue()));
				Property prop = new Property(attribute.getName().getLocalPart(), attribute.getValue());
				tu.setTargetProperty(trgLang, prop);
			}
		}
		if(!typeFound){
			skel.addValuePlaceholder(tu, Property.APPROVED, trgLang);
			tu.setTargetProperty(trgLang, new Property(Property.APPROVED, "yes", false));
		}
		skel.append(">");
	}
		
	@SuppressWarnings("unchecked")
	private void addStartElemToSkelAddProps( StartElement startElement, IResource resource ) {

		skel.append("<"+startElement.getName().getLocalPart());				
		Iterator<Attribute> attributes = startElement.getAttributes();

		while ( attributes.hasNext() ){
			Attribute attribute = attributes.next();
			skel.append(String.format(" %s=\"%s\"", attribute.getName().getLocalPart(), attribute.getValue()));
			Property prop = new Property(attribute.getName().getLocalPart(), attribute.getValue());
			if (resource instanceof DocumentPart) {
				((DocumentPart) resource).setProperty(prop);
			}else if (resource instanceof StartGroup) {
				((StartGroup) resource).setProperty(prop);
			}else if (resource instanceof TextUnit) {
				((TextUnit) resource).setProperty(prop);
			}
		}
		skel.append(">");
	}	
	@SuppressWarnings("unchecked")
	private void addStartElemToSkel( StartElement startElement ) {

		skel.append("<"+startElement.getName().getLocalPart());				
		Iterator<Attribute> attributes = startElement.getAttributes();
		while ( attributes.hasNext() ){
			Attribute attribute = attributes.next();
			skel.append(String.format(" %s=\"%s\"", attribute.getName().getLocalPart(), attribute.getValue()));
		}
		skel.append(">");
	}
	
	@SuppressWarnings("unchecked")
	private void procStartElemAddToTuContent(StartElement startElement, TextUnit tu) {

		StringBuilder sb = new StringBuilder();
		
		sb.append("<"+startElement.getName().getLocalPart());	
		Iterator<Attribute> attributes = startElement.getAttributes();
		while ( attributes.hasNext() ){
			Attribute attribute = attributes.next();
			sb.append(String.format(" %s=\"%s\"", attribute.getName().getLocalPart(), attribute.getValue()));
		}
		sb.append(">");
		
		if(ts.currentMessageLocation == MessageLocation.SOURCE){
			TextContainer tc = tu.getSource();
			if( !tc.hasText() ){
				skel.addContentPlaceholder(tu);	
			}
			tc.append(sb.toString());
		}else if(ts.currentMessageLocation == MessageLocation.TARGET){

			TextContainer tc = tu.getTarget(trgLang);
			if( !tc.hasText() ){
				skel.addContentPlaceholder(tu, trgLang);	
			}
			tc.append(sb.toString());
		}
	}
	private void procEndElemAddToTuContent(EndElement endElement, TextUnit tu) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("</"+endElement.getName().getLocalPart()+">");
		
		if(ts.currentMessageLocation == MessageLocation.SOURCE){
			TextContainer tc = tu.getSource();
			if( !tc.hasText() ){
				skel.addContentPlaceholder(tu);	
			}
			tc.append(sb.toString());
		}else if(ts.currentMessageLocation == MessageLocation.TARGET){

			TextContainer tc = tu.getTarget(trgLang);
			if( !tc.hasText() ){
				skel.addContentPlaceholder(tu, trgLang);	
			}
			tc.append(sb.toString());
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void procStartElemGeneric(StartElement startElement, boolean nextIsSkippable) {
		
		skel.append("<"+startElement.getName().getLocalPart());	
		
		Iterator<Attribute> attributes = startElement.getAttributes();
		while ( attributes.hasNext() ){
			Attribute attribute = attributes.next();
			skel.append(String.format(" %s=\"%s\"", attribute.getName().getLocalPart(), attribute.getValue()));
		}
		
		if(nextIsSkippable){
			skel.append("/>");
		}else{
			skel.append(">");
		}
	}
	
	private void procStartElemByte(StartElement startElement, TextUnit tu) {
		
		if(ts.currentMessageLocation == MessageLocation.RESOURCE){
			procStartElemByte(startElement);
		}else if(ts.currentMessageLocation == MessageLocation.SOURCE){
			TextContainer tc = tu.getSource();
			if( !tc.hasText() ){
				skel.addContentPlaceholder(tu);	
			}
			
			if(params.decodeByteValues){
				Attribute attr = startElement.getAttributeByName(new QName("value"));
				tc.append(decodeByteValue(attr.getValue()));
			}else{
				tc.append("<byte value=\""+startElement.getAttributeByName(new QName("value")).getValue()+"\"/>");
				//tc.append(TagType.PLACEHOLDER, "byte", "<byte value=\""+startElement.getName().getLocalPart()+ "\"/>");
				//TODO: should it be text or code?
			}

		}else if(ts.currentMessageLocation == MessageLocation.TARGET){
			TextContainer tc = tu.getTarget(trgLang);
			if( !tc.hasText() ){
				skel.addContentPlaceholder(tu,trgLang);	
			}
			
			if(params.decodeByteValues){
				Attribute attr = startElement.getAttributeByName(new QName("value"));
				tc.append(decodeByteValue(attr.getValue()));
			}else{
				tc.append("<byte value=\""+startElement.getAttributeByName(new QName("value")).getValue()+"\"/>");
				//tc.append(TagType.PLACEHOLDER, "byte", "<byte value=\""+startElement.getName().getLocalPart()+ "\"/>");
				//TODO: should it be text or code?
			}
		}
	}
		
	
	private void procStartElemByte(StartElement startElement) {
		skel.append("<byte value=\""+startElement.getAttributeByName(new QName("value")).getValue()+"\"/>");
	}
	
		
	private void procEndElem(EndElement endElement) {
		skel.append("</"+endElement.getName().getLocalPart()+">");				
	}
	
	
	boolean elementShouldBeEmpty(String name){
		if(name.equals("byte") || name.equals("location")){
			return true;
		}else{
			return false;			
		}
	}
	
	boolean nextIsSkippableEmpty(StartElement curElem, String name){
		if ( elementShouldBeEmpty(name) && nextIsEmpty(curElem) ){
			return true;
		}else{
			return false;
		}
	}
	
	boolean nextIsEmpty(StartElement curElem){
		int index = eventList.indexOf(curElem);
		XMLEvent nextEvent = eventList.get(index+1);  
		if(nextEvent != null){
			if(nextEvent.isEndElement()){
				EndElement nextEndElem = nextEvent.asEndElement();
				if(curElem.getName().getLocalPart().equals(nextEndElem.getName().getLocalPart())){
					return true;
				}
			}
		}
		return false;
	}
			
	private String decodeByteValue(String str){
		try{
			if(str.startsWith("x")){
				str=str.substring(1, str.length());
				int i= Integer.parseInt(str,16);
				char c = (char)i;
				return ""+c;
			}else{
				int i= Integer.parseInt(str,16);
				char c = (char)i;
				return ""+c;
			}
		}catch(NumberFormatException ne){
			throw new OkapiBadFilterInputException("Invalid value ("+str+" ) in byte element. ");
		}
	}
}
