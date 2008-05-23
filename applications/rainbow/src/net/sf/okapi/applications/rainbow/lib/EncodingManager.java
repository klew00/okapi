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

package net.sf.okapi.applications.rainbow.lib;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EncodingManager {
	
	private ArrayList<EncodingItem>    items;
	
	public EncodingManager () {
		items = new ArrayList<EncodingItem>();
	}
	
	public int getCount () {
		return items.size();
	}
	
	public EncodingItem getItem (int index) {
		return items.get(index);
	}

	public int getIndexFromIANAName (String ianaName) {
		int i = 0;
		for ( EncodingItem item : items ) {
			if ( ianaName.equalsIgnoreCase(item.ianaName) ) return i;
			i++;
		}
		return -1;
	}
	
	public void loadList (String p_sPath)
		throws Exception
	{
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(new File(p_sPath));
			
			NodeList NL = Doc.getElementsByTagName("encoding");
			items.clear();
			EncodingItem item;
			
			for ( int i=0; i<NL.getLength(); i++ ) {
				Node N = NL.item(i).getAttributes().getNamedItem("iana");
				if ( N == null ) throw new Exception("The attribute 'iana' is missing.");
				item = new EncodingItem();
				item.ianaName = N.getTextContent();
				N = NL.item(i).getAttributes().getNamedItem("cp");
				if ( N == null ) item.codePage = -1;
				else item.codePage = Integer.valueOf(N.getTextContent());
				N = NL.item(i).getFirstChild();
				while ( N != null ) {
					if (( N.getNodeType() == Node.ELEMENT_NODE )
						&& ( N.getNodeName().equals("name") )) {
						item.name = N.getTextContent();
						break;
					}
					else N = N.getNextSibling();
				}
				if ( item.name == null ) throw new Exception("The element 'name' is missing.");
				items.add(item);
			}
	    }
		catch ( Exception E ) {
			throw E;
		}
	}
	
}
