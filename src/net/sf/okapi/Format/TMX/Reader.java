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

package net.sf.okapi.Format.TMX;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Filter.InlineCode;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Utils;

/**
 * Reads a bilingual TMX document.
 * The reader does not support <sub> elements.
 * The reader supports cases where several translations in the same language
 * exist for one source.
 */
public class Reader {

	//private static final String   NSXML = "http://www.w3.org/XML/1998/namespace";
	
	public static final int       RESULT_ERROR        = 0;
	public static final int       RESULT_ENDOFDOC     = 1;
	public static final int       RESULT_ITEM         = 2;
	
	private NodeList         m_NL;
	private int              m_nCurTU;
	private IFilterItem      m_SrcFI;
	private IFilterItem      m_CurrentFI;
	private boolean          m_bAdd;
	private ILog             m_Log;
	private String           m_sVersion;
	private String           m_sLang;
	private String           m_sMainSrcLang;
	private String           m_sSrcLang;
	private String           m_sReqSrcLang;
	private String           m_sReqTrgLang;
	private boolean          m_bSrc;
	private Vector<Item>     m_aItems;
	private int              m_nTargetIndex;

	public Reader (ILog p_Log) {
		m_Log = p_Log;
		m_SrcFI = new FilterItem();
		m_aItems = new Vector<Item>();
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

	public String getVersion () {
		return m_sVersion;
	}

	/**
	 * Opens a TMX document for reading.
	 * @param p_sPath The path of the document to read.
	 * @param p_sSrcLang The code of the source language requested.
	 * Use null to request the source language specified in the header element.
	 * @param p_sTrgLang The code of the target language requested.
	 * Use null to request the first non-source language found in the document.
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public void open (String p_sPath,
		String p_sSrcLang,
		String p_sTrgLang)
		throws Exception
	{
		try {
			close();
			
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(new File(p_sPath));

			Element Elem = Doc.getDocumentElement(); // tmx
			processTMX(Elem);
			
			Elem = (Element)Doc.getFirstChild(); // get header
			processHeader(Elem);
			
			Elem = (Element)Elem.getNextSibling(); // get body
			m_nCurTU = -1;
			m_NL = Elem.getElementsByTagName("tu");
			
			m_sReqSrcLang = p_sSrcLang;
			m_sReqTrgLang = p_sTrgLang;
		}
		catch ( Exception E ) {
			throw E;
		}
	}
	
	public void close ()
	{
		if ( m_NL != null ) m_NL = null;
		reset();
	}
	
	public IFilterItem getSource () {
		return m_SrcFI;
	}

	/**
	 * Gets the number of target entries the current TU has.
	 * @return the number of non-source TUV.
	 */
	public int getTargetCount () {
		return m_aItems.size();
	}
	
	public Item getNextTarget () {
		if ( m_nTargetIndex+1 < m_aItems.size() ) {
			m_nTargetIndex++;
			return m_aItems.elementAt(m_nTargetIndex); 
		}
		return null;
	}
	
	public String getSourceLanguage () {
		return m_sSrcLang;
	}

	public String getRequestedSourceLanguage () {
		return m_sReqSrcLang;
	}

	public String getRequestedTargetLanguage () {
		return m_sReqTrgLang;
	}

	/**
	 * Reads the next TUV element in the document.
	 * @return 0=Error, 1=OK, 2=End of document
	 */
	public int readItem ()
	{
		//TODO: Implement this reader with DOM
		try {
			m_bAdd = false;
			if ( m_nCurTU+1 >= m_NL.getLength() ) {
				return RESULT_ENDOFDOC;
			}
			Element Elem = (Element)m_NL.item(++m_nCurTU);
			traverseNode(Elem);
			return RESULT_ITEM;
		}
		catch ( Exception E ) {
			m_Log.error(E.getMessage());
		}
		return RESULT_ERROR;
	}
	
	private void traverseNode (Node p_Node)
		throws Exception
	{
		if ( p_Node == null ) return;
		
		switch ( p_Node.getNodeType() ) {
		case Node.ELEMENT_NODE:
			if ( p_Node.getNodeName().equalsIgnoreCase("tu") )
				processTU((Element)p_Node);
			else if ( p_Node.getNodeName().equalsIgnoreCase("tuv") )
				processTUV((Element)p_Node);
			else if ( p_Node.getNodeName().equalsIgnoreCase("seg") )
				processSeg((Element)p_Node);
			else if ( p_Node.getNodeName().equalsIgnoreCase("bpt") ) {
				m_CurrentFI.appendCode(InlineCode.OPENING, null, null);
			}
			else if ( p_Node.getNodeName().equalsIgnoreCase("ept") ) {
				m_CurrentFI.appendCode(InlineCode.CLOSING, null, null);
			}
			else if ( p_Node.getNodeName().equalsIgnoreCase("ph") ) {
				m_CurrentFI.appendCode(InlineCode.ISOLATED, null, null);
			}
			else if ( p_Node.getNodeName().equalsIgnoreCase("it") ) {
				m_CurrentFI.appendCode(InlineCode.ISOLATED, null, null);
			}
			break;
		case Node.TEXT_NODE:
			if ( m_bAdd ) m_CurrentFI.appendText(p_Node.getTextContent());
			break;
		}
		
		// Move to next node
		if ( p_Node.hasChildNodes() ) {
			traverseNode(p_Node.getFirstChild());
		}
		traverseNode(p_Node.getNextSibling());
	}
	
	
	private void reset () {
		m_aItems.clear();
		m_SrcFI.reset();
		m_nTargetIndex = -1;
	}

	private void processSeg (Element p_Elem)
	{
		m_bAdd = true;
	}
	
	private void processTU (Element p_Elem) {
		reset();
		String sTmp = p_Elem.getAttribute("tuid");
		if ( sTmp != null ) {
			m_SrcFI.setResName(sTmp);
		}
		sTmp = p_Elem.getAttribute("srclang");
		if ( sTmp != null ) m_sSrcLang = sTmp;
		else m_sSrcLang = m_sMainSrcLang;
	}

	private void processTUV (Element p_Elem)
		throws Exception
	{
		// Get the language of this TUV
		m_sLang = p_Elem.getAttribute("xml:lang");
		if ( m_sLang == null ) {
			m_sLang = p_Elem.getAttribute("lang"); // For old TMX versions
			if ( m_sLang == null ) {
				throw new Exception(Res.getString("LANGMISSING"));
			}
		}

		// Check if it goes to the source or the target, or is skipped
		m_bSrc = Utils.areSameLanguages(m_sLang, m_sSrcLang, true);
		if ( m_bSrc ) {
			m_CurrentFI = m_SrcFI;
		}
		else { // Target language
			if ( m_sReqTrgLang == null ) m_sReqTrgLang = m_sLang;
			if ( Utils.areSameLanguages(m_sLang, m_sReqTrgLang, false) ) {
				Item IT = new Item(m_sLang);
				m_CurrentFI = IT.getFI();
				m_aItems.add(IT);
			}
			else {
				m_CurrentFI = null; // Not in a language to keep
			}
		}
	}

	private void processHeader (Element p_Elem)
		throws Exception
	{
		m_sMainSrcLang = p_Elem.getAttribute("srclang");
		if ( m_sMainSrcLang == null ) {
			throw new Exception(Res.getString("SRCLANGMISSING"));
		}
		if ( m_sReqSrcLang == null ) m_sReqSrcLang = m_sMainSrcLang; 
	}
	
	private void processTMX (Element p_Elem)
		throws Exception
	{
		m_sVersion = p_Elem.getAttribute("version");
		if ( m_sVersion == null ) {
			throw new Exception(Res.getString("VERSIONMISSING"));
		}
	}
}
