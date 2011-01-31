/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.simplekit.common;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.LocaleId;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implements the writing and reading of a manifest document, commonly used
 * in different types of translation packages.
 */
public class Manifest {

	public static final String VERSION = "2";
	public static final String MANIFEST_FILENAME = "manifest.xml";
	
	private LinkedHashMap<Integer, ManifestItem> docs;
	private String inputRoot;
	private String packageRoot;
	private String packageId;
	private String projectId;
	private LocaleId sourceLoc;
	private LocaleId targetLoc;
	private String originalDir;
	private String sourceDir;
	private String targetDir;
	private String date;

	public Manifest () {
		docs = new LinkedHashMap<Integer, ManifestItem>();
		originalDir = "";
		sourceDir = "";
		targetDir = "";
	}

	public Map<Integer, ManifestItem> getItems () {
		return docs;
	}

	public ManifestItem getItem (int docID) {
		return docs.get(docID);
	}
	
	public String getPackageId () {
		return packageId;
	}
	
	public String getProjectId () {
		return projectId;
	}
	
	public LocaleId getSourceLocale () {
		return sourceLoc;
	}
	
	public LocaleId getTargetLocale () {
		return targetLoc;
	}
	
	/**
	 * Gets the input root (always with the terminal separator).
	 * @return the input root.
	 */
	public String getInputRoot () {
		return inputRoot;
	}
	
	/**
	 * Gets the package root (always with the terminal separator).
	 * @return the package root.
	 */
	public String getPackageRoot () {
		return packageRoot;
	}
	
	/**
	 * Gets the directory where to store the original files (always with a terminal separator).
	 * @return the directory where to store the original files.
	 */
	public String getOriginalDirectory () {
		return originalDir;
	}
	
	public void setOriginalSurDirectory (String subDir) {
		this.originalDir = Util.ensureSeparator(packageRoot + subDir, false);
	}
	
	/**
	 * Gets the full directory where to store the prepared source files (always with a terminal separator). 
	 * @return the directory where to store the prepared source files.
	 */
	public String getSourceDirectory () {
		return sourceDir;
	}
	
	public void setSourceSubDirectory (String subDir) {
		sourceDir = Util.ensureSeparator(packageRoot + subDir, false);
	}
	
	/**
	 * Get the directory where to store the prepared target files (always with a terminal separator).
	 * @return the directory where to store the prepared target files.
	 */
	public String getTargetDirectory () {
		return targetDir;
	}
	
	public void setTargetSubDirectory (String subDir) {
		targetDir = Util.ensureSeparator(packageRoot + subDir, false);
	}
	
	public void setInformation (String packageRoot,
		LocaleId srcLoc,
		LocaleId trgLoc,
		String inputRoot)
	{
		this.sourceLoc = srcLoc;
		this.targetLoc = trgLoc;
		this.inputRoot = Util.ensureSeparator(inputRoot, false);
		this.packageRoot = Util.ensureSeparator(packageRoot, false);
	}
	
	/**
	 * Adds a document to the manifest.
	 * @param docId Key of the document. Must be unique within the manifest.
	 * @param relativeInputPath Relative path of the input document.
	 * @param relativeOutputPath Relative path of the output document.
	 */
	public void addDocument (int docId,
		String originalRelativePath,
		String sourceRelativePath,
		String encoding,
		String filterID,
		String formatType)
	{
		docs.put(docId, new ManifestItem(docId,
			originalRelativePath, sourceRelativePath,
			encoding, filterID, formatType));
	}

