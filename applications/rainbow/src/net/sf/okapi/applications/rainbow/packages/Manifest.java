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
	private String                             readerClass;


	public Manifest () {
		docs = new Hashtable<Integer, ManifestItem>();
		sourceDir = targetDir = "";
		originalDir = "";
	}

	public void setReaderClass (String readerClass) {
		this.readerClass = readerClass;
	}
	
	public String getReaderClass () {
		return readerClass;
	}
	
	public Hashtable<Integer, ManifestItem> getItems () {
		return docs;
	}

	public ManifestItem getItem (int docID) {
		return docs.get(docID);
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
	 * @param relativeInputPath Relative path of the input document.
	 * @param relativeOutputPath Relative path of the output document.
	 */
	public void addDocument (int docID,
		String relativeWorkPath,
		String relativeInputPath,
		String relativeOutputPath,
		String inputEncoding,
		String outputEncoding,
		String filterID)
	{
		docs.put(docID, new ManifestItem(relativeWorkPath,
			relativeInputPath, relativeOutputPath,
			inputEncoding, outputEncoding, filterID, true));
	}

	public String getItemFullSourcePath (int docID) {
		return rootFolder + File.separator
			+ (( sourceDir.length() == 0 ) ? "" : (sourceDir + File.separator))
			+ docs.get(docID).getRelativeInputPath();
	}

	public String getItemRelativeSourcePath (int docID) {
		return (( sourceDir.length() == 0 ) ? "" : (sourceDir + File.separator))
			+ docs.get(docID).getRelativeInputPath();
	}

	public String getItemFullTargetPath (int docID) {
		return rootFolder + File.separator
			+ (( targetDir.length() == 0 ) ? "" : (targetDir + File.separator))
			+ docs.get(docID).getRelativeInputPath();
	}

	public String getItemRelativeTargetPath (int docID) {
		return (( targetDir.length() == 0 ) ? "" : (targetDir + File.separator))
			+ docs.get(docID).getRelativeInputPath();
	}

	public String getItemFullOriginalPath (int docID) {
		return rootFolder + File.separator
			+ (( originalDir.length() == 0 ) ? "" : (originalDir + File.separator))
			+ docs.get(docID).getRelativeInputPath();
	}

	public String getItemRelativeOriginalPath (int docID) {
		return (( originalDir.length() == 0 ) ? "" : (originalDir + File.separator))
			+ docs.get(docID).getRelativeInputPath();
	}

	/**
	 * Saves the manifest file. This method assumes the root is set.
	 */
	public void Save () {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter();
			writer.create(rootFolder + File.separator + "manifest.xml");

			writer.writeStartDocument();
			writer.writeComment("=================================================================");
			writer.writeComment("PLEASE, DO NOT RENAME, MOVE, MODIFY OR ALTER IN ANY WAY THIS FILE");
			writer.writeComment("=================================================================");
			writer.writeStartElement("rainbowManifest");
			writer.writeAttributeString("xmlns:its", "http://www.w3.org/2005/11/its");
			writer.writeAttributeString("its:version", "1.0");
			writer.writeAttributeString("its:translate", "no");
			writer.writeAttributeString("projectID", projectID);
			writer.writeAttributeString("packageID", packageID);
			writer.writeAttributeString("sourceLang", sourceLang);
			writer.writeAttributeString("targetLang", targetLang);
			writer.writeAttributeString("packageType", packageType);
			writer.writeAttributeString("readerClass", readerClass);
			writer.writeAttributeString("originalDir", originalDir);
			writer.writeAttributeString("sourceDir", sourceDir);
			writer.writeAttributeString("targetDir", targetDir);
			SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
			writer.writeAttributeString("date", DF.format(new java.util.Date()));

			Enumeration<Integer> E = docs.keys();
			ManifestItem item;
			while ( E.hasMoreElements() ) {
				int id = E.nextElement();
				item = docs.get(id);
				writer.writeStartElement("doc");
				writer.writeAttributeString("id", String.valueOf(id));
				writer.writeAttributeString("filter", item.getFilterID());
				writer.writeAttributeString("work", item.getRelativeWorkPath());
				writer.writeAttributeString("input", item.getRelativeInputPath());
				writer.writeAttributeString("output", item.getRelativeOutputPath());
				writer.writeAttributeString("inputEncoding", item.getInputEncoding());
				writer.writeAttributeString("outputEncoding", item.getOutputEncoding());
				writer.writeEndElementLineBreak();
			}

			writer.writeEndElement(); // rainbowManifest
			writer.writeEndDocument();
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
		finally {
			if ( writer != null ) writer.close();
		}
	}

	public void load (String path) {
		try {
			DocumentBuilderFactory DFac = DocumentBuilderFactory.newInstance();
		    // Not needed in this case: DFac.setNamespaceAware(true);
		    Document doc = DFac.newDocumentBuilder().parse("file:///"+path);
		    
		    NodeList NL = doc.getElementsByTagName("rainbowManifest");
		    if ( NL == null ) throw new Exception("Invalid manifest file.");
		    Element elem = (Element)NL.item(0);
		    
		    String tmp = elem.getAttribute("projectID");
		    if (( tmp == null ) || ( tmp.length() == 0 ))
		    	throw new Exception("Missing projectID attribute.");
		    else setProjectID(tmp);
		    
		    tmp = elem.getAttribute("packageID");
		    if (( tmp == null ) || ( tmp.length() == 0 ))
		    	throw new Exception("Missing packageID attribute.");
		    else setPackageID(tmp);
		    
		    tmp = elem.getAttribute("packageType");
		    if (( tmp == null ) || ( tmp.length() == 0 ))
		    	throw new Exception("Missing packageType attribute.");
		    else setPackageType(tmp);
		    
		    tmp = elem.getAttribute("readerClass");
		    if (( tmp == null ) || ( tmp.length() == 0 ))
		    	throw new Exception("Missing readerClass attribute.");
		    else setReaderClass(tmp);
		    
		    tmp = elem.getAttribute("sourceLang");
		    if (( tmp == null ) || ( tmp.length() == 0 ))
		    	throw new Exception("Missing sourceLang attribute.");
		    else setSourceLanguage(tmp);
		    
		    tmp = elem.getAttribute("targetLang");
		    if (( tmp == null ) || ( tmp.length() == 0 ))
		    	throw new RuntimeException("Missing targetLang attribute.");
		    else setTargetLanguage(tmp);

		    tmp = elem.getAttribute("originalDir");
		    setOriginalLocation(tmp);

		    tmp = elem.getAttribute("sourceDir");
		    setSourceLocation(tmp);

		    tmp = elem.getAttribute("targetDir");
		    setTargetLocation(tmp);

		    String inPath, outPath, inEnc, outEnc, filterID;
		    docs.clear();
		    NL = elem.getElementsByTagName("doc");
		    for ( int i=0; i<NL.getLength(); i++ ) {
		    	elem = (Element)NL.item(i);
		    	tmp = elem.getAttribute("id");
			    if (( tmp == null ) || ( tmp.length() == 0 ))
			    	throw new RuntimeException("Missing id attribute.");
			    int id = Integer.valueOf(tmp);
			    
		    	tmp = elem.getAttribute("work");
			    if (( tmp == null ) || ( tmp.length() == 0 ))
			    	throw new RuntimeException("Missing work attribute.");
			    
		    	inPath = elem.getAttribute("input");
			    if (( inPath == null ) || ( inPath.length() == 0 ))
			    	throw new RuntimeException("Missing input attribute.");
			    
		    	outPath = elem.getAttribute("output");
			    if (( outPath == null ) || ( outPath.length() == 0 ))
			    	throw new RuntimeException("Missing output attribute.");
			    
		    	inEnc = elem.getAttribute("inputEncoding");
			    if (( inEnc == null ) || ( inEnc.length() == 0 ))
			    	throw new RuntimeException("Missing inputEncoding attribute.");
			    
		    	outEnc = elem.getAttribute("outputEncoding");
			    if (( outEnc == null ) || ( outEnc.length() == 0 ))
			    	throw new RuntimeException("Missing outputEncoding attribute.");
			    
			    filterID = elem.getAttribute("filter");
			    if (( filterID == null ) || ( filterID.length() == 0 ))
			    	throw new RuntimeException("Missing filter attribute.");
			    
		    	docs.put(id, new ManifestItem(tmp, inPath, outPath, inEnc, outEnc, filterID, true));
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
