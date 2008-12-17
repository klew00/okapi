/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework Contributors                    */
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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.its.ITSEngine;
import org.w3c.its.ITraversal;
import org.xml.sax.SAXException;

public class XMLReader {

	public static final int       RESULT_ENDINPUT          = 0;
	public static final int       RESULT_STARTGROUP        = 1;
	public static final int       RESULT_ENDGROUP          = 2;
	public static final int       RESULT_STARTTRANSUNIT    = 3;
	public static final int       RESULT_ENDTRANSUNIT      = 4;
	
	public static final String    ILMARKER  = "@MRK:";

	protected Resource resource;
	
	private boolean sendEndEvent;
	private TextUnit item;
	private TextContainer content;
	private int itemID;
	private boolean hasText;
	private Node node;
	private ITSEngine itsEng;
	
	public XMLReader () {
		resource = new Resource();
	}
	
	public void open (InputStream input,
		String inputName)
	{
		try {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			fact.setValidating(false);
			resource.setName(inputName);
			resource.doc = fact.newDocumentBuilder().parse(input);
			node = resource.doc.getDocumentElement();
			applyITSRules();
			itsEng.startTraversal();
			sendEndEvent = false;
			itemID = 0;
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
		catch ( SAXException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public TextUnit getItem () {
		return item;
	}

	public int read () {
		if ( sendEndEvent ) {
			sendEndEvent = false;
			return RESULT_ENDTRANSUNIT;
		}
		item = new TextUnit();
		resetStorage(item);

		while ( true ) {
			if ( (node = itsEng.nextNode()) == null ) {
				return RESULT_ENDINPUT; // Document is done
			}
			
			switch ( node.getNodeType() ) {
			case Node.ELEMENT_NODE:
				if ( itsEng.backTracking() ) { // Closing tag
					switch ( itsEng.getWithinText() ) {
					case ITraversal.WITHINTEXT_YES:
						content.append(TagType.CLOSING, node.getLocalName(), tagToString(node, false));
						break;
					//TODO: case ITraversal.WITHINTEXT_NESTED:
					default:
						if ( hasText ) {
							setItemInfo(node);
							return RESULT_STARTTRANSUNIT;
						}
						else {
							resetStorage(item);
						}
						break;
					}
				}
				else { // Start tag
					processAttributes();
					switch ( itsEng.getWithinText() ) {
					case ITraversal.WITHINTEXT_YES:
						if ( node.hasChildNodes() )
							content.append(TagType.OPENING, node.getLocalName(), tagToString(node, true));
						else // Empty element
							content.append(TagType.PLACEHOLDER, node.getLocalName(), tagToString(node, true));
						node.setUserData("toDel", true, null);
						break;
					//TODO: case ITraversal.WITHINTEXT_NESTED:
					default:
						// Finish previous item if needed
						if ( hasText ) {
							setItemInfo(node.getParentNode());
							return RESULT_STARTTRANSUNIT;
						}
						else {
							if ( item.hasChild() ) {
								// Has no content, just one child or more.
								//TODO: Return the item to allow treatment of children
//								item.setIsTranslatable(false);
//								setItemInfo(node.getParentNode());
//								return RESULT_STARTTRANSUNIT;
							}
							resetStorage(item);
						}
						break;
					}
				}
				break;
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				if ( itsEng.translate() ) {
					content.append(node.getNodeValue());
					// Check if we have already some content and change flag if needed
					if ( !hasText ) {
						for ( int i=0; i<node.getNodeValue().length(); i++ ) {
							if ( !Character.isWhitespace(node.getNodeValue().charAt(i)) ) {
								hasText = true;
								break;
							}
						}
					}
				}
				else {
					//TODO: Need to escape text (?)
					content.append(TagType.PLACEHOLDER, node.getNodeName(),
						node.getNodeValue());
				}
				node.setUserData("toDel", true, null);
				break;
			case Node.ENTITY_NODE:
				//TODO: ENTITY_NODE
				break;
			case Node.ENTITY_REFERENCE_NODE:
				//TODO: ENTITY_REFERENCE_NODE
				break;
			case Node.COMMENT_NODE:
				content.append(TagType.PLACEHOLDER, null, "<!--"+node.getNodeValue()+"-->");
				node.setUserData("toDel", true, null);
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				content.append(TagType.PLACEHOLDER, null,
					"<?"+node.getNodeName()+" "+node.getNodeValue()+"?>");
				node.setUserData("toDel", true, null);
				break;
			}
		}
	}

	/**
	 * Check for translatable attributes.
	 */
	private void processAttributes () {
		if ( !node.hasAttributes() ) return; // Fast way out
		NamedNodeMap list = node.getAttributes();
		Attr attr;
		for ( int i=0; i<list.getLength(); i++ ) {
			attr = (Attr)list.item(i);
			if ( itsEng.translate(attr) ) {
				TextUnit attrItem = new TextUnit();
				attrItem.getSourceContent().append(attr.getNodeValue());
				attrItem.setId(String.valueOf(++itemID));
				attrItem.setType("x-attr-"+attr.getNodeName());
				if ( itsEng.getWithinText() == ITraversal.WITHINTEXT_YES ) {
					// For sub-items in in-line codes: Replace the value by a
					// marker so it can be used later for merging (as the node
					// itself will not be available).
					attrItem.setProperty("subItem", String.format("%s%d", ILMARKER, itemID));
					attr.setNodeValue(attrItem.getProperty("subItem"));
				}
				else {
					// For non-inline tag: jut use the attribute name
					attrItem.setProperty("subItem", attr.getNodeName());
				}
				item.addChild(attrItem);
			}
		}
	}
	
	private void resetStorage (TextUnit tu) {
		hasText = false;
		content = new TextContainer(tu);
	}
	
	private void setItemInfo (Node node) {
		if ( node == null ) throw new NullPointerException();
		item.setType("x-"+node.getLocalName());
		item.setSourceContent(content);
		item.setId(String.valueOf(++itemID));
		item.setName(((Element)node).getAttribute("xml:id"));
		sendEndEvent = true;
		resource.srcNode = node;
	}
	
	private void applyITSRules () {
		try {
			URI inputURI = new URI(Util.makeURIFromPath(resource.getName()));
			itsEng = new ITSEngine(resource.doc, inputURI);
		
			// Add any external rules file(s)
			//TODO: Get the info from the parameters
		
			// Apply the all rules (external and internal)
			itsEng.applyRules(ITSEngine.DC_LANGINFO | ITSEngine.DC_TRANSLATE
					| ITSEngine.DC_WITHINTEXT);
		}
		catch ( URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String tagToString (Node element,
		boolean startTag)
	{
		if ( startTag ) {
			StringBuilder tmp = new StringBuilder();
			tmp.append("<" + element.getNodeName());
			if ( element.hasAttributes() ) {
				NamedNodeMap attrs = element.getAttributes();
				for ( int i=0; i<attrs.getLength(); i++ ) {
					Node attr = attrs.item(i);
					tmp.append(" " + attr.getNodeName() + "=\""
						+ Util.escapeToXML(attr.getNodeValue(), 3, false) + "\"");
				}
			}
			if ( node.hasChildNodes() ) tmp.append(">");
			else tmp.append("/>");
			return tmp.toString();
		}
		else {
			return "</" + element.getNodeName() + ">";
		}
	}
}
