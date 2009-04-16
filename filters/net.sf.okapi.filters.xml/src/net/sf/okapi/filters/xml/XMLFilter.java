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

package net.sf.okapi.filters.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.DefaultEntityResolver;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.BadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.its.IProcessor;
import org.w3c.its.ITSEngine;
import org.w3c.its.ITraversal;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLFilter implements IFilter {

	private String docName;
	private String encoding;
	private String srcLang;
	private String lineBreak;
	private Document doc;
	private ITraversal trav;
	private LinkedList<Event> queue;
	private int tuId;
	private int otherId;
	private TextFragment frag;
	private GenericSkeleton skel;
	private Stack<ContextItem> context;
	private boolean canceled;
	private Parameters params;
	private boolean hasUTF8BOM;

	public XMLFilter () {
		params = new Parameters();
	}
	
	public void cancel () {
		canceled = true;
	}

	public void close () {
	}

	public ISkeletonWriter createSkeletonWriter () {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	public String getName () {
		return "okf_xml";
	}
	
	public String getMimeType () {
		return "text/xml";
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return (queue != null);
	}

	public Event next () {
		if ( canceled ) {
			queue = null;
			return new Event(EventType.CANCELED);
		}
		if ( queue == null ) return null;

		// Process queue if it's not empty yet
		while ( true ) {
			if ( queue.size() > 0 ) {
				Event event = queue.poll();
				if ( event.getEventType() == EventType.END_DOCUMENT ) {
					queue = null;
				}
				return event;
			}

			// Process the next item, filling the queue
			process();
			// Ensure no infinite loop
			if ( queue.size() == 0 ) return null;
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
			throw new BadFilterInputException("RawDocument has no input defined.");
		}
	}
	
	private void open (InputStream input) {
		commonOpen(0, input);
	}

	private void open (CharSequence inputText) {
		encoding = "UTF-16";
		hasUTF8BOM = false;
		lineBreak = BOMNewlineEncodingDetector.getNewlineType(inputText).toString();
		InputSource is = new InputSource(new StringReader(inputText.toString()));
		commonOpen(2, is);
	}

	private void open (URI inputURI) {
		docName = inputURI.getPath();
		commonOpen(1, inputURI);
	}

	private void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLanguage;
		encoding = defaultEncoding;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	/**
	 * Shared open method for the public open() calls.
	 * @param type Indicates the type of obj: 0=InputStream, 1=URI, 2=InputSource.
	 * @param obj The object to read.
	 */
	private void commonOpen (int type,
		Object obj)
	{
		close();
		// Initializes the variables
		canceled = false;
		tuId = 0;
		otherId = 0;

		// Create the document builder factory
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		fact.setNamespaceAware(true);
		fact.setValidating(false);
		
		fact.setExpandEntityReferences(false);
		
		// Create the document builder
		DocumentBuilder docBuilder;
		try {
			docBuilder = fact.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new OkapiIOException(e);
		}
		
		//TODO: Do this only as an option
		// Avoid DTD declaration
		docBuilder.setEntityResolver(new DefaultEntityResolver());
		
		URI uri = null;
		BOMNewlineEncodingDetector detector = null;
		// Load the document
		try {
			
			switch ( type ) {
			case 0: // InputStream
				detector = new BOMNewlineEncodingDetector((InputStream)obj);
				hasUTF8BOM = detector.hasUtf8Bom();
				lineBreak = detector.getNewlineType().toString();
				doc = docBuilder.parse((InputStream)obj);
				break;
			case 1: // URI
				uri = (URI)obj;
				detector = new BOMNewlineEncodingDetector(uri.toURL().openStream());
				hasUTF8BOM = detector.hasUtf8Bom();
				lineBreak = detector.getNewlineType().toString();
				doc = docBuilder.parse(uri.toString());
				break;
			case 2: // InputSource
				doc = docBuilder.parse((InputSource)obj);
				break;
			}
		}
		catch ( SAXException e ) {
			throw new OkapiIOException(e);
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
		finally {
			if ( detector != null ) {
				detector = null; // Release it
			}
		}
		
		// Create the ITS engine
		ITSEngine itsEng;
		itsEng = new ITSEngine(doc, uri);
		// Load the parameters file if there is one
		if ( params != null ) {
			if ( params.getDocument() != null ) {
				itsEng.addExternalRules(params.getDocument(), params.getURI());
			}
		}
		
		// Apply the all rules (external and internal) to the document
		itsEng.applyRules(IProcessor.DC_TRANSLATE | IProcessor.DC_LANGINFO 
			| IProcessor.DC_LOCNOTE | IProcessor.DC_WITHINTEXT);
		
		trav = itsEng;
		trav.startTraversal();
		context = new Stack<ContextItem>();
		
		// Set the start event
		queue = new LinkedList<Event>();

		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		String realEnc = doc.getInputEncoding();
		if ( realEnc != null ) encoding = realEnc;
		startDoc.setEncoding(encoding, hasUTF8BOM);
		startDoc.setLineBreak(lineBreak);
		startDoc.setLanguage(srcLang);
		startDoc.setFilterParameters(getParameters());
		startDoc.setType("text/xml");
		startDoc.setMimeType("text/xml");

		// Add the XML declaration
		skel = new GenericSkeleton();
		skel.add("<?xml version=\"" + doc.getXmlVersion() + "\"");
		skel.add(" encoding=\"");
		skel.addValuePlaceholder(startDoc, Property.ENCODING, "");
		skel.add("\"");
		startDoc.setProperty(new Property(Property.ENCODING, encoding, false));
		if ( doc.getXmlStandalone() ) skel.add(" standalone=\"true\"");
		skel.add("?>"+lineBreak);

		// Add the DTD if needed
		DocumentType dt = doc.getDoctype();
		if ( dt != null ) {
			String tmp;
			if ( dt.getPublicId() != null ) {
				tmp = String.format("<!DOCTYPE %s PUBLIC \"%s\" \"%s\">"+lineBreak,
						dt.getName(),
						dt.getPublicId(),
						dt.getSystemId());
			}
			else {
				tmp = String.format("<!DOCTYPE %s SYSTEM \"%s\">"+lineBreak,
						dt.getName(),
						dt.getSystemId());
			}
			skel.add(tmp);
		}
		
		startDoc.setSkeleton(skel);
		// Put the start document in the queue
		queue.add(new Event(EventType.START_DOCUMENT, startDoc));
	}

	private void process () {
		Node node;
		frag = null;
		skel = new GenericSkeleton();
		
		if ( context.size() > 0 ) {
			// If we are within an element, reset the fragment to append to it
			if ( context.peek().translate ) { // The stack is up-to-date already
				frag = new TextFragment();
			}
		}
		
		while ( true ) {
			node = trav.nextNode();
			if ( node == null ) { // No more node: we stop
				Ending ending = new Ending(String.valueOf(++otherId));
				if (( skel != null ) && ( !skel.isEmpty() )) {
					ending.setSkeleton(skel);
				}
				queue.add(new Event(EventType.END_DOCUMENT, ending));
				return;
			}
			
			// Else: valid node
			switch ( node.getNodeType() ) {
			case Node.CDATA_SECTION_NODE:
				if ( frag == null ) {
					skel.append(buildCDATA(node));
				}
				else {
					if ( trav.translate() ) {
						frag.append(node.getNodeValue());
					}
					else {
						frag.append(TagType.PLACEHOLDER, null, buildCDATA(node));
					}
				}
				break;

			case Node.TEXT_NODE:
				if ( frag == null ) {//TODO: escape unsupported chars
					skel.append(Util.escapeToXML(
						node.getNodeValue().replace("\n", lineBreak), 0, false, null));
				}
				else {
					if ( trav.translate() ) {
						frag.append(node.getNodeValue());
					}
					else {//TODO: escape unsupported chars
						frag.append(TagType.PLACEHOLDER, null, Util.escapeToXML(
							node.getNodeValue().replace("\n", lineBreak), 0, false, null));
					}
				}
				break;
				
			case Node.ELEMENT_NODE:
				if ( processElementTag(node) ) return;
				break;
				
			case Node.PROCESSING_INSTRUCTION_NODE:
				if ( frag == null ) {
					skel.add(buildPI(node));
				}
				else {
					frag.append(TagType.PLACEHOLDER, null, buildPI(node));
				}
				break;
				
			case Node.COMMENT_NODE:
				if ( frag == null ) {
					skel.add(buildComment(node));
				}
				else {
					frag.append(TagType.PLACEHOLDER, null, buildComment(node));
				}
				break;
				
			case Node.NOTATION_NODE:
				//TODO: handle notations
				break;
				
			case Node.DOCUMENT_TYPE_NODE:
				// Handled in the start document process
				break;
			}
		}
	}

	private void addStartTagToSkeleton (Node node) {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<"
			+ ((node.getPrefix()==null) ? "" : node.getPrefix()+":")
			+ node.getLocalName());
		if ( node.hasAttributes() ) {
			NamedNodeMap list = node.getAttributes();
			Attr attr;
			for ( int i=0; i<list.getLength(); i++ ) {
				attr = (Attr)list.item(i);
				if ( !attr.getSpecified() ) continue; // Skip auto-attributes
				tmp.append(" "
					+ ((attr.getPrefix()==null) ? "" : attr.getPrefix()+":")
					+ attr.getLocalName() + "=\"");
				// Extract if needed
				if (( trav.translate(attr) ) && ( attr.getValue().length() > 0 )) {
					// Store the text part and reset the buffer
					skel.append(tmp.toString());
					tmp.setLength(0);
					// Create the TU
					addAttributeTextUnit(attr, true);
					tmp.append("\"");
				}
				else if ( attr.getName().equals("xml:lang") ) {
					//String x = attr.getValue();
					//TODO: handle xml:lang
					tmp.append(Util.escapeToXML(attr.getNodeValue(), 3, false, null)
						+ "\"");
				}
				else { //TODO: escape unsupported chars
					tmp.append(Util.escapeToXML(attr.getNodeValue(), 3, false, null)
						+ "\"");
				}
			}
		}
		if ( !node.hasChildNodes() ) tmp.append("/");
		tmp.append(">");
		skel.append(tmp.toString());
	}

	private void addStartTagToFragment (Node node) {
		StringBuilder tmp = new StringBuilder();
		String id = null;
		tmp.append("<"
			+ ((node.getPrefix()==null) ? "" : node.getPrefix()+":")
			+ node.getLocalName());
		if ( node.hasAttributes() ) {
			NamedNodeMap list = node.getAttributes();
			Attr attr;
			for ( int i=0; i<list.getLength(); i++ ) {
				attr = (Attr)list.item(i);
				if ( !attr.getSpecified() ) continue; // Skip auto-attributes
				tmp.append(" "
					+ ((attr.getPrefix()==null) ? "" : attr.getPrefix()+":")
					+ attr.getLocalName() + "=\"");
				// Extract if needed
				if (( trav.translate(attr) ) && ( attr.getValue().length() > 0 )) {
					id = addAttributeTextUnit(attr, false);
					tmp.append(TextFragment.makeRefMarker(id));
					tmp.append("\"");
				}
				else if ( attr.getName().equals("xml:lang") ) { // xml:lang
					if ( attr.getValue().equalsIgnoreCase(srcLang) ) {
						//TODO
					}
					tmp.append(Util.escapeToXML(attr.getNodeValue(), 3, false, null)
						+ "\"");
				}
				else { //TODO: escape unsupported chars
					tmp.append(Util.escapeToXML(attr.getNodeValue(), 3, false, null)
						+ "\"");
				}
			}
		}
		if ( !node.hasChildNodes() ) tmp.append("/");
		tmp.append(">");
		// Set the inline code
		Code code = frag.append(TagType.OPENING, node.getLocalName(), tmp.toString());
		code.setReferenceFlag(id!=null); // Set reference flag if we created TU(s)
	}

	private String addAttributeTextUnit (Attr attr,
		boolean addToSkeleton)
	{
		String id = String.valueOf(++tuId);
		TextUnit tu = new TextUnit(id, attr.getValue(), true, "text/xml");
		queue.add(new Event(EventType.TEXT_UNIT, tu));
		if ( addToSkeleton ) skel.addReference(tu);
		return id;
	}
	
	private String buildEndTag (Node node) {
		if ( node.hasChildNodes() ) {
			return "</"
				+ ((node.getPrefix()==null) ? "" : node.getPrefix()+":")
				+ node.getLocalName() + ">";
		}
		else { // Start tag was set as an empty element
			return "";
		}
	}
	
	private String buildPI (Node node) {
		// Do not escape PI content
		return "<?" + node.getNodeName() + " " + node.getNodeValue() + "?>";
	}
	
	private String buildCDATA (Node node) {
		// Do not escape CDATA content
		return "<![CDATA[" + node.getNodeValue().replace("\n", lineBreak) + "]]>";
	}
	
	private String buildComment (Node node) {
		// Do not escape comments
		return "<!--" + node.getNodeValue().replace("\n", lineBreak) + "-->";
	}

	/**
	 * Processes the start or end tag of an element node.
	 * @param node Node to process.
	 * @return True if we need to return, false to continue processing.
	 */
	private boolean processElementTag (Node node) {
		if ( trav.backTracking() ) {
			if ( frag == null ) { // Not an extraction: in skeleton
				skel.add(buildEndTag(node));
				if ( node.isSameNode(context.peek().node) ) context.pop();
				if ( isContextTranslatable() ) { // We are after non-translatable withinText='no', check parent again.
					frag = new TextFragment();
				}
			}
			else { // Else we are within an extraction
				if ( node.isSameNode(context.peek().node) ) { // End of possible text-unit
					return addTextUnit(node, true);
				}
				else { // Within text
					frag.append(TagType.CLOSING, node.getLocalName(), buildEndTag(node));
				}
			}
		}
		else { // Else: Start tag
			switch ( trav.getWithinText() ) {
			case ITraversal.WITHINTEXT_YES:
			case ITraversal.WITHINTEXT_NESTED: //TODO: deal with nested elements
				if ( frag == null ) { // Not yet in extraction
					// Strange case: inline without parent???
					//TODO: do something about this, warning?
					assert(false);
				}
				else { // Already in extraction
					//frag.append(TagType.OPENING, node.getLocalName(), buildStartTag(node));
					addStartTagToFragment(node);
				}
				break;
			default: // Not within text
				if ( frag == null ) { // Not yet in extraction
					addStartTagToSkeleton(node);
					if ( trav.translate() ) {
						frag = new TextFragment();
					}
					if ( node.hasChildNodes() ) {
						context.push(new ContextItem(node, trav));
					}
				}
				else { // Already in extraction
					// Queue the current item
					addTextUnit(node, false);
					addStartTagToSkeleton(node);
					// And create a new one
					if ( trav.translate() ) {
						frag = new TextFragment();
					}
					if ( node.hasChildNodes() ) {
						context.push(new ContextItem(node, trav));
					}
				}
				break;
			}
		}
		return false;
	}

	private boolean isContextTranslatable () {
		if ( context.size() == 0 ) return false;
		return context.peek().translate;
	}
	
	/**
	 * Adds a text unit to the queue if needed.
	 * @param node The current node.
	 * @param popStack True to pop the stack, false to leave the stack alone.
	 * @return True if a text unit was added to the queue, false otherwise.
	 */
	private boolean addTextUnit (Node node,
		boolean popStack)
	{
		// Create a unit only if needed
		if ( !frag.hasCode() && !frag.hasText(false) ) {
			if ( !frag.isEmpty() ) { // Nothing but white spaces
				skel.add(frag.toString().replace("\n", lineBreak)); // Pass them as skeleton
			}
			frag = null;
			if ( popStack ) {
				context.pop();
				skel.add(buildEndTag(node));
			}
			return false;
		}
		// Create the unit
		TextUnit tu = new TextUnit(String.valueOf(++tuId));
		tu.setMimeType("text/xml");
		tu.setSourceContent(frag);

		String locNote = context.peek().locNote;
		if ( locNote != null ) {
			//TODO: implement real notes
			tu.setProperty(new Property(Property.NOTE, locNote));
		}
		
		if ( context.peek().preserveWS ) {
			tu.setPreserveWhitespaces(true);
		}
		else {
			tu.setPreserveWhitespaces(false);
			TextFragment.unwrap(tu.getSourceContent());
		}
		
		skel.addContentPlaceholder(tu);
		if ( popStack ) {
			context.pop();
			skel.add(buildEndTag(node));
		}
		tu.setSkeleton(skel);
		queue.add(new Event(EventType.TEXT_UNIT, tu));
		frag = null;
		skel = new GenericSkeleton();
		return true;
	}
	
}
