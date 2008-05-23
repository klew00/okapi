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

package net.sf.okapi.applications.rainbow.packages;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.lib.XMLWriter;
import net.sf.okapi.common.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implements the writing and reading of a manifest document, commonly used in different types of
 * translation packages.
 */
public class Manifest {
	private ILog                               m_Log;
	private Hashtable<Integer, ManifestItem>   m_Docs;
	private String                             m_sRoot;
	private String                             m_sPackageID;
	private String                             m_sPackageType;
	private String                             m_sProjectID;
	private String                             m_sSrcLang;
	private String                             m_sTrgLang;
	private String                             m_sSrcDir;
	private String                             m_sTrgDir;


	public Manifest (ILog p_Log) {
		m_Log = p_Log;
		m_Docs = new Hashtable<Integer, ManifestItem>();
		m_sSrcDir = m_sTrgDir = "";
	}

	public Hashtable<Integer, ManifestItem> getItems () {
		return m_Docs;
	}

	public String getPackageID () {
		return m_sPackageID;
	}
	
	public void setPackageID (String p_sValue) {
		m_sPackageID = p_sValue;
	}

	public String getPackageType () {
		return m_sPackageType;
	}
	
	public void setPackageType (String p_sValue) {
		m_sPackageType = p_sValue;
	}

	public String getProjectID () {
		return m_sProjectID;
	}
	
	public void setProjectID (String p_sValue) {
		m_sProjectID = p_sValue;
	}

	public String getSourceLanguage () {
		return m_sSrcLang;
	}
	
	public void setSourceLanguage (String p_sValue) {
		m_sSrcLang = p_sValue;
	}

	public String getTargetLanguage () {
		return m_sTrgLang;
	}
	
	public void setTargetLanguage (String p_sValue) {
		m_sTrgLang = p_sValue;
	}

	public String getRoot () {
		return m_sRoot;
	}
	
	public void setRoot (String p_sValue) {
		m_sRoot = p_sValue;
	}

	public String getSourceLocation () {
		return m_sSrcDir;
	}
	
	public void setSourceLocation (String p_sValue) {
		if ( p_sValue == null ) m_sSrcDir = "";
		else m_sSrcDir = p_sValue;
	}

	public String getTargetLocation () {
		return m_sTrgDir;
	}
	
	public void setTargetLocation (String p_sValue) {
		if ( p_sValue == null ) m_sTrgDir = "";
		else m_sTrgDir = p_sValue;
	}

	/**
	 * Adds a document to the manifest.
	 * @param p_nDKey Key of the document. Must be unique within the manifest.
	 * @param p_sRelativePath Relative path of the document (without leading separator).
	 */
	public void addDocument (int p_nDKey,
		String p_sRelativePath)
	{
		m_Docs.put(p_nDKey, new ManifestItem(p_sRelativePath, true));
	}

	public String getItemFullSourcePath (int p_nDKey) {
		return m_sRoot + File.separator
			+ (( m_sSrcDir.length() == 0 ) ? "" : (m_sSrcDir + File.separator))
			+ m_Docs.get(p_nDKey).getRelativePath();
	}

	public String getItemRelativeSourcePath (int p_nDKey) {
		return (( m_sSrcDir.length() == 0 ) ? "" : (m_sSrcDir + File.separator))
			+ m_Docs.get(p_nDKey).getRelativePath();
	}

	public String getItemFullTargetPath (int p_nDKey) {
		return m_sRoot + File.separator
			+ (( m_sTrgDir.length() == 0 ) ? "" : (m_sTrgDir + File.separator))
			+ m_Docs.get(p_nDKey).getRelativePath();
	}

	public String getItemRelativeTargetPath (int p_nDKey) {
		return (( m_sTrgDir.length() == 0 ) ? "" : (m_sTrgDir + File.separator))
			+ m_Docs.get(p_nDKey).getRelativePath();
	}

