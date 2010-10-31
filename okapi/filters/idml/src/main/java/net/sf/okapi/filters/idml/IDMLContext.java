/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.idml;

import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

class IDMLContext {
	
	private int nodeCount;
	private Node startNode;
	private TextFragment tf;

	public IDMLContext (Node startNode,
		int nodeCount)
	{
		this.nodeCount = nodeCount;
		this.startNode = startNode;
		tf = new TextFragment();
	}
	
	/**
	 * Adds the text unit to the given queue.
	 * @param queue the event queue where to add the event.
	 * @param tuIdPrefix the prefix to use for the text unit ID.
	 */
	public void addToQueue (List<Event> queue,
		String tuIdPrefix)
	{
		if ( tf.isEmpty() ) return; // Skip empty entries
		// Otherwise the fragment contains text and possibly codes as needed
		// Just create the text unit
		TextUnit tu = new TextUnit(tuIdPrefix+nodeCount);
		tu.setSourceContent(tf);
		tu.setSkeleton(new IDMLSkeleton(startNode));
		// And add the new event to the queue
		queue.add(new Event(EventType.TEXT_UNIT, tu));
		// This object should not be called again
	}
	
	/**
	 * Adds a Content element to the text unit.
	 * @param elem the Content element node.
	 */
	public void addContent (Element elem) {
		String text = Util.getTextContent(elem);
		tf.append(TagType.OPENING, "code", buildStartTag(elem));
		tf.append(text);
		tf.append(TagType.CLOSING, "code", buildEndTag(elem));
	}
	
	public void addCode (Node node) {
		// Assume text node
		String text = node.getNodeValue();
		for ( int i=0; i<text.length(); i++ ) {
			if ( !Character.isWhitespace(text.charAt(i)) ) {
				tf.append(TagType.PLACEHOLDER, "text", text);
				return;
			}
		}
		// Otherwise: just white spaces: no output
	}
	
	public void addStartTag (Element elem) {
		tf.append(elem.hasChildNodes() ? TagType.OPENING : TagType.PLACEHOLDER,
			elem.getNodeName(), buildStartTag(elem));
	}
	
	public void addEndTag (Element elem) {
		if ( elem.hasChildNodes() ) {
			tf.append(TagType.CLOSING, elem.getNodeName(), buildEndTag(elem));
		}
	}
	
	public String buildStartTag (Element elem) {
		StringBuilder sb = new StringBuilder("<"+elem.getNodeName());
		NamedNodeMap attrNames = elem.getAttributes();
		for ( int i=0; i<attrNames.getLength(); i++ ) {
			Attr attr = (Attr)attrNames.item(i);
			sb.append(" " + attr.getName() + "=\"");
			sb.append(Util.escapeToXML(attr.getValue(), 3, false, null));
			sb.append("\"");
		}
		// Make it an empty element if possible
		if ( elem.hasChildNodes() ) {
			sb.append(">");
		}
		else {
			sb.append("/>");
		}
		return sb.toString();
	}
	
	public String buildEndTag (Element elem) {
		return "</"+elem.getNodeName()+">";
	}

}
