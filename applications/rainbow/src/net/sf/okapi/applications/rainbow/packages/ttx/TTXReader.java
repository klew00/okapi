/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.applications.rainbow.packages.ttx;

import java.io.File;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Reads a TTX file, where okapiTTX is the underlying format.
 */
class TTXReader {

	// Same as in Borneo database TSTATUS_* values
	//TODO: need to define this list in a generic class, no Borneo
	public static final int       STATUS_NOTRANS      = 0;
	public static final int       STATUS_UNUSED       = 1;
	public static final int       STATUS_TOTRANS      = 2;
	public static final int       STATUS_TOEDIT       = 3;
	public static final int       STATUS_TOREVIEW     = 4;
	public static final int       STATUS_OK           = 5;

	public IExtractionItem   sourceItem;
	public IExtractionItem   targetItem;
	
	private IContainer       content;
	private NodeList         nodeList = null;
	private Node             node;
	private Stack<Boolean>   m_stkFirstChildDone;
	private Pattern          idPattern;
	private int              tuvType;
	private int              inline;

	//TODO: Implement case for multiple file in single doc
	public TTXReader () {
		sourceItem = new ExtractionItem();
		targetItem = new ExtractionItem();
		idPattern = Pattern.compile("(\\d+)");
	}
	
	protected void finalize ()
		throws Throwable
	{
	    try {
	    	close();
	    } finally {
	        super.finalize();
	    }
	}

	public void open (String p_sPath) {
		try {
			close();
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(new File(p_sPath));
			m_stkFirstChildDone = new Stack<Boolean>();
			m_stkFirstChildDone.push(true); // For #document root
			node = Doc.getDocumentElement();
			m_stkFirstChildDone.push(false);
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public void close () {
		// Make list available for GC
		if ( nodeList != null ) nodeList = null;
	}

	public boolean readItem () {
		resetItem();
		while ( true ) {
			if ( !nextNode() ) {
				return false; // Document is done
			}
			String name = getName();
			if ( name.equals("ut") ) {
				if ( processUT() ) return true;
			}
			else if ( name.equals("Tuv") ) {
				processTUV();
			}
			else if ( name.equals("Tu") ) {
				processTU();
			}
		}
	}

	/**
	 * Gets the name of the current node or an empty string.
	 * @return The name, or an empty string if the node is null or not an element.
	 */
	private String getName () {
		if ( node == null ) return "";
		if ( node.getNodeType() != Node.ELEMENT_NODE ) return "";
		return node.getNodeName();
	}
	
	private boolean nextNode () {
		if ( node != null ) {
			if ( !m_stkFirstChildDone.peek() && node.hasChildNodes() ) {
				// Change the flag for the current node
				m_stkFirstChildDone.push(!m_stkFirstChildDone.pop());
				// Get the new node and push its flag
				node = node.getFirstChild();
				m_stkFirstChildDone.push(false);
			}
			else {
				Node TmpNode = node.getNextSibling();
				if ( TmpNode == null ) {
					node = node.getParentNode();
					m_stkFirstChildDone.pop();
				}
				else {
					node = TmpNode;
					m_stkFirstChildDone.pop(); // Remove flag for previous sibling 
					m_stkFirstChildDone.push(false); // Set new flag for new sibling
				}
			}
		}
		return (node != null);
	}
	
	private void resetItem () {
		sourceItem = new ExtractionItem();
		targetItem = new ExtractionItem();
	}

	private boolean processUT () {
		boolean result = false;
		String content = node.getTextContent().trim();
		if ( content.indexOf("<u ") == 0 ) {
			Matcher M = idPattern.matcher(content);
			if ( M.find() ) {
				sourceItem.setIsTranslatable(false);
				sourceItem.setID(content.substring(M.start(), M.end()));
			}
			else throw new RuntimeException("ID value not found for <u> element: "+content);
		}
		else if ( content.equals("</u>") ) {
			// If <ut> contains a </u> tag, that's the end of the filter item
			result = true;
		}
		// Then move to the closing tag
		while ( true ) {
			if ( !nextNode() || getName().equals("ut") ) 
				return result;
		}
	}

	private void processTU () {
		tuvType = 0;
	}
	
	private void processTUV () {
		tuvType++;
		content = new Container();
		inline = 0;
		processContent("tuv");
		if ( tuvType == 1 ) sourceItem.setContent(content);
		else targetItem.setContent(content);
	}
	
	private void processContent (String container) {
		// Check if it's empty
		if ( !node.hasChildNodes() ) {
			return;
		}
		
		while ( nextNode() ) {
			switch ( node.getNodeType() ) {
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				if ( inline == 0 )
					content.append(node.getTextContent());
				break;

			case Node.ELEMENT_NODE:
				String sName = node.getNodeName();
				if ( sName.equals(container) ) {
					if ( sName.equals("bpt") ) inline--;
					else if ( sName.equals("ept") ) inline--;
					else if ( sName.equals("ph") ) inline--;
					else if ( sName.equals("g") ) {
						content.append(new CodeFragment(IContainer.CODE_CLOSING, 1, sName));
					}
					// End return in all cases
					return;
				}
				
				// Else: It's a start of element
				if ( sName.equals("g") ) {
					content.append(new CodeFragment(IContainer.CODE_OPENING, 1, sName));
				}
				else if ( sName.equals("x") ) {
					content.append(new CodeFragment(IContainer.CODE_ISOLATED, 1, sName));
				}
				else if ( sName.equals("bpt") ) {
					content.append(new CodeFragment(IContainer.CODE_OPENING, 1, sName));
					inline++;
				}
				else if ( sName.equals("ept") ) {
					content.append(new CodeFragment(IContainer.CODE_CLOSING, 1, sName));
					inline++;
				}
				else if ( sName.equals("ph") ) {
					content.append(new CodeFragment(IContainer.CODE_ISOLATED, 1, sName));
					inline++;
				}
				else if ( sName.equals("it") ) {
					content.append(new CodeFragment(IContainer.CODE_ISOLATED, 1, sName));
					inline++;
				}
				break;
			}
		}
	}
}
