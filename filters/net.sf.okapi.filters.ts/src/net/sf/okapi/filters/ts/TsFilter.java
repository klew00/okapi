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

package net.sf.okapi.filters.ts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
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
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;


public class TsFilter implements IFilter {

	private Parameters params;
	private boolean hasNext;
	private XMLStreamReader reader;	
	private String docName;
	private boolean canceled;
	private LinkedList<Event> queue;	
	private int otherId; 
	private GenericSkeleton skel;	
	private String lineBreak;
	private String srcLang;
	private String trgLang;
	private String encoding;
	private boolean hasUTF8BOM;
	private int tuId;
	private int groupId;
	private Stack<Integer> parentIds;
	
	
	boolean procCtxGrp=false;
	boolean procCtxGrpName=false;
	String procCtxGrpNameValue=null;
	
	TsMessage tMsg = null;
	
	public TsFilter(){
		params = new Parameters();
	}
	
	public void cancel() {
		canceled = true;
	}

	public void close() {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
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
			"text/x-ts",
			getClass().getName(),
			"TS",
			"Configuration for Qt Ts files."));
		return list;
	}

	public String getMimeType() {
		return "text/x-ts";	
	}

	public String getName() {
		return "okf_ts";
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
			
			//fact.setXMLResolver(new DefaultXMLResolver());
			//TODO: Resolve the re-construction of the DTD, for now just skip it
			fact.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			
			// Determine encoding based on BOM, if any
			input.setEncoding("UTF-8"); // Default for XML, other should be auto-detected
			BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
			detector.detectBom();
			input.setEncoding(detector.getEncoding());
			reader = fact.createXMLStreamReader(input.getReader());
						
			encoding = input.getEncoding();
			srcLang = input.getSourceLanguage();
			if ( srcLang == null ) throw new NullPointerException("Source language not set.");
			trgLang = input.getTargetLanguage();
			if ( trgLang == null ) throw new NullPointerException("Target language not set.");
			hasUTF8BOM = detector.hasUtf8Bom();
			lineBreak = detector.getNewlineType().toString();
			if ( input.getInputURI() != null ) {
				docName = input.getInputURI().getPath();
			}

			//preserveSpaces = new Stack<Boolean>();
			//preserveSpaces.push(false);
			parentIds = new Stack<Integer>();
			parentIds.push(0);
			groupId = 0;
			tuId = 0;
			otherId = 0;
			// Set the start event
			hasNext = true;
			queue = new LinkedList<Event>();
			
			StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
			startDoc.setName(docName);
			String realEnc = reader.getCharacterEncodingScheme();
			if ( realEnc != null ) encoding = realEnc;
			startDoc.setEncoding(encoding, hasUTF8BOM);
			startDoc.setLanguage(srcLang);
			startDoc.setFilterParameters(getParameters());
			startDoc.setFilterWriter(createFilterWriter());
			startDoc.setType("text/x-ts");
			startDoc.setMimeType("text/x-ts");
			startDoc.setMultilingual(false);
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
		
		skel = new GenericSkeleton();
		int eventType;
		
		while ( reader.hasNext() ) {
			eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:
				//--process <message>--
				if (reader.getLocalName().equals("message")){
					
					//--if <message> ends context--
					if(procCtxGrp){

						//--create new group event--
						StartGroup group = new StartGroup(parentIds.peek().toString(), String.valueOf(++groupId));
						group.setSkeleton(skel);
						group.setName(procCtxGrpNameValue);
						
						parentIds.push(groupId);
						queue.add(new Event(EventType.START_GROUP, group));
					
						//--restart the skeleton--
						skel = new GenericSkeleton();

						procCtxGrp = false;
					}
					
					tMsg = new TsMessage(trgLang,reader,queue, lineBreak);
					return tMsg.processMessage(tuId);
				}
				
				if (reader.getLocalName().equals("context")){

					procCtxGrp = true;
					
					//--restart the skeleton--
					DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
					skel = new GenericSkeleton();
					queue.add(new Event(EventType.DOCUMENT_PART, dp));
					
					storeStartElement();
					break;
				}
					
				if (reader.getLocalName().equals("name")){
					if(procCtxGrp){
						procCtxGrpName=true;
						storeStartElement();
					}
					break;
				}

				storeStartElement();
				break;
			case XMLStreamConstants.END_ELEMENT:

				if (reader.getLocalName().equals("context")){
					
					//--restart the skeleton--
					DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel);
					skel = new GenericSkeleton();
					queue.add(new Event(EventType.DOCUMENT_PART, dp));					
					
					storeEndElement();
					
					// Pop and checks the value for this group
					int id = parentIds.pop();
					// Else: it's a structural group
					Ending ending = new Ending(String.valueOf(id));
					ending.setSkeleton(skel);
					queue.add(new Event(EventType.END_GROUP, ending));
			
					//--restart the skeleton--
					skel = new GenericSkeleton();
					break;
				}
				
				if (reader.getLocalName().equals("name")){
					if(procCtxGrp){
						procCtxGrpName=false;
						storeEndElement();
					}
					break;
				}

				storeEndElement();
				break;				
				
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.CDATA:
				skel.append(reader.getText().replace("\n", lineBreak));
				break;				
			case XMLStreamConstants.CHARACTERS: //TODO: Check if it's ok to not check for unsupported chars
				
				String str = Util.escapeToXML(reader.getText(), 0, true, null);
				
				if(procCtxGrp && procCtxGrpName){
					procCtxGrpNameValue=str;
				}
		
				skel.append(str);
				//skel.append(reader.getText());
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


	private void storeEndElement () {
		String ns = reader.getPrefix();
		if (( ns == null ) || ( ns.length()==0 )) {
			skel.append("</"+reader.getLocalName()+">");
		}
		else {
			skel.append("</"+ns+":"+reader.getLocalName()+">");
		}
	}
	
	

	
}
