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
import java.net.URL;
import java.util.Queue;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IEncoder;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TargetsAnnotation;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public class TmxFilter implements IFilter {
	
	private static String [] docStartGroups = {"tmx","header","body","ude"};
	private static String [] docDocumentParts = {"note","prop","map"};
	private boolean startSent = false;
	private boolean startDocSent = false;
	private Queue<FilterEvent> queue;
	private String sourceLanguage;
	private String targetLanguage;

	private int docPartId = 0;			//--keep track of sequential documentPartId--
	private int groupId = 0;				//--keep track of sequential groupId--
	private int endGroupId = 0;				//--keep track of sequential endGroupId--
	private int tuId = 0;				//--keep track of sequential endGroupId--	
	private boolean canceled;
	
	private Stack<StartGroup> groupStack = new Stack<StartGroup>();	//--keep track of parent startGroups--
	
	private XMLStreamReader reader;
	private int parseState = 0;

	private IResource currentRes;
	
	private StartDocument docRes;
	private String encoding;

	public void cancel() {
		canceled = true;
	}

	public void close() {
		try {
			//inputText = null;
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
			parseState = 0;
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public String getName() {
		return "okf_tmx";
	}

	public IResource getResource() {
		return currentRes;
	}

	public boolean hasNext() {
		try {
			return reader.hasNext();
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);		
		}
	}

	
	public FilterEvent next() {
		
		// Cancel if requested
		if ( canceled ) {
			//parseState = 0;
			//currentRes = null;
			return null;
		}
		
		try {

			//--process start document separately--
			if(!startDocSent && reader.getEventType() == XMLStreamConstants.START_DOCUMENT){

				//--
				if(!startSent){
					System.out.println("START");
					startSent=true;
					return new FilterEvent(FilterEventType.START);
				}
				
				System.out.println("START_DOCUMENT");
				
				startDocSent = true;
				
				//--DEBUG--
				/*System.out.println("getEncoding()-> "+reader.getEncoding());
				System.out.println("getVersion() -> "+reader.getVersion());
				System.out.println("isStandalone() -> "+reader.isStandalone());
				System.out.println("standaloneSet() -> "+reader.standaloneSet());
				System.out.println("getCharacterEncodingScheme() -> "+reader.getCharacterEncodingScheme());*/
				//System.out.println("nextTag() -> "+reader.nextTag());
				
				docRes = new StartDocument();
				docRes.setEncoding("utf-8");
				docRes.setLanguage("en");
				docRes.setIsMultilingual(true);
				
				currentRes = docRes;
				return new FilterEvent(FilterEventType.START_DOCUMENT, docRes, new GenericSkeleton("<?xml version=\"1.0\" ?>"));
				
			}
			
			int eventType = reader.next();

			switch ( eventType ) {
			case XMLStreamConstants.START_ELEMENT:

				System.out.println("\nSTART_ELEMENT");
				if( isDocGroup(reader.getLocalName())){
					System.out.println("--group element--");
					return processStartGroup();
				}else if (isDocumentPart(reader.getLocalName())){
					System.out.println("--document note--");
					return processDocumentPart();
				}else if (reader.getLocalName().equals("tu")){
					System.out.println("--translation unit--");
					return processTranslationUnit();
				}else{
					System.out.println("--"+reader.getLocalName());
					break;
				}

			case XMLStreamConstants.END_ELEMENT:

				System.out.println("\nEND_ELEMENT");
				if( isDocGroup(reader.getLocalName())){
					System.out.println("--group element--");
					return processEndGroup();
				}else{
					System.out.println("--"+reader.getLocalName());
					break;
				}
				
			case XMLStreamConstants.ATTRIBUTE:
				System.out.println("ATTRIBUTE");
				break;
			case XMLStreamConstants.NAMESPACE:
				System.out.println("NAMESPACE");
				break;
			case XMLStreamConstants.CHARACTERS:
				//System.out.println("\nCHARACTERS");
				//System.out.println("getTextXXX()-> \""+reader.getText()+"\"");
				DocumentPart dpC = new DocumentPart(String.format("dp%d", ++docPartId), false);
				return new FilterEvent(FilterEventType.DOCUMENT_PART, dpC, new GenericSkeleton(reader.getText()));

				//break;
			case XMLStreamConstants.ENTITY_DECLARATION:
				System.out.println("ENTITY_DECLARATION");
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				System.out.println("ENTITY_REFERENCE");
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				System.out.println("PROCESSING_INSTRUCTION");
				break;
			case XMLStreamConstants.COMMENT:
				System.out.println("COMMENT");
				
				DocumentPart dp = new DocumentPart("dp1", false);
				GenericSkeleton skel = new GenericSkeleton("<!--");
				skel.addRef(dp, "comment-1", null);
				skel.append("-->");				
				dp.setProperty(new Property("comment-1",reader.getText(), true));
				
				currentRes = dp;
				return new FilterEvent(FilterEventType.DOCUMENT_PART, dp, skel);

			case XMLStreamConstants.END_DOCUMENT:
				System.out.println("END_DOCUMENT");

				Ending ending = new Ending(String.format("%d", ++groupId));
				currentRes = ending;
				return new FilterEvent(FilterEventType.END_DOCUMENT, ending);
				//break;
			case XMLStreamConstants.DTD:
				System.out.println("DTD");
				System.out.println("getText: "+reader.getText());
				break;
			case XMLStreamConstants.NOTATION_DECLARATION:
				System.out.println("NOTATION_DECLARATION");
				break;
			case XMLStreamConstants.SPACE:

				//TODO: Make sure to add the space to the previous Generic Skeleton
				DocumentPart dpS = new DocumentPart(String.format("dp%d", ++docPartId), false);
				return new FilterEvent(FilterEventType.DOCUMENT_PART, dpS, new GenericSkeleton(reader.getText()));

			case XMLStreamConstants.CDATA:
				System.out.println("CDATA");
				break;
			default:
				break;
			}
			
			DocumentPart dp = new DocumentPart(String.format("dp%d", ++docPartId), false);
			return new FilterEvent(FilterEventType.DOCUMENT_PART, dp);
			
				//String name = reader.getLocalName();
				//System.out.println("getLocalName: "+name);
			
			
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public void open(URL inputUrl) {

		try { 
			open(inputUrl.openStream());
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}

	}

	public void open(CharSequence inputText) {
		// TODO Auto-generated method stub
	}
	
	public void open (InputStream input) {

		//--initialize
		canceled = false;
		docPartId = 0;			
		groupId = 0;			
		endGroupId = 0;			
		tuId = 0;	
		//parseState = 1;
		
		try {
			close();
			
			// Open the input reader from the provided stream
			BOMAwareInputStream bis = new BOMAwareInputStream(input, encoding);
			
			XMLInputFactory fact = XMLInputFactory.newInstance();
			fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			fact.setProperty(XMLInputFactory2.IS_VALIDATING, false);
			reader = fact.createXMLStreamReader(bis, bis.detectEncoding());
			//nextAction = -1;
			//sklID = 0;
			//itemID = 0;
			//sklAfter = new SkeletonUnit();

			//parseState = 1;
			//canceled = false;
			
			
			// Set the start event
			//queue = new LinkedList<FilterEvent>();
			//queue.add(new FilterEvent(FilterEventType.START_DOCUMENT, docRes));
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	//--TODO--
	public IParameters getParameters() {
		// TODO Auto-generated method stub
		return null;
	}	
	public void setParameters(IParameters params) {
		// TODO Auto-generated method stub

	}

	private FilterEvent nextEvent () {
		if ( queue.size() == 0 ) return null;
		currentRes = queue.peek().getResource();
		if ( queue.peek().getEventType() == FilterEventType.END_DOCUMENT ) {
			parseState = 0; // No more event after
		}
		return queue.poll();
	}

	public void setOptions(String language, String defaultEncoding,
			boolean generateSkeleton) {

		setOptions(language, null, defaultEncoding, generateSkeleton);
	}

	public void setOptions(String sourceLanguage, String targetLanguage,
			String defaultEncoding, boolean generateSkeleton) {

		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		encoding = defaultEncoding;
	}
	
	/**
	 * Generates a start group filter event
	 * @return FilterEvent
	 */		
	private FilterEvent processStartGroup(){
		
		StartGroup group;
		GenericSkeleton skel;
		
		if(groupStack.size()==0){
			group = new StartGroup("d1",String.format("g%d", ++groupId), false);
		}else{
			group = new StartGroup(groupStack.peek().getId(),String.format("g%d", ++groupId), false);
		}
				
		skel = new GenericSkeleton("<");
		System.out.print("<");
			
		skel.append(reader.getLocalName());
		System.out.print(reader.getLocalName());
		
		group.setName(reader.getLocalName());
			
		for(int i=0; i< reader.getAttributeCount(); i++){
			skel.append(" "+reader.getAttributeLocalName(i)+"=\""+reader.getAttributeValue(i)+"\"");
			System.out.print(" "+reader.getAttributeLocalName(i)+"=\""+reader.getAttributeValue(i)+"\"");
			//TODO: handle readonly vs localizable here
			//group.setProperty(new Property(reader.getAttributeLocalName(i),reader.getAttributeValue(i), true));
		}

		skel.append(">");
		System.out.println(">");
		
		group.setSkeleton(skel);
		groupStack.add(group);

		currentRes = group;
		return new FilterEvent(FilterEventType.START_GROUP, group);
	}
	
	/**
	 * Generates a end group filter event
	 * @return FilterEvent
	 */		
	private FilterEvent processEndGroup(){
		
		Ending ending;
		GenericSkeleton skel;
		
		ending = new Ending(String.format("e%d", ++endGroupId));
		skel = new GenericSkeleton("</"+reader.getLocalName()+">");
		System.out.println("</"+reader.getLocalName()+">");
			
		ending.setSkeleton(skel);
		groupStack.pop();

		currentRes = ending;
		return new FilterEvent(FilterEventType.END_GROUP, ending);
	}

	
	/**
	 * Process a document note
	 * @return FilterEvent
	 */		
	private FilterEvent processDocumentPart(){
		
		DocumentPart dp;
		GenericSkeleton skel;
		String startElement = reader.getLocalName();
		
		
		dp = new DocumentPart(String.format("dp%d", ++docPartId), false);
				
		skel = new GenericSkeleton("<");
		System.out.print("<");
			
		skel.append(startElement);
		System.out.print(startElement);
		
		dp.setName(startElement);
			
		for(int i=0; i< reader.getAttributeCount(); i++){
			skel.append(" "+reader.getAttributeLocalName(i)+"=\""+reader.getAttributeValue(i)+"\"");
			System.out.print(" "+reader.getAttributeLocalName(i)+"=\""+reader.getAttributeValue(i)+"\"");
			//TODO: handle readonly vs localizable here
			//group.setProperty(new Property(reader.getAttributeLocalName(i),reader.getAttributeValue(i), true));
		}

		skel.append(">");
		System.out.print(">");
		
		
		boolean endNoteProcessed = false;
		try {
			while(reader.hasNext() && !endNoteProcessed){
				
				int eventType = reader.next();
				
				switch ( eventType ) {

				//--process the document note content--
				case XMLStreamConstants.CHARACTERS:
					
					skel.append(reader.getText());
					System.out.print(reader.getText());
					break;
					
				//--process the end document note--				
				case XMLStreamConstants.END_ELEMENT:

					if(reader.getLocalName().equals(startElement)){

						skel.append("</"+reader.getLocalName()+">");
						System.out.println("</"+reader.getLocalName()+">");
						
						endNoteProcessed = true;
					}
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		
		dp.setSkeleton(skel);

		currentRes = dp;
		return new FilterEvent(FilterEventType.DOCUMENT_PART, dp);
	}
	
	
	/**
	 * Process an entire tu element
	 * @return FilterEvent
	 */		
	private FilterEvent processTranslationUnit(){
		
		int tuvType=0;	//--tracks tuv type: 1 for source, 2 for target, otherwise 0 language independent--
		
		GenericSkeleton skel = new GenericSkeleton();
		TextUnit tu = new TextUnit(String.format("t%d", ++tuId),null,false,"text/xml");
		TextContainer trgCont = tu.setTarget(targetLanguage, new TextContainer());
		
		//--process the opening tu element which is 0 unspecified at this point--
		appendStartElement(skel, tu, tuvType);

		String localName;
		boolean completed = false;
		try {
			while(reader.hasNext() && !completed){
				
				int eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
					//--whitespace and linebreaks--
					skel.append(reader.getText());
					break;
				case XMLStreamConstants.START_ELEMENT:

					localName = reader.getLocalName().toLowerCase(); 
					if(localName.equals("note") || localName.equals("prop")){
						processNoteProp(skel, tu, tuvType);	
					}else if(reader.getLocalName().equals("tuv")){
						tuvType = tuvType();
						appendStartElement(skel, tu, tuvType);
					}else if(reader.getLocalName().equals("seg")){
						if(tuvType==1){
							processSeg(skel, tuvType, tu.getSource(), tu);
						}if(tuvType==2){
							processSeg(skel, tuvType, trgCont, tu);
						}if(tuvType==0){
							processSeg(skel, tuvType, null, null);
						}
					}
					break;
					
				//--process the end document note--				
				case XMLStreamConstants.END_ELEMENT:

					//--lowercase for comparison--
					localName = reader.getLocalName().toLowerCase(); 
					if(localName.equals("tu")){
						appendEndElement(skel);
						completed = true;
					}else if(reader.getLocalName().equals("tuv")){
						appendEndElement(skel);
						tuvType=0; 	//--reset tuvType to unspecified--
					}			
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		
		tu.setSkeleton(skel);

		currentRes = tu;
		return new FilterEvent(FilterEventType.TEXT_UNIT, tu);
	}
	
	
	/**
	 * Process an opening element, building the skeleton and adding the properties to nameable 
	 */			
	public void appendStartElement(GenericSkeleton skel, INameable nameable, int tuvType){
		
		skel.append("<"+reader.getLocalName());
		System.out.print("<"+reader.getLocalName());
		
		for(int i=0; i< reader.getAttributeCount(); i++){
			
			String attributeName;	//--handle prefixes--
			
			if(reader.getAttributePrefix(i).length()>0){
				attributeName=reader.getAttributePrefix(i)+":"+reader.getAttributeLocalName(i);
			}else{
				attributeName=reader.getAttributeLocalName(i);
			}
			
			skel.append(" "+attributeName+"=\""+reader.getAttributeValue(i)+"\"");
			System.out.print(" "+attributeName+"=\""+reader.getAttributeValue(i)+"\"");

			//--set the properties depending on the tuvType--
			if(tuvType == 1){
				nameable.setSourceProperty(new Property(attributeName, reader.getAttributeValue(i), true));	
			}else if(tuvType == 2){
				nameable.setTargetProperty(targetLanguage, new Property(attributeName,reader.getAttributeValue(i), true));
			}else if(tuvType == 0){
				nameable.setProperty(new Property(attributeName,reader.getAttributeValue(i), true));
			}
		}

		skel.append(">");
		System.out.print(">");
	}
	
	/**
	 * Process a closing element, building the skeleton 
	 */			
	public void appendEndElement(GenericSkeleton skel){
		skel.append("</"+reader.getLocalName()+">");
		System.out.println("</"+reader.getLocalName()+">");
	}	
	
	/**
	 * Process a content element, appending the skeleton to skel and adding the properties to nameable 
	 */			
	private void processNoteProp(GenericSkeleton skel, INameable nameable, int tuvType){
		
		String startElement = reader.getLocalName();
		appendStartElement(skel, nameable, tuvType);
		boolean completed = false;				
		try {
			while(reader.hasNext() && !completed){
				int eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:

					skel.append(reader.getText());
					System.out.print(reader.getText());
					
					//--set the properties depending on the tuvType--
					if(tuvType == 1){
						nameable.setSourceProperty(new Property(startElement, reader.getText(), true));	
					}else if(tuvType == 2){
						nameable.setTargetProperty(targetLanguage, new Property(startElement, reader.getText(), true));
					}else if(tuvType == 0){
						nameable.setProperty(new Property(startElement, reader.getText(), true));
					}
					break;
				
				case XMLStreamConstants.END_ELEMENT:

					if(reader.getLocalName().equals(startElement)){
						appendEndElement(skel);
						completed = true;
					}
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Process a segment <seg>*</seg>, appending the skeleton to skel and adding the properties to nameable and reference to tu 
	 */			
	private void processSeg(GenericSkeleton skel, int tuvType, TextContainer tc, TextUnit tu){
		
		String startElement = reader.getLocalName();
			
		skel.append("<"+reader.getLocalName()+">");
		System.out.print("<"+reader.getLocalName()+">");
		//TODO: <seg> shouldn't have any attributes but possibly add any attributes to the skeleton-- 
		
		boolean completed = false;				//--controls the loop--
		try {
			while(reader.hasNext() && !completed){
				
				int eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
					if(tuvType == 1 || tuvType == 2){
						//--if source or target add it to TextContainer (and make a ref later)--
						tc.append(reader.getText());
						System.out.print(reader.getText());
					}else if (tuvType == 0){
						//--if unspecified add it to the general skeleton--
						skel.append(reader.getText());
						System.out.print(reader.getText());
						//TODO: Need to add placeholders to the skeleton as well
					}
					break;
				
				case XMLStreamConstants.START_ELEMENT:		
					
					if(tuvType == 1 || tuvType == 2){
						String localName = reader.getLocalName().toLowerCase();
						
						if(localName.equals("ph") || localName.equals("it") || localName.equals("hi")){
							processInlineElement(tc, TagType.PLACEHOLDER);	
						}else if(localName.equals("bpt")){
							processInlineElement(tc, TagType.OPENING);	
						}else if(localName.equals("ept")){
							processInlineElement(tc, TagType.CLOSING);	
						}
						break;
					}else if (tuvType == 0){
						//TODO: handle remaining inline tags
					}
					
				case XMLStreamConstants.END_ELEMENT:

					//--matches the opening element--
					if(reader.getLocalName().equals(startElement)){
						if(tuvType == 1){
							//--add ref to source
							skel.addRef(tu, null);
						}else if(tuvType == 2){
							//--add ref to target
							skel.addRef(tu, targetLanguage);
						}
						appendEndElement(skel);
						completed = true;
					}
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * Process an Inline Element, create a code and add to the passed TextContainer. 
	 * Include start tag, content, and end tag. 
	 */		
	private void processInlineElement(TextContainer tc, TagType tt){
		
		String startElement = reader.getLocalName();	//--store the start element--
		StringBuffer sb = new StringBuffer();			//--store the code--
		String type = startElement;						//--store the type--
		
		sb.append("<"+startElement);
		System.out.print("<"+startElement);
			
		for(int i=0; i< reader.getAttributeCount(); i++){

			//--set the type if attribute type exists--
			if(reader.getAttributeLocalName(i).equals("type")){
				type=reader.getAttributeValue(i);
			}
			
			sb.append(" "+reader.getAttributeLocalName(i)+"=\""+reader.getAttributeValue(i)+"\"");
			System.out.print(" "+reader.getAttributeLocalName(i)+"=\""+reader.getAttributeValue(i)+"\"");
		}
		
		sb.append(">");
		System.out.print(">");
		
		boolean elementProcessed = false;
		try {
			while(reader.hasNext() && !elementProcessed){
				int eventType = reader.next();
				switch ( eventType ) {

				//--process the document note content--
				case XMLStreamConstants.CHARACTERS:

					sb.append(reader.getText());
					System.out.print(reader.getText());
					break;
					
				//--process the end document note--				
				case XMLStreamConstants.END_ELEMENT:

					if(reader.getLocalName().equals(startElement)){

						sb.append("</"+reader.getLocalName()+">");
						System.out.print("</"+reader.getLocalName()+">");
						elementProcessed = true;
					}
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		tc.append(tt, type, sb.toString());
	}
	
	
	/**
	 * Checks the elementName to see if it's a group element in the general doc section as listed in the docStartGroups array
	 * @param elementName Name of the element.
	 * @return returns true if elementName refers to a group.
	 */	
	private boolean isDocGroup(String elementName){
		for (String g : docStartGroups) {
		    if(elementName.equals(g))
		    	return true;
		}
		return false;
	}
	
	/**
	 * Checks the elementName to see if it's a group element
	 * @param elementName Name of the element.
	 * @return returns true if elementName refers to a group.
	 */	
	private boolean isDocumentPart(String elementName){

		boolean isDocumentPart = false;
		
		//--check if it's in the list of documentParts--
		for (String d : docDocumentParts) {
		    if(elementName.equals(d))
		    	isDocumentPart = true;
		}

		if( isDocumentPart){
			
			//--check if we're in the document part--
			for (StartGroup sg : groupStack){
				if( sg.getName().equals("body")){
			    	return false;
				}
			}
			return true;
		}		
		return false;
	}
	
	
	/**
	 * Checks the tuv lang attribute to see if it's a source tuv
	 * @return 	returns 0 for tuv that is neither source or target
	 * 			returns 1 for source tuv
	 * 			returns 2 for target tuv
	 */		
	private int tuvType(){
		
		//TODO: change this to check for the actual name of the attribute (not the index)
		if (reader.getAttributeValue(0).toLowerCase().equals(sourceLanguage.toLowerCase())){
			return 1;
		}else if (reader.getAttributeValue(0).toLowerCase().equals(targetLanguage.toLowerCase())){
			return 2;
		}
		return 0;
	}
	
}
