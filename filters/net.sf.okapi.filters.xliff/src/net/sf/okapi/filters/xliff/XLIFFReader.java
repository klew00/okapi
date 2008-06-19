package net.sf.okapi.filters.xliff;

import java.io.InputStream;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.GroupResource;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.ISkeletonResource;
import net.sf.okapi.common.resource.SkeletonResource;

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
	protected GroupResource       fileRes;
	protected IExtractionItem     item;

	private SkeletonResource      sklBefore;
	private SkeletonResource      sklAfter;
	private SkeletonResource      currentSkl;
	private int                   sklID;
	private boolean               sourceDone;
	private boolean               targetDone;
	private XMLStreamReader       reader; 
	private IContainer            content;
	private int                   nextAction;
	private boolean               fallbackToID;
	

	public XLIFFReader () {
		resource = new Resource();
		sklBefore = new SkeletonResource();
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
	
	public void open (InputStream input,
		boolean fallbackToID)
	{
		try {
			close();
			this.fallbackToID = fallbackToID;
			XMLInputFactory fact = XMLInputFactory.newInstance();
			//fact.setProperty(XMLInputFactory.IS_COALESCING, false);
			fact.setProperty(XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE, true);
			reader = fact.createXMLStreamReader(input);
			nextAction = -1;
			sklID = 0;
			sklAfter = new SkeletonResource();
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the last item read.
	 * @return The last item read.
	 */
	public IExtractionItem getItem () {
		return item;
	}

	public ISkeletonResource getSkeleton () {
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
			case RESULT_ENDINPUT:
				nextAction = -1;
				return RESULT_ENDINPUT;
			}
			sourceDone = targetDone = false;
			sklBefore.data = new StringBuilder(sklAfter.toString());
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
					else storeStartElement();
					break;
				case XMLStreamConstants.END_ELEMENT:
					storeEndElement();
					break;
				case XMLStreamConstants.SPACE:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.CHARACTERS:
					currentSkl.data.append(reader.getText());
					break;
				case XMLStreamConstants.COMMENT:
					currentSkl.data.append("<!--"+ reader.getText() + "-->");
					break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					currentSkl.data.append("<?"+ reader.getPITarget() + " "
						+ reader.getPIData() + "?>");
					break;
				case XMLStreamConstants.START_DOCUMENT:
					//TODO
					break;
				case XMLStreamConstants.END_DOCUMENT:
					//TODO
					break;
				case XMLStreamConstants.ENTITY_REFERENCE:
					//TODO
					break;
					//More to do
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
		nextAction = RESULT_ENDINPUT; 
		return RESULT_SKELETON;
	}
	
	private void resetItem () {
		item = new ExtractionItem();
		resource.srcElem = null;
		resource.trgElem = null;
		resource.status = STATUS_TOTRANS;
	}
	
	private int processFile () {
		fileRes = new GroupResource();
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
			currentSkl.data.append("<"+reader.getLocalName());
		}
		else {
			currentSkl.data.append("<"+prefix+":"+reader.getLocalName());
		}

		int count = reader.getNamespaceCount();
		for ( int i=0; i<count; i++ ) {
			currentSkl.data.append(String.format(" xmlns:%s=\"%s\"",
				reader.getNamespacePrefix(i),
				reader.getNamespaceURI(i)));
		}
		
		count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			prefix = reader.getAttributePrefix(i); 
			currentSkl.data.append(String.format(" %s%s=\"%s\"",
				(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
				reader.getAttributeLocalName(i),
				reader.getAttributeValue(i)));
		}
		currentSkl.data.append(">");
	}
	
	private void storeEndElement () {
		String ns = reader.getPrefix();
		if (( ns == null ) || ( ns.length()==0 )) {
			currentSkl.data.append("</"+reader.getLocalName()+">");
		}
		else {
			currentSkl.data.append("</"+ns+":"+reader.getLocalName()+">");
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
			storeStartElement();
			String sTmp = reader.getAttributeValue("", "translate");
			if ( sTmp!= null ) item.setIsTranslatable(sTmp.equals("yes"));
		
			sTmp = reader.getAttributeValue("", "id");
			if ( sTmp == null ) throw new RuntimeException("Missing attribute 'id'.");
			else item.setID(sTmp);
			
			sTmp = reader.getAttributeValue("", "resname");
			if ( sTmp != null ) item.setName(sTmp);
			else if ( fallbackToID ) item.setName(item.getID());
			
			sTmp = reader.getAttributeValue("", "restype");
			if ( sTmp != null ) item.setType(sTmp);
			
			// Get the content
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				String name;
				switch ( eventType ) {
				case XMLStreamConstants.START_ELEMENT:
					name = reader.getLocalName();
					if ( "source".equals(name) ) {
						content = new Container();
						storeStartElement();
						processContent(name, true, true, content);
						storeEndElement();
						item.setSource(content);
						sklAfter.data = new StringBuilder();
						sourceDone = true;
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
						String tmp = reader.getText();
						for ( int i=0; i<tmp.length(); i++ ) {
							if ( !Character.isWhitespace(tmp.charAt(i)) ) {
								checkTarget();
								break;
							}
						}
					}
					currentSkl.data.append(reader.getText());
					break;
				case XMLStreamConstants.COMMENT:
					checkTarget();
					currentSkl.data.append("<!--"+ reader.getText() + "-->");
					break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					checkTarget();
					currentSkl.data.append("<?"+ reader.getPITarget() + " "
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
			if ( item.hasNote() ) {
				tmp.append(item.getNote());
				tmp.append("\n---\n");
			}
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					currentSkl.data.append(Util.escapeToXML(reader.getText(), 0, false));
					tmp.append(reader.getText());
					break;
				case XMLStreamConstants.END_ELEMENT:
					String name = reader.getLocalName();
					if ( name.equals("note") ) {
						//TODO: Handle 'annotates', etc.
						item.setNote(tmp.toString());
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
	 * @param, makeInline True to create an array of start-element for in-lined.
	 * This is used to merge later on.
	 * @param content IContainer where to put the content.
	 */
	private void processContent (String tagName,
		boolean store,
		boolean makeInline,
		IContainer content)
	{
		try {
			int id = 0;
			Stack<Integer> idStack = new Stack<Integer>();
			idStack.push(id);
			int eventType;
			String name;
			
			if ( makeInline ) {
				resource.inlineCodes.clear();
			}
			
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					content.append(reader.getText());
					if ( store ) {
						// Make sure it's escaped
						currentSkl.data.append(Util.escapeToXML(reader.getText(), 0, false));
					}
					break;
		
				case XMLStreamConstants.END_ELEMENT:
					name = reader.getLocalName();
					if ( name.equals(tagName) ) {
						return;
					}
					else if ( name.equals("g") ) {
						if ( store ) storeEndElement();
						content.append(
							new CodeFragment(IContainer.CODE_CLOSING, idStack.pop(), name));
						if ( makeInline ) resource.inlineCodes.add(
							new CodeFragment(IContainer.CODE_CLOSING, idStack.pop(), "TESTeg"+name));
					}
					break;
					
				case XMLStreamConstants.START_ELEMENT:
					if ( store ) storeStartElement();
					name = reader.getLocalName();
					if ( name.equals("g") ) {
						idStack.push(++id);
						content.append(
							new CodeFragment(IContainer.CODE_OPENING, id, name));
						if ( makeInline ) resource.inlineCodes.add(
							new CodeFragment(IContainer.CODE_OPENING, id, "TESTsg"+name));
					}
					else if ( name.equals("x") ) {
						appendCode(IContainer.CODE_ISOLATED, ++id, name, store, makeInline, content);
					}
					else if ( name.equals("bpt") ) {
						idStack.push(++id);
						appendCode(IContainer.CODE_OPENING, id, name, store, makeInline, content);
					}
					else if ( name.equals("ept") ) {
						appendCode(IContainer.CODE_CLOSING, idStack.pop(), name, store, makeInline, content);
					}
					else if ( name.equals("ph") ) {
						appendCode(IContainer.CODE_ISOLATED, ++id, name, store, makeInline, content);
					}
					else if ( name.equals("it") ) {
						appendCode(IContainer.CODE_ISOLATED, ++id, name, store, makeInline, content);
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
	 */
	private void appendCode (int type,
		int id,
		String tagName,
		boolean store,
		boolean makeInline,
		IContainer content)
	{
		try {
			StringBuilder tmp = new StringBuilder();
			if ( makeInline ) {
				tmp.append("<"+tagName);
				int count = reader.getAttributeCount();
				String prefix;
				for ( int i=0; i<count; i++ ) {
					if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
					prefix = reader.getAttributePrefix(i); 
					tmp.append(String.format(" %s%s=\"%s\"",
						(((prefix==null)||(prefix.length()==0)) ? "" : prefix+":"),
						reader.getAttributeLocalName(i),
						reader.getAttributeValue(i)));
				}
				tmp.append(">");
			}
			int eventType;
			while ( reader.hasNext() ) {
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.END_ELEMENT:
					if ( store ) storeEndElement();
					if ( tagName.equals(reader.getLocalName()) ) {
						if ( makeInline ) tmp.append("</"+tagName+">");
						content.append(new CodeFragment(type, id, tmp.toString()));
						if ( makeInline ) resource.inlineCodes.add(
							new CodeFragment(type, id, tmp.toString()));
						return;	
					}
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					tmp.append(reader.getText());
					if ( store ) {
						// Make sure it's escaped
						currentSkl.data.append(Util.escapeToXML(reader.getText(), 0, false));
					}
					break;
				}
			}
		}
		catch ( XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void processTarget () {
		if ( targetDone ) {
			// Case where this entry is not the main one, but from an alt-trans
			Container tmpCont = new Container();
			processContent("target", true, false, tmpCont);
			return;
		}
		
		item.setTarget(new Container());
		String tmp = reader.getAttributeValue("", "state");
		if ( tmp != null ) {
			item.getTarget().setProperty("state", tmp);
			if ( tmp.equals("needs-translation") ) resource.status = STATUS_TOTRANS;
			else if ( tmp.equals("final") ) resource.status = STATUS_OK;
			else if ( tmp.equals("translated") ) resource.status = STATUS_TOEDIT;
			else if ( tmp.equals("needs-review-translation") ) resource.status = STATUS_TOREVIEW;
		}
		content = new Container();
		processContent("target", false, false, content);
		resource.needTargetElement = false;
		if ( !item.isEmpty() && !content.isEmpty() )
			item.setTarget(content);
		else
			item.setTarget(null);
	}
	
}
