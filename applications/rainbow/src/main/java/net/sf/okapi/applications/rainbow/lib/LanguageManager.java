/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util.SUPPORTED_OS;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LanguageManager {
	
	private Vector<LanguageItem> langs;

	public LanguageManager () {
		langs = new Vector<LanguageItem>();
	}
	
	public void loadList (String p_sPath) {
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(new File(p_sPath));
			
			NodeList NL = Doc.getElementsByTagName("language");
			langs.clear();
			LanguageItem LI;
			
			for ( int i=0; i<NL.getLength(); i++ ) {
				Node N = NL.item(i).getAttributes().getNamedItem("code");
				if ( N == null ) throw new RuntimeException("The attribute 'code' is missing.");
				LI = new LanguageItem();
				LI.code = Util.getTextContent(N).toUpperCase();
				N = NL.item(i).getAttributes().getNamedItem("lcid");
				if ( N == null ) LI.lcid = -1;
				else LI.lcid = Integer.valueOf(Util.getTextContent(N));
				N = NL.item(i).getAttributes().getNamedItem("encoding");
				if ( N == null ) LI.setEncoding("UTF-8", SUPPORTED_OS.WINDOWS);
				else LI.setEncoding(Util.getTextContent(N), SUPPORTED_OS.WINDOWS);
				N = NL.item(i).getAttributes().getNamedItem("macEncoding");
				if ( N != null ) LI.setEncoding(Util.getTextContent(N), SUPPORTED_OS.MAC);
				N = NL.item(i).getAttributes().getNamedItem("unixEncoding");
				if ( N != null ) LI.setEncoding(Util.getTextContent(N), SUPPORTED_OS.LINUX);
				
				N = NL.item(i).getFirstChild();
				while ( N != null ) {
					if (( N.getNodeType() == Node.ELEMENT_NODE )
						&& ( N.getNodeName().equals("name") )) {
						LI.name = Util.getTextContent(N);
						break;
					}
					else N = N.getNextSibling();
				}
				if ( LI.name == null ) throw new RuntimeException("The element 'name' is missing.");
				langs.add(LI);
			}
        }
		catch ( SAXException e ) {
			throw new RuntimeException(e);
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public int getCount () {
		return langs.size();
	}
	
	public LanguageItem getItem (int p_nIndex) {
		return langs.get(p_nIndex);
	}

	public LanguageItem GetItem (String p_sCode) {
		for ( int i=0; i<langs.size(); i++ ) {
			if ( p_sCode.equalsIgnoreCase(langs.get(i).code) )
				return langs.get(i);
		}
		return null;
	}

	public int GetLCID (int p_nIndex) {
		return langs.get(p_nIndex).lcid;
	}

	public String GetNameFromCode (String p_sCode) {
		for ( int i=0; i<langs.size(); i++ ) {
			if ( p_sCode.equalsIgnoreCase(langs.get(i).code) )
				return langs.get(i).name;
		}
		return p_sCode; // Return code if not found
	}
	
	public int getIndexFromCode (String p_sCode) {
		if ( p_sCode == null ) return -1;
		for ( int i=0; i<langs.size(); i++ ) {
			if ( p_sCode.equalsIgnoreCase(langs.get(i).code) )
				return i;
		}
		return  -1; // Return -1 if not found
	}
	
	public String getDefaultEncodingFromCode (LocaleId language,
		SUPPORTED_OS osType)
	{
		LanguageItem LI = GetItem(language.toString());
		if ( LI == null ) return "UTF-8";
		return LI.getEncoding(osType);
	}
}
