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

package net.sf.okapi.applications.rainbow.packages;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implements the writing and reading of a manifest document, commonly used
 * in different types of translation packages.
 */
public class Manifest {
	private Hashtable<Integer, ManifestItem>   docs;
	private String                             rootFolder;
	private String                             packageID;
	private String                             packageType;
	private String                             projectID;
	private String                             sourceLang;
	private String                             targetLang;
	private String                             originalDir;
	private String                             sourceDir;
	private String                             targetDir;


	public Manifest () {
		docs = new Hashtable<Integer, ManifestItem>();
		sourceDir = targetDir = "";
		originalDir = "";
	}

	public Hashtable<Integer, ManifestItem> getItems () {
		return docs;
	}

	public String getPackageID () {
		return packageID;
	}
	
	public void setPackageID (String value) {
		packageID = value;
	}

	public String getPackageType () {
		return packageType;
	}
	
	public void setPackageType (String value) {
		packageType = value;
	}

	public String getProjectID () {
		return projectID;
	}
	
	public void setProjectID (String value) {
		projectID = value;
	}

	public String getSourceLanguage () {
		return sourceLang;
	}
	
	public void setSourceLanguage (String value) {
		if ( value == null ) throw new NullPointerException();
		sourceLang = value;
	}

	public String getTargetLanguage () {
		return targetLang;
	}
	
	public void setTargetLanguage (String value) {
		if ( value == null ) throw new NullPointerException();
		targetLang = value;
	}

	public String getRoot () {
		return rootFolder;
	}
	
	public void setRoot (String value) {
		if ( value == null ) throw new NullPointerException();
		rootFolder = value;
	}

	public String getSourceLocation () {
		return sourceDir;
	}
	
	public void setSourceLocation (String value) {
		if ( value == null ) sourceDir = "";
		else sourceDir = value;
	}

	public String getTargetLocation () {
		return targetDir;
	}
	
	public void setTargetLocation (String value) {
		if ( value == null ) targetDir = "";
		else targetDir = value;
	}

	public String getOriginalLocation () {
		return originalDir;
	}
	
	public void setOriginalLocation (String value) {
		if ( value == null ) originalDir = "";
		else originalDir = value;
	}

	/**
	 * Adds a document to the manifest.
	 * @param docID Key of the document. Must be unique within the manifest.
	 * @param relativeInputPath Relative path of the input document (without leading separator).
	 */
	public void addDocument (int docID,
		String relativeInputPath)
	{
		docs.put(docID, new ManifestItem(relativeInputPath, true));
	}

	public String getItemFullSourcePath (int docID) {
		return rootFolder + File.separator
			+ (( sourceDir.length() == 0 ) ? "" : (sourceDir + File.separator))
			+ docs.get(docID).getRelativePath();
	}

	public String getItemRelativeSourcePath (int docID) {
		return (( sourceDir.length() == 0 ) ? "" : (sourceDir + File.separator))
			+ docs.get(docID).getRelativePath();
	}

	public String getItemFullTargetPath (int docID) {
		return rootFolder + File.separator
			+ (( targetDir.length() == 0 ) ? "" : (targetDir + File.separator))
			+ docs.get(docID).getRelativePath();
	}

	public String getItemRelativeTargetPath (int docID) {
		return (( targetDir.length() == 0 ) ? "" : (targetDir + File.separator))
			+ docs.get(docID).getRelativePath();
	}

	public String getItemFullOriginalPath (int docID) {
		return rootFolder + File.separator
			+ (( originalDir.length() == 0 ) ? "" : (originalDir + File.separator))
			+ docs.get(docID).getRelativePath();
	}

	public String getItemRelativeOriginalPath (int docID) {
		return (( originalDir.length() == 0 ) ? "" : (originalDir + File.separator))
			+ docs.get(docID).getRelativePath();
	}

	/**
	 * Saves the manifest file. This method assumes the root is set.
	 */
	public void Save () {
		XMLWriter XW = null;
		try {
			XW = new XMLWriter();
			XW.create(rootFolder + File.separator + "manifest.xml");

			XW.writeStartDocument();
			XW.writeComment("=================================================================");
			XW.writeComment("PLEASE, DO NOT RENAME, MOVE, MODIFY OR ALTER IN ANY WAY THIS FILE");
			XW.writeComment("=================================================================");
			XW.writeStartElement("borneoManifest");
			XW.writeAttributeString("xmlns:its", "http://www.w3.org/2005/11/its");
			XW.writeAttributeString("its:version", "1.0");
			XW.writeAttributeString("its:translate", "no");
			XW.writeAttributeString("projectID", projectID);
			XW.writeAttributeString("packageID", packageID);
			XW.writeAttributeString("sourceLang", sourceLang);
			XW.writeAttributeString("targetLang", targetLang);
			XW.writeAttributeString("packageType", packageType);
			XW.writeAttributeString("sourceDir", sourceDir);
			XW.writeAttributeString("targetDir", targetDir);
			SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
			XW.writeAttributeString("date", DF.format(new java.util.Date()));

			Enumeration<Integer> E = docs.keys();
			while ( E.hasMoreElements() ) {
				int nDKey = E.nextElement();
				XW.writeStartElement("doc");
				XW.writeAttributeString("id", String.valueOf(nDKey));
				XW.writeString(docs.get(nDKey).getRelativePath());
				XW.writeEndElement();
			}

			XW.writeEndElement(); // borneoManifest
			XW.writeEndDocument();
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
		finally {
			if ( XW != null ) XW.close();
		}
	}

	public void load (String path) {
		try {
			DocumentBuilderFactory DFac = DocumentBuilderFactory.newInstance();
		    // Not needed in this case: DFac.setNamespaceAware(true);
		    Document XD = DFac.newDocumentBuilder().parse("file:///"+path);
		    
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

		    docs.clear();
		    NL = E.getElementsByTagName("doc");
		    for ( int i=0; i<NL.getLength(); i++ ) {
		    	E = (Element)NL.item(i);
		    	sTmp = E.getAttribute("id");
			    if (( sTmp == null ) || ( sTmp.length() == 0 ))
			    	throw new Exception("Missing id attribute.");
			    
		    	docs.put(Integer.valueOf(sTmp),
		    		new ManifestItem(E.getTextContent(), true));
		    }

		    rootFolder = Util.getDirectoryName(path);
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks the content of the manifest against the package where
	 * it has been found.
	 * @return The number of error found.
	 */
	public int checkPackageContent () {
		int nErrors = 0;
		Enumeration<Integer> E = docs.keys();
		int nDKey;
		ManifestItem MI;
		while ( E.hasMoreElements() ) {
			nDKey = E.nextElement();
			MI = docs.get(nDKey);
			File F = new File(getItemFullTargetPath(nDKey));
			if ( !F.exists() ) {
				nErrors++;
				MI.setExists(false);
			}
		}
		return nErrors;
	}
}