	/**
	 * Saves the manifest file. This method assumes the root is set.
	 */
	public void Save () {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(packageRoot + MANIFEST_FILENAME);

			writer.writeStartDocument();
			writer.writeComment("=================================================================");
			writer.writeComment("PLEASE, DO NOT RENAME, MOVE, MODIFY OR ALTER IN ANY WAY THIS FILE");
			writer.writeComment("=================================================================");
			writer.writeStartElement("manifest");
			writer.writeAttributeString("version", VERSION);
			writer.writeAttributeString("projectId", projectId);
			writer.writeAttributeString("packageId", packageId);
			writer.writeAttributeString("source", sourceLoc.toString());
			writer.writeAttributeString("target", targetLoc.toString());
			writer.writeAttributeString("originalDir", originalDir.replace('\\', '/'));
			writer.writeAttributeString("sourceDir", sourceDir.replace('\\', '/'));
			writer.writeAttributeString("targetDir", targetDir.replace('\\', '/'));
			SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
			writer.writeAttributeString("date", DF.format(new java.util.Date()));

			for ( ManifestItem item : docs.values() ) {
				writer.writeStartElement("doc");
				writer.writeAttributeString("id", String.valueOf(item.getId()));
				writer.writeAttributeString("original", item.getOriginalRelativePath().replace('\\', '/'));
				writer.writeAttributeString("source", item.getSourceRelativePath().replace('\\', '/'));
				writer.writeAttributeString("filter", item.getFilterId());
				writer.writeAttributeString("encoding", item.getEncoding());
				writer.writeAttributeString("formatType", item.getFormatType());
				writer.writeEndElementLineBreak();
			}

			writer.writeEndElement(); // manifest
			writer.writeEndDocument();
		}
		finally {
			if ( writer != null ) writer.close();
		}
	}

	public void load (File inputFile) {
		try {
			DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
		    // Not needed in this case: docFac.setNamespaceAware(true);
			Document doc = docFac.newDocumentBuilder().parse(inputFile);
		    
		    NodeList NL = doc.getElementsByTagName("manifest");
		    if ( NL == null ) throw new RuntimeException("Invalid manifest file.");
		    Element elem = (Element)NL.item(0);
		    if ( elem == null ) throw new RuntimeException("Invalid manifest file.");
		    
		    String tmp = elem.getAttribute("version");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing vaersion attribute.");

		    tmp = elem.getAttribute("projectId");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing projectId attribute.");
		    projectId = tmp;
		    
		    tmp = elem.getAttribute("packageId");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing packageId attribute.");
		    packageId = tmp;
		    
		    tmp = elem.getAttribute("source");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing source attribute.");
		    sourceLoc = LocaleId.fromString(tmp);
		    
		    tmp = elem.getAttribute("target");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing target attribute.");
		    targetLoc = LocaleId.fromString(tmp);

		    tmp = elem.getAttribute("originalDir");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing originalDir attribute.");
		    originalDir = tmp.replace('/', File.separatorChar);

		    tmp = elem.getAttribute("sourceDir");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing sourceDir attribute.");
		    sourceDir = tmp.replace('/', File.separatorChar);

		    tmp = elem.getAttribute("date");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing date attribute.");
		    date = tmp;
		    
		    String oriPath, srcPath, enc, filterID, formatType;
		    docs.clear();
		    NL = elem.getElementsByTagName("doc");
		    for ( int i=0; i<NL.getLength(); i++ ) {
		    	elem = (Element)NL.item(i);
		    	tmp = elem.getAttribute("id");
			    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing id attribute.");
			    int id = Integer.valueOf(tmp);
			    
//		    	tmp = elem.getAttribute("sourcePath");
//		    	if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing work attribute.");
//		    	sourceDir
//			    
//		    	oriPath = elem.getAttribute("original");
//			    if (( oriPathinPath == null ) || ( inPath.length() == 0 ))
//			    	throw new RuntimeException("Missing input attribute.");
//			    
//		    	inEnc = elem.getAttribute("inputEncoding");
//			    if (( inEnc == null ) || ( inEnc.length() == 0 ))
//			    	throw new RuntimeException("Missing inputEncoding attribute.");
//			    
//		    	outEnc = elem.getAttribute("outputEncoding");
//			    if (( outEnc == null ) || ( outEnc.length() == 0 ))
//			    	throw new RuntimeException("Missing outputEncoding attribute.");
//			    
//			    filterID = elem.getAttribute("filter");
//			    if (( filterID == null ) || ( filterID.length() == 0 ))
//			    	throw new RuntimeException("Missing filter attribute.");
//			    
//			    postProcessingType = elem.getAttribute("postProcessing");
//			    if (( filterID == null ) || ( filterID.length() == 0 )) {
//			    	postProcessingType = "default";	
//			    }
			    
//		    	docs.put(id, new ManifestItem(id, tmp.replace('/', File.separatorChar),
//		    		inPath.replace('/', File.separatorChar),
//		    		outPath.replace('/', File.separatorChar),
//		    		inEnc, outEnc, filterID, postProcessingType, true));
		    }

		    packageRoot = Util.getDirectoryName(inputFile.getAbsolutePath());
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

	/**
	 * Checks the content of the manifest against the package where
	 * it has been found.
	 * @return The number of error found.
	 */
	public int checkPackageContent () {
		int nErrors = 0;
		Iterator<Integer> iter = docs.keySet().iterator();
		int docId;
		ManifestItem mi;
		while ( iter.hasNext() ) {
			docId = iter.next();
////			mi = docs.get(docId);
////			File F = new File(getFileToMergePath(docId));
//			if ( !F.exists() ) {
//				nErrors++;
//				mi.setExists(false);
//			}
		}
		return nErrors;
	}

}