	/**
	 * Saves the manifest file. This method assumes the root is set.
	 */
	public void Save () {
		XMLWriter XW = null;
		try {
			XW = new XMLWriter();
			XW.create(m_sRoot + File.separator + "manifest.xml");

			XW.writeStartDocument();
			XW.writeComment("=================================================================");
			XW.writeComment("PLEASE, DO NOT RENAME, MOVE, MODIFY OR ALTER IN ANY WAY THIS FILE");
			XW.writeComment("=================================================================");
			XW.writeStartElement("borneoManifest");
			XW.writeAttributeString("xmlns:its", "http://www.w3.org/2005/11/its");
			XW.writeAttributeString("its:version", "1.0");
			XW.writeAttributeString("its:translate", "no");
			XW.writeAttributeString("projectID", m_sProjectID);
			XW.writeAttributeString("packageID", m_sPackageID);
			XW.writeAttributeString("sourceLang", m_sSrcLang);
			XW.writeAttributeString("targetLang", m_sTrgLang);
			XW.writeAttributeString("packageType", m_sPackageType);
			XW.writeAttributeString("sourceDir", m_sSrcDir);
			XW.writeAttributeString("targetDir", m_sTrgDir);
			SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
			XW.writeAttributeString("date", DF.format(new java.util.Date()));

			Enumeration<Integer> E = m_Docs.keys();
			while ( E.hasMoreElements() ) {
				int nDKey = E.nextElement();
				XW.writeStartElement("doc");
				XW.writeAttributeString("key", String.valueOf(nDKey));
				XW.writeString(m_Docs.get(nDKey).getRelativePath());
				XW.writeEndElement();
			}

			XW.writeEndElement(); // borneoManifest
			XW.writeEndDocument();
		}
		catch ( Exception e ) {
			e.printStackTrace();
			m_Log.error(e.getLocalizedMessage());
		}
		finally {
			if ( XW != null ) XW.close();
		}
	}

	public boolean load (String p_sPath) {
		try {
			DocumentBuilderFactory DFac = DocumentBuilderFactory.newInstance();
		    // Not needed in this case: DFac.setNamespaceAware(true);
		    Document XD = DFac.newDocumentBuilder().parse("file:///"+p_sPath);
		    
		    NodeList NL = XD.getElementsByTagName("borneoManifest");
		    if ( NL == null ) throw new Exception("Invalid manifest file.");
		    Element E = (Element)NL.item(0);
		    
		    String sTmp = E.getAttribute("projectID");
		    if (( sTmp == null ) || ( sTmp.length() == 0 ))
		    	throw new Exception("Missing projectID attribute.");
		    else setProjectID(sTmp);
		    
		    sTmp = E.getAttribute("packageID");
		    if (( sTmp == null ) || ( sTmp.length() == 0 ))
		    	throw new Exception("Missing packageID attribute.");
		    else setPackageID(sTmp);
		    
		    sTmp = E.getAttribute("packageType");
		    if (( sTmp == null ) || ( sTmp.length() == 0 ))
		    	throw new Exception("Missing packageType attribute.");
		    else setPackageType(sTmp);
		    
		    sTmp = E.getAttribute("sourceLang");
		    if (( sTmp == null ) || ( sTmp.length() == 0 ))
		    	throw new Exception("Missing sourceLang attribute.");
		    else setSourceLanguage(sTmp);
		    
		    sTmp = E.getAttribute("targetLang");
		    if (( sTmp == null ) || ( sTmp.length() == 0 ))
		    	throw new Exception("Missing targetLang attribute.");
		    else setTargetLanguage(sTmp);

		    sTmp = E.getAttribute("sourceDir");
		    setSourceLocation(sTmp);

		    sTmp = E.getAttribute("targetDir");
		    setTargetLocation(sTmp);

		    m_Docs.clear();
		    NL = E.getElementsByTagName("doc");
		    for ( int i=0; i<NL.getLength(); i++ ) {
		    	E = (Element)NL.item(i);
		    	sTmp = E.getAttribute("key");
			    if (( sTmp == null ) || ( sTmp.length() == 0 ))
			    	throw new Exception("Missing key attribute.");
			    
		    	m_Docs.put(Integer.valueOf(sTmp),
		    		new ManifestItem(E.getTextContent(), true));
		    }

		    m_sRoot = Util.getDirectoryName(p_sPath);
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			return false;
		}
		return true;
	}

	public int checkPackageContent () {
		int nErrors = 0;
		try {
			Enumeration<Integer> E = m_Docs.keys();
			int nDKey;
			ManifestItem MI;
			while ( E.hasMoreElements() ) {
				nDKey = E.nextElement();
				MI = m_Docs.get(nDKey);
				File F = new File(getItemFullTargetPath(nDKey));
				if ( !F.exists() ) {
					m_Log.warning("The document not found: " + F.getAbsolutePath());
					nErrors++;
					MI.setExist(false);
				}
			}
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
		return nErrors;
	}
}
