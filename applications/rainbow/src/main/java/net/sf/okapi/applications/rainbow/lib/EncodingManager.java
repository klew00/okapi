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

package net.sf.okapi.applications.rainbow.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EncodingManager {
	
	private ArrayList<EncodingItem> items;
	
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
	
	public void loadList (InputStream is) {
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			parseDoc(Fact.newDocumentBuilder().parse(is));			
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		catch ( SAXException e ) {
			throw new RuntimeException(e);
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void loadList (String p_sPath) {
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			parseDoc(Fact.newDocumentBuilder().parse(new File(p_sPath)));			
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		catch ( SAXException e ) {
			throw new RuntimeException(e);
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void parseDoc(Document doc) {
		NodeList NL = doc.getElementsByTagName("encoding");
		items.clear();
		EncodingItem item;
		
		for ( int i=0; i<NL.getLength(); i++ ) {
			Node N = NL.item(i).getAttributes().getNamedItem("iana");
			if ( N == null ) throw new RuntimeException("The attribute 'iana' is missing.");
			item = new EncodingItem();
			item.ianaName = Util.getTextContent(N);
			N = NL.item(i).getAttributes().getNamedItem("cp");
			if ( N == null ) item.codePage = -1;
			else item.codePage = Integer.valueOf(Util.getTextContent(N));
			N = NL.item(i).getFirstChild();
			while ( N != null ) {
				if (( N.getNodeType() == Node.ELEMENT_NODE )
					&& ( N.getNodeName().equals("name") )) {
					item.name = Util.getTextContent(N);
					break;
				}
				else N = N.getNextSibling();
			}
			if ( item.name == null ) throw new RuntimeException("The element 'name' is missing.");
			items.add(item);
		}
	}
}
