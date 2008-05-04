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

package net.sf.okapi.Package.ttx;

import java.io.File;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Filter.InlineCode;

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

	private IFilterItem      sourceItem;
	private IFilterItem      targetItem;
	private IFilterItem      currentItem;
	private int              status;
	private NodeList         nodeList = null;
	private Node             node;
	private Stack<Boolean>   m_stkFirstChildDone;
	private Pattern          idPattern;
	private int              tuvType;
	private int              inline;

	//TODO: Implement case for multiple file in single doc
	public TTXReader () {
		sourceItem = new FilterItem();
		targetItem = new FilterItem();
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

	public IFilterItem getSourceItem () {
		return sourceItem;
	}

	public IFilterItem getTargetItem () {
		return targetItem;
	}

	public void open (String p_sPath)
		throws Exception
	{
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
		catch ( Exception E ) {
			// Reduce all exception to the same, so we can change the internal
			// implementation without changing the API
			throw E;
		}
	}

	public void close () {
		// Make list available for GC
		if ( nodeList != null ) nodeList = null;
	}

	public boolean readItem ()
		throws Exception
	{
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
		sourceItem.reset();
		targetItem.reset();
		status = STATUS_TOTRANS;
	}

	private boolean processUT ()
		throws Exception
	{
		boolean result = false;
		String content = node.getTextContent().trim();
		if ( content.indexOf("<u ") == 0 ) {
			Matcher M = idPattern.matcher(content);
			if ( M.find() ) {
				sourceItem.setTranslatable(false);
				sourceItem.setItemID(Integer.valueOf(
					content.substring(M.start(), M.end())));
			}
			else throw new Exception("ID value not found for <u> element: "+content);
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
		if ( tuvType == 1 ) currentItem = sourceItem;
		else currentItem = targetItem;
		inline = 0;
		processContent("tuv");
	}
	
	private void processContent (String container) {
		while ( nextNode() ) {
			switch ( node.getNodeType() ) {
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				if ( inline == 0 )
					currentItem.appendText(node.getTextContent());
				break;

			case Node.ELEMENT_NODE:
				String sName = node.getNodeName();
				if ( sName.equals(container) ) {
					if ( sName.equals("bpt") ) inline--;
					else if ( sName.equals("ept") ) inline--;
					else if ( sName.equals("ph") ) inline--;
					else if ( sName.equals("g") ) {
						currentItem.appendCode(InlineCode.CLOSING, null, null);
					}
					// End return in all cases
					return;
				}
				
				// Else: It's a start of element
				if ( sName.equals("g") ) {
					currentItem.appendCode(InlineCode.OPENING, null, null);
				}
				else if ( sName.equals("x") ) {
					currentItem.appendCode(InlineCode.ISOLATED, null, null);
				}
				else if ( sName.equals("bpt") ) {
					currentItem.appendCode(InlineCode.OPENING, null, null);
					inline++;
				}
				else if ( sName.equals("ept") ) {
					currentItem.appendCode(InlineCode.CLOSING, null, null);
					inline++;
				}
				else if ( sName.equals("ph") ) {
					currentItem.appendCode(InlineCode.ISOLATED, null, null);
					inline++;
				}
				else if ( sName.equals("it") ) {
					currentItem.appendCode(InlineCode.ISOLATED, null, null);
					inline++;
				}
				break;
			}
		}
	}
}
