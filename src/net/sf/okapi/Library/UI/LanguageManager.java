/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Library.UI;

import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.Library.Base.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LanguageManager {
	
	private Vector<LanguageItem>  m_aLangs;

	public LanguageManager () {
		m_aLangs = new Vector<LanguageItem>();
	}
	
	public void loadList (String p_sPath)
		throws Exception
	{
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(new File(p_sPath));
			
			NodeList NL = Doc.getElementsByTagName("language");
			m_aLangs.clear();
			LanguageItem LI;
			
			for ( int i=0; i<NL.getLength(); i++ ) {
				Node N = NL.item(i).getAttributes().getNamedItem("code");
				if ( N == null ) throw new Exception("The attribute 'code' is missing.");
				LI = new LanguageItem();
				String sCode = N.getTextContent();
				LI.setCode(sCode);
				N = NL.item(i).getAttributes().getNamedItem("lcid");
				if ( N == null ) LI.setLCID(-1);
				else LI.setLCID(Integer.valueOf(N.getTextContent()));
				N = NL.item(i).getAttributes().getNamedItem("sourceEncoding");
				if ( N == null ) LI.setEncoding("UTF-8", Utils.PFTYPE_WIN);
				else LI.setEncoding(N.getTextContent(), Utils.PFTYPE_WIN);
				N = NL.item(i).getAttributes().getNamedItem("macEncoding");
				if ( N != null ) LI.setEncoding(N.getTextContent(), Utils.PFTYPE_MAC);
				N = NL.item(i).getAttributes().getNamedItem("unixEncoding");
				if ( N != null ) LI.setEncoding(N.getTextContent(), Utils.PFTYPE_UNIX);
				
				N = NL.item(i).getFirstChild();
				while ( N != null ) {
					if (( N.getNodeType() == Node.ELEMENT_NODE )
						&& ( N.getNodeName().equals("name") )) {
						LI.setName(N.getTextContent());
						break;
					}
					else N = N.getNextSibling();
				}
				if ( LI.getName() == null ) throw new Exception("The element 'name' is missing.");
				m_aLangs.add(LI);
			}
        }
		catch ( Exception E ) {
			throw E;
		}
	}
	
	public int getCount () {
		return m_aLangs.size();
	}
	
	public LanguageItem getItem (int p_nIndex) {
		return m_aLangs.get(p_nIndex);
	}

	public LanguageItem GetItem (String p_sCode) {
		for ( int i=0; i<m_aLangs.size(); i++ ) {
			if ( p_sCode.equalsIgnoreCase(m_aLangs.get(i).getCode()) )
				return m_aLangs.get(i);
		}
		return null;
	}

	public int GetLCID (int p_nIndex) {
		return m_aLangs.get(p_nIndex).getLCID();
	}

	public String GetNameFromCode (String p_sCode) {
		for ( int i=0; i<m_aLangs.size(); i++ ) {
			if ( p_sCode.equalsIgnoreCase(m_aLangs.get(i).getCode()) )
				return m_aLangs.get(i).getName();
		}
		return p_sCode; // Return code if not found
	}
	
	public String getDefaultEncodingFromCode (String p_sCode,
		int p_nPFType)
	{
		LanguageItem LI = GetItem(p_sCode);
		if ( LI == null ) return "UTF-8";
		return LI.getEncoding(p_nPFType);
	}
}
