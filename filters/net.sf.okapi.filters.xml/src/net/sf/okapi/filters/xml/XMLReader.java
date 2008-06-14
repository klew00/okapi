package net.sf.okapi.filters.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;

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

	protected Resource       resource;
	
	private boolean          sendEndEvent;
	private IExtractionItem  item;
	private IContainer       content;
	private int              itemID;
	private boolean          hasText;
	private Node             node;
	private ITSEngine        itsEng;
	private int              codeID;
	private Stack<Integer>   codeIDStack;
	
	
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
			codeIDStack = new Stack<Integer>();
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

	public IExtractionItem getItem () {
		return item;
	}

	public int read () {
		if ( sendEndEvent ) {
			sendEndEvent = false;
			return RESULT_ENDTRANSUNIT;
		}
		item = new ExtractionItem();
		resetStorage();

		while ( true ) {
			if ( (node = itsEng.nextNode()) == null ) {
				return RESULT_ENDINPUT; // Document is done
			}
			
			switch ( node.getNodeType() ) {
			case Node.ELEMENT_NODE:
				if ( itsEng.backTracking() ) { // Closing tag
					switch ( itsEng.getWithinText() ) {
					case ITraversal.WITHINTEXT_YES:
						content.append(new CodeFragment(IContainer.CODE_CLOSING, codeIDStack.pop(),
							tagToString(node, false), node));
						break;
					//TODO: case ITraversal.WITHINTEXT_NESTED:
					default:
						if ( hasText ) {
							setItemInfo(node);
							return RESULT_STARTTRANSUNIT;
						}
						else {
							resetStorage();
						}
						break;
					}
				}
				else { // Start tag
					//TODO: Deal with empty elements!
					processAttributes();
					switch ( itsEng.getWithinText() ) {
					case ITraversal.WITHINTEXT_YES:
						if ( node.hasChildNodes() )
							content.append(new CodeFragment(IContainer.CODE_OPENING, codeIDStack.push(++codeID),
								tagToString(node, true), node));
						else // Empty element
							content.append(new CodeFragment(IContainer.CODE_ISOLATED, ++codeID,
								tagToString(node, true), node));
						break;
					//TODO: case ITraversal.WITHINTEXT_NESTED:
					default:
						// Finish previous item if needed
						if ( hasText ) {
							setItemInfo(node.getParentNode());
							return RESULT_STARTTRANSUNIT;
						}
						else {
							resetStorage();
						}
						break;
					}
				}
				break;
			case Node.TEXT_NODE:
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
					content.append(new CodeFragment(IContainer.CODE_ISOLATED, ++codeID,
						node.getNodeValue(), node));
				}
				break;
			case Node.CDATA_SECTION_NODE:
				//TODO: CDATA_SECTION_NODE
				break;
			case Node.ENTITY_NODE:
				//TODO: ENTITY_NODE
				break;
			case Node.ENTITY_REFERENCE_NODE:
				//TODO: ENTITY_REFERENCE_NODE
				break;
			case Node.COMMENT_NODE:
				content.append(new CodeFragment(IContainer.CODE_ISOLATED, ++codeID,
					"<!--"+node.getNodeValue()+"-->", node));
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				//TODO: PIs
				content.append(new CodeFragment(IContainer.CODE_ISOLATED, ++codeID,
					node.getNodeValue(), node));
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
		Node attr;
		for ( int i=0; i<list.getLength(); i++ ) {
			attr = list.item(i);
			if ( itsEng.translate(attr.getNodeName()) ) {
				ExtractionItem attrItem = new ExtractionItem();
				attrItem.getSource().setContent(attr.getNodeValue());
				attrItem.setID(String.valueOf(++itemID));
				attrItem.setType("x-attr-"+attr.getNodeName());
				//TODO: Find another way! attrItem.setData(attr);
				if ( itsEng.getWithinText() == ITraversal.WITHINTEXT_YES ) {
					// For sub-items in in-line codes: Replace the value by a
					// marker so it can be used later for merging (as the node
					// itself will not be available).
					attr.setNodeValue(String.format("%s%d", ILMARKER, itemID));
				}
				item.addChild(attrItem);
			}
		}
	}
	
	private void resetStorage () {
		codeID = 0;
		codeIDStack.clear();
		codeIDStack.push(codeID);
		hasText = false;
		content = new Container();
	}
	
	private void setItemInfo (Node node) {
		if ( node == null ) throw new NullPointerException();
		item.setType("x-"+node.getLocalName());
		item.setSource(content);
		item.setID(String.valueOf(++itemID));
		item.setName(((Element)node).getAttribute("xml:id"));
		sendEndEvent = true;
		resource.srcNode = node;
	}
	
	private void applyITSRules () {
		itsEng = new ITSEngine(resource.doc, resource.getName());
		
		// Add any external rules file(s)
		//TODO: Get the info from the parameters
		
		// Apply the all rules (external and internal)
		itsEng.applyRules(ITSEngine.DC_LANGINFO | ITSEngine.DC_TRANSLATE
			| ITSEngine.DC_WITHINTEXT);
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
