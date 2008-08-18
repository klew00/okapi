/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

package net.sf.okapi.filters.xliff;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.LocaleData;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class XLIFFReader {

	public static final int       STATUS_NOTRANS      = 0;
	public static final int       STATUS_UNUSED       = 1;
	public static final int       STATUS_TOTRANS      = 2;
	public static final int       STATUS_TOEDIT       = 3;
	public static final int       STATUS_TOREVIEW     = 4;
	public static final int       STATUS_OK           = 5;

	public static final int       RESULT_ENDINPUT          = 0;
	public static final int       RESULT_STARTFILE         = 1;
	public static final int       RESULT_ENDFILE           = 2;
	public static final int       RESULT_STARTGROUP        = 3;
	public static final int       RESULT_ENDGROUP          = 4;
	public static final int       RESULT_STARTTRANSUNIT    = 5;
	public static final int       RESULT_ENDTRANSUNIT      = 6;
	public static final int       RESULT_SKELETON          = 7;
	

	protected Resource            resource;
	protected Group               fileRes;
	protected Stack<Group>        groupResStack;
	protected TextUnit            item;

	private SkeletonUnit          sklBefore;
	private SkeletonUnit          sklAfter;
	private SkeletonUnit          currentSkl;
	private int                   itemID;
	private int                   sklID;
	private boolean               sourceDone;
	private boolean               targetDone;
	private XMLStreamReader       reader; 
	private TextContainer         content;
	private int                   nextAction;
//	private Pattern               pattern;
	
//	private String                elemTransUnit;
//	private String                elemFile;
//	private String                elemNote;
	

	public XLIFFReader () {
		resource = new Resource();
		sklBefore = new SkeletonUnit();
		groupResStack = new Stack<Group>();
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
	
	public void open (InputStream input) {
		try {
			close();
			XMLInputFactory fact = XMLInputFactory.newInstance();
			//fact.setProperty(XMLInputFactory.IS_COALESCING, false);
			fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			reader = fact.createXMLStreamReader(input);
			nextAction = -1;
			sklID = 0;
			itemID = 0;
			sklAfter = new SkeletonUnit();
/*			if ( resource.params.useStateValues ) {
				pattern = Pattern.compile(resource.params.stateValues);
			}*/
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the last item read.
	 * @return The last item read.
	 */
	public TextUnit getItem () {
		return item;
	}

	/**
	 * Gets the last skeleton part read.
	 * @return
	 */
	public SkeletonUnit getSkeleton () {
		return currentSkl;
	}
	
	/**
	 * Reads the next part of the input.
	 * @return One of the RESULT_* values.
	 */
	public int readItem () {
		try {
			switch ( nextAction ) {
			case RESULT_STARTTRANSUNIT:
				nextAction = RESULT_ENDTRANSUNIT;
				return RESULT_STARTTRANSUNIT;
			case RESULT_ENDTRANSUNIT:
				nextAction = -1;
				return RESULT_ENDTRANSUNIT;
			case RESULT_STARTGROUP:
				nextAction = -1;
				return RESULT_STARTGROUP;
			case RESULT_ENDGROUP:
				nextAction = -2;
				return RESULT_ENDGROUP;
			case RESULT_ENDINPUT:
				nextAction = -1;
				return RESULT_ENDINPUT;
			case -2: // Pop group
				nextAction = -1;
				groupResStack.pop();
				break;
			}
			sourceDone = targetDone = false;
			sklBefore.setData(sklAfter.toString());
			sklBefore.setID(String.format("s%d", ++sklID));
			currentSkl = sklBefore;
			resource.needTargetElement = true;

			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					String name = reader.getLocalName();
					if ( "file".equals(name) ) {
						processFile();
					}
					else if ( "trans-unit".equals(name) ) {
						return processStartTransUnit();
					}
					//else if ( "group".equals(name) ) {
					//	return processStartGroup();
					//}
					else storeStartElement();
					break;
				case XMLStreamConstants.END_ELEMENT:
					//if ( "group".equals(reader.getLocalName()) ) {
					//	return processEndGroup();
					//}
					//else 
					storeEndElement();
					break;
				case XMLStreamConstants.SPACE:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.CHARACTERS:
					currentSkl.appendData(Util.escapeToXML(reader.getText(),
						0, resource.params.escapeGT));
					break;
				case XMLStreamConstants.COMMENT:
					currentSkl.appendData("<!--"+ reader.getText() + "-->");
					break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					currentSkl.appendData("<?"+ reader.getPITarget() + " "
						+ reader.getPIData() + "?>");
					break;
				case XMLStreamConstants.DTD:
					processDTD();
					break;
				case XMLStreamConstants.ENTITY_REFERENCE:
					//TODO: handle entity references
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
		nextAction = RESULT_ENDINPUT; 
		return RESULT_SKELETON;
	}

	private void processDTD () {
		//TODO: handle DTD declaration
//		List notations = (List)reader.getProperty("javax.xml.stream.notations");
//		List entities = (List)reader.getProperty("javax.xml.stream.entities");
//		int i = notations.size();
	}
	
/*	private int processStartGroup () {
		storeStartElement();
		groupResStack.push(new GroupResource());
		nextAction = RESULT_STARTGROUP;
		return RESULT_SKELETON;
	}
	
	private int processEndGroup () {
		// Pop is done in main loop after the call comes back
		storeEndElement();
		nextAction = RESULT_ENDGROUP;
		return RESULT_SKELETON;
	}*/
	
	private void resetItem () {
		item = new TextUnit();
	}
	
	private int processFile () {
		fileRes = new Group();
		storeStartElement();
		String tmp = reader.getAttributeValue("", "original");
		if ( tmp == null ) throw new RuntimeException("Missing attribute 'original'.");
		else fileRes.setName(tmp);
		//TODO: check lang, etc.
		return RESULT_STARTFILE;
	}
	
	private void storeStartElement () {
		String prefix = reader.getPrefix();
		if (( prefix == null ) || ( prefix.length()==0 )) {
			currentSkl.appendData("<"+reader.getLocalName());
		}
		else {
			currentSkl.appendData("<"+prefix+":"+reader.getLocalName());
		}

		int count = reader.getNamespaceCount();
		for ( int i=0; i<count; i++ ) {
			prefix = reader.getNamespacePrefix(i);
			currentSkl.appendData(String.format(" xmlns%s=\"%s\"",
				((prefix.length()>0) ? ":"+prefix : ""),
				reader.getNamespaceURI(i)));
		}
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i); 
			currentSkl.appendData(String.format(" %s%s=\"%s\"",
				(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
				reader.getAttributeLocalName(i),
				reader.getAttributeValue(i)));
		}
		currentSkl.appendData(">");
	}
	
	private void storeEndElement () {
		String ns = reader.getPrefix();
		if (( ns == null ) || ( ns.length()==0 )) {
			currentSkl.appendData("</"+reader.getLocalName()+">");
		}
		else {
			currentSkl.appendData("</"+ns+":"+reader.getLocalName()+">");
		}
	}
	
	private void checkTarget () {
		if ( !sourceDone ) return;
		if ( targetDone ) return;
		currentSkl = sklAfter;
		targetDone = true;
	}
	
	private int processStartTransUnit () {
		try {
			resetItem();
			item.setID(String.format("%d", ++itemID));
			storeStartElement();

			String tmp = reader.getAttributeValue("", "translate");
			if ( tmp != null ) item.setIsTranslatable(tmp.equals("yes"));
		
			tmp = reader.getAttributeValue("", "resname");
			if ( tmp != null ) item.setName(tmp);
			else if ( resource.params.fallbackToID ) {
				tmp = reader.getAttributeValue("", "id");
				if ( tmp == null ) throw new RuntimeException("Missing attribute 'id'.");
				item.setName(tmp);
			}

			tmp = reader.getAttributeValue("", "restype");
			if ( tmp != null ) item.setType(tmp);
			
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
						processSource();
						storeEndElement();
					}
					else if ( "target".equals(name) ) {
						storeStartElement();
						processTarget();
						checkTarget();
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
						nextAction = RESULT_STARTTRANSUNIT;
						currentSkl = sklBefore;
						return RESULT_SKELETON;
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
					currentSkl.appendData(Util.escapeToXML(reader.getText(),
						0, resource.params.escapeGT));
					break;
				case XMLStreamConstants.COMMENT:
					checkTarget();
					currentSkl.appendData("<!--"+ reader.getText() + "-->");
					break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					checkTarget();
					currentSkl.appendData("<?"+ reader.getPITarget() + " "
						+ reader.getPIData() + "?>");
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
		return RESULT_ENDINPUT;
	}
	
	private void processNote () {
		try {
			StringBuilder tmp = new StringBuilder();
			if ( item.getProperties().containsKey("note") ) {
				tmp.append(item.getProperty("note"));
				tmp.append("\n---\n");
			}
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					currentSkl.appendData(Util.escapeToXML(reader.getText(),
						0, resource.params.escapeGT));
					tmp.append(reader.getText());
					break;
				case XMLStreamConstants.END_ELEMENT:
					String name = reader.getLocalName();
					if ( name.equals("note") ) {
						//TODO: Handle 'annotates', etc.
						item.setProperty("note", tmp.toString());
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
	
	/**
	 * Processes a segment content. Set the content and set inCode variables 
	 * before calling this method with <source> or <target>.
	 * @param tagName The name of the element content that is being processed.
	 * @param store True if the data must be stored in the skeleton.
	 * This is used to merge later on.
	 * @param content The object where to put the code.
	 * @param inlineCodes Array where to save the in-line codes. Do not save if this parameter
	 * is set to null.
	 */
	private void processContent (String tagName,
		boolean store,
		TextContainer content,
		ArrayList<Code> inlineCodes)
	{
		try {
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
						currentSkl.appendData(Util.escapeToXML(reader.getText(),
							0, resource.params.escapeGT));
					break;
		
				case XMLStreamConstants.END_ELEMENT:
					name = reader.getLocalName();
					if ( name.equals(tagName) ) {
						return;
					}
					else if ( name.equals("g") || name.equals("mrk") ) {
						if ( store ) storeEndElement();
						//TODO: was int tmpID = idStack.pop();
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
							0, resource.params.escapeGT));
					if ( store )
						currentSkl.appendData(Util.escapeToXML(reader.getText(),
							0, resource.params.escapeGT));
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void processSource () {
		if ( sourceDone ) {
			// Case where this entry is not the main one, but from an alt-trans
			TextContainer tmpCont = new TextContainer(null);
			processContent("source", true, tmpCont, null);
			return;
		}
		content = item.getSourceContent();
		content.clear();
		processContent("source", true, content, resource.srcCodes);
		sklAfter.setData("");
		sourceDone = true;
	}
	
	private void processTarget () {
		if ( targetDone ) {
			// Case where this entry is not the main one, but from an alt-trans
			TextContainer tmpCont = new TextContainer(null);
			processContent("target", true, tmpCont, null);
			return;
		}
		
		LocaleData ld = new LocaleData(item);
		//String tmp = reader.getAttributeValue("", "state");
		/*if ( tmp != null ) {
			item.getTarget().setProperty("state", tmp);
			if ( tmp.equals("needs-translation") ) resource.status = STATUS_TOTRANS;
			else if ( tmp.equals("final") ) resource.status = STATUS_OK;
			else if ( tmp.equals("translated") ) resource.status = STATUS_TOEDIT;
			else if ( tmp.equals("needs-review-translation") ) resource.status = STATUS_TOREVIEW;
		}*/
		content = ld.getContent();
		processContent("target", false, content, resource.trgCodes);
		resource.needTargetElement = false;
		item.setTarget(ld);
		if ( item.isEmpty() || content.isEmpty() ) {
			item.setTargetContent(null);
		}
	}
	
/*	private boolean isExtractable () {
		if ( !resource.params.useStateValues ) return true;
		if ( !item.hasTarget() ) return true;
		
		String state = (String)item.getTarget().getProperty("state");
		if (( state == null ) || ( state.length() == 0 )) {
			return resource.params.extractNoState;
		}
		return pattern.matcher(state).find();
	}*/
	
}
