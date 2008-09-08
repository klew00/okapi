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

package net.sf.okapi.filters.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class OutputFilter implements IOutputFilter {
	
	private Resource                        res;
	private OutputStream                    output;
	private DocumentBuilderFactory          fact;
	private DocumentBuilder                 docBuilder;
	private HashMap<String, TextContainer>  subItems;
	private final Logger                    logger = LoggerFactory.getLogger("net.sf.okapi.logging");


	public void close () {
		// Nothing to do
	}

	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage)
	{
		try {
			this.output = output;
			fact = DocumentBuilderFactory.newInstance();
			docBuilder = fact.newDocumentBuilder();
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
	}

	public void endContainer (Group resource) {
	}

	private void buildContent (Node node,
		String content)
		throws SAXException, IOException
	{
		// Remove the part of the existing content that is
		// marked with 'toDel' user-data flag
		boolean foundNodeToDelete = false;
		Node beforeNode = null;
		Node deleteNode = null;
		Node currentNode = node.getFirstChild();
		while ( currentNode != null ) {
			if ( currentNode.getUserData("toDel") == null ) {
				if ( foundNodeToDelete ) { // Deletion done, stop here
					beforeNode = currentNode;
					break;
				}
				else { // No deletion done yet, keep looking for the first one
					currentNode = currentNode.getNextSibling();
				}
			}
			else { // Do the deletion, set the flag
				deleteNode = currentNode;
				currentNode = currentNode.getNextSibling();
				node.removeChild(deleteNode);
				foundNodeToDelete = true;
			}
		}

		org.w3c.dom.Document doc = node.getOwnerDocument();
		DocumentFragment df = parseXMLString(doc, content);

		if ( df.hasChildNodes() ) {
			// If beforeNode is null, insertBefore does an append(),
			// which is what we need.
			node.insertBefore(df.removeChild(df.getFirstChild()), beforeNode);
			while ( df.hasChildNodes() ) {
				node.appendChild(df.removeChild(df.getFirstChild()));
			}
		}
	}
	
	public void endExtractionItem (TextUnit item) {
		processTU(item);
		for ( TextUnit tu : item.childTextUnitIterator() ) {
			processTU(tu);
		}
	}
	
	public void processTU (TextUnit tu) {
		if ( tu.isTranslatable() ) {
			if ( tu.hasTarget() ) {
				// Check for attribute sub-item case
				String siFlag = tu.getProperty("subItem");
				if ( siFlag != null ) {
					if ( siFlag.startsWith(XMLReader.ILMARKER) ) {
						// This is an item extracted from an in-line code
						// We have to wait the parent to be merged to set it
						subItems.put(tu.getID(), tu.getTargetContent());
					}
					else { // Not in in-line code, set it now
						Element elem = (Element)res.srcNode;
						Attr attr = elem.getAttributeNode(siFlag);
						if ( attr != null ) attr.setNodeValue(tu.getTargetContent().toString());
						else {
							logger.warn(String.format("Cannot found mergeable attribute '%s' in item id='%s'",
								siFlag, tu.getID()));
						}
					}
				}
				else {
					String tmp = makeXMLString(tu.getTargetContent());
					// Merge items extracted from in-line codes if there are any
					if ( subItems.size() > 0 ) {
						Iterator<String> iter = subItems.keySet().iterator();
						while ( iter.hasNext() ) {
							String id = iter.next();
							String mark = XMLReader.ILMARKER + id;
							TextContainer cont = subItems.get(id);
							if ( tmp.indexOf(mark) > -1 ) {
								//TODO: We risk double-escape if the sub-item has in-line code
								tmp = tmp.replace(mark, Util.escapeToXML(
									cont.toString(), 3, false));
							}
							else {
								logger.warn(String.format("Cannot found marker for sub-item id='%s' in item id='%s'",
									id, tu.getID()));
							}
						}
						subItems.clear();
					}
					// Merge the content
					try {
						buildContent(res.srcNode, tmp);
					}
					catch ( IOException e ) {
						logger.error(String.format("Problem in new content of item id='%s'.",
							tu.getID()), e);
					}
					catch ( SAXException e ) {
						logger.error(String.format("Problem in new content of item id='%s'.",
							tu.getID()), e);
					}
				}
			}
		}
	}
	
	public void endResource (Document resource) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(res.doc), new StreamResult(output));
		}
		catch ( TransformerConfigurationException e ) {
			throw new RuntimeException(e);
		}
		catch ( TransformerException e ) {
			throw new RuntimeException(e);
		}
	}

	public void startContainer (Group resource) {
	}

	public void startExtractionItem (TextUnit item) {
	}

	public void startResource (Document resource) {
		res = (Resource)resource;
		subItems = new HashMap<String, TextContainer>();
	}

    public void skeletonContainer (SkeletonUnit resource) {
    }
    
	private String makeXMLString (TextContainer item) {
		String text = item.getCodedText();
		StringBuilder tmp = new StringBuilder();
		Code code;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				code = item.getCode(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(code.getData());
				break;
			case '>':
				tmp.append("&gt;");
				break;
			case '<':
				tmp.append("&lt;");
				break;
			case '&':
				tmp.append("&amp;");
				break;
			case '"':
				tmp.append("&quot;");
				break;
			case '\'':
				tmp.append("&apos;");
				break;
			default:
				tmp.append(text.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}
	
	private DocumentFragment parseXMLString (org.w3c.dom.Document doc,
		String fragment)
		throws SAXException, IOException
	{
		// Make sure we have boundaries
		fragment = "<F>"+fragment+"</F>";

		// Parse the fragment
		org.w3c.dom.Document tmpDoc = docBuilder.parse(
			new InputSource(new StringReader(fragment)));
		// Import the nodes of the new document into the destination
		// document so that they will be compatible with the it
		Node node = doc.importNode(tmpDoc.getDocumentElement(), true);
		// Create the document fragment node to hold the new nodes
		DocumentFragment docfrag = doc.createDocumentFragment();
        // Move the nodes into the fragment
		while ( node.hasChildNodes() ) {
			docfrag.appendChild(node.removeChild(node.getFirstChild()));
		}
		return docfrag;
    }
	
}
