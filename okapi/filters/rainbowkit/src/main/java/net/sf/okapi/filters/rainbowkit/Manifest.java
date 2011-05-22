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

package net.sf.okapi.filters.rainbowkit;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.Base64;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.IAnnotation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implements the writing and reading of a manifest document, commonly used
 * in different types of translation packages.
 */
public class Manifest implements IAnnotation {

	public static final String EXTRACTIONTYPE_NONE = "none"; // Not an extracted file (used for reference files)
	public static final String EXTRACTIONTYPE_XLIFF = "xliff";
	public static final String EXTRACTIONTYPE_XLIFF2 = "xliff2";
	public static final String EXTRACTIONTYPE_PO = "po";
	public static final String EXTRACTIONTYPE_RTF = "rtf";
	public static final String EXTRACTIONTYPE_XLIFFRTF = "xliff+rtf";
	public static final String EXTRACTIONTYPE_OMEGAT = "omegat";
	public static final String EXTRACTIONTYPE_TRANSIFEX = "transifex";
	public static final String EXTRACTIONTYPE_ONTRAM = "ontram";

	public static final String VERSION = "2";
	public static final String MANIFEST_FILENAME = "manifest";
	public static final String MANIFEST_EXTENSION = ".rkm";
	
	private LinkedHashMap<Integer, MergingInfo> docs;
	private String packageRoot;
	private String packageId;
	private String projectId;
	private LocaleId sourceLoc;
	private LocaleId targetLoc;
	private String inputRoot;
	private String originalSubDir;
	private String sourceSubDir;
	private String targetSubDir;
	private String tmSubDir;
	private String mergeSubDir;
	private String originalDir;
	private String sourceDir;
	private String targetDir;
	private String mergeDir;
	private String tmDir;
	private String creatorParams;
	private boolean useApprovedOnly;
	private boolean updateApprovedFlag;
	private String date;

	public Manifest () {
		docs = new LinkedHashMap<Integer, MergingInfo>();
		packageRoot = "";
		originalSubDir = "";
		sourceSubDir = "";
		targetSubDir = "";
		mergeSubDir = "";
		tmSubDir = "";
		updateFullDirectories();
		useApprovedOnly = false;
		updateApprovedFlag = false;
	}

	public Map<Integer, MergingInfo> getItems () {
		return docs;
	}

	public MergingInfo getItem (int docID) {
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
	
	public String getCreatorParameters () {
		return creatorParams;
	}
	
	public boolean getUseApprovedOnly () {
		return useApprovedOnly;
	}
	
	public void setUseApprovedOnly (boolean value) {
		useApprovedOnly = value;
	}
	
	public boolean getUpdateApprovedFlag () {
		return updateApprovedFlag;
	}
	
	public void setUpdateApprovedFlag (boolean value) {
		updateApprovedFlag = value;
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
	 * Sets the sub-directories used by the given package.
	 * All defaults to "" (same directory as the directory of the package itself.
	 * @param originalSubDir the sub-directory for the original document.
	 * @param sourceSubDir the sub-directory for the source documents.
	 * @param targetSubDir the sub-directory for the target documents.
	 * @param mergeSubDir the sub-directory for the merged documents.
	 * @param tmSubDir the sub-directory for TM-related data.
	 * @param overwrite true to overwrite existing settings (use null to not overwrite a given sub-directory).
	 * false to use the specified value (if the current is empty).
	 */
	public void setSubDirectories (String originalSubDir,
		String sourceSubDir,
		String targetSubDir,
		String mergeSubDir,
		String tmSubDir,
		boolean overwrite)
	{
		if (( originalSubDir != null ) && ( overwrite || Util.isEmpty(this.originalSubDir) )) {
			this.originalSubDir = originalSubDir;
		}
		if (( sourceSubDir != null ) && ( overwrite || Util.isEmpty(this.sourceSubDir) )) {
			this.sourceSubDir = sourceSubDir;
		}
		if (( targetSubDir != null ) && ( overwrite || Util.isEmpty(this.targetSubDir) )) {
			this.targetSubDir = targetSubDir;
		}
		if (( mergeSubDir != null ) && ( overwrite || Util.isEmpty(this.mergeSubDir) )) {
			this.mergeSubDir = mergeSubDir;
		}
		if (( tmSubDir != null ) && ( overwrite || Util.isEmpty(this.tmSubDir) )) {
			this.tmSubDir = tmSubDir;
		}
		updateFullDirectories();
	}
	
	/**
	 * Gets the directory where to store the original files (always with a terminal separator).
	 * @return the directory where to store the original files.
	 */
	public String getOriginalDirectory () {
		return originalDir;
	}
	
	/**
	 * Gets the full directory where to store the prepared source files (always with a terminal separator). 
	 * @return the directory where to store the prepared source files.
	 */
	public String getSourceDirectory () {
		return sourceDir;
	}
	
	/**
	 * Get the directory where to store the prepared target files (always with a terminal separator).
	 * @return the directory where to store the prepared target files.
	 */
	public String getTargetDirectory () {
		return targetDir;
	}
	
	/**
	 * Get the directory where to output the result of the merging process (always with a terminal separator).
	 * @return the directory where to store the prepared target files.
	 */
	public String getMergeDirectory () {
		return mergeDir;
	}
	
	/**
	 * Gets the directory where to output TM-related information (always with a terminal separator).
	 * @return the directory where to store TM-related information.
	 */
	public String getTmDirectory () {
		return tmDir;
	}
	
	/**
	 * Gets the date when the manifest was created (saved the first time).
	 * @return A string representation of the creation date,
	 * or null if the manifest file has not been saved yet.
	 */
	public String getDate () {
		return date;
	}
	
	public void setInformation (String packageRoot,
		LocaleId srcLoc,
		LocaleId trgLoc,
		String inputRoot,
		String packageId,
		String projectId,
		String creatorParams)
	{
		this.sourceLoc = srcLoc;
		this.targetLoc = trgLoc;
		this.inputRoot = Util.ensureSeparator(inputRoot, false);
		this.packageRoot = Util.ensureSeparator(packageRoot, false);
		this.packageId = packageId;
		this.projectId = projectId;
		updateFullDirectories();
		this.creatorParams = creatorParams;
	}
	
	/**
	 * Adds a document to the manifest.
	 * @param docId Key of the document. Must be unique within the manifest.
	 * @param relativeInputPath Relative path of the input document.
	 * @param relativeOutputPath Relative path of the output document.
	 */
	public void addDocument (int docId,
		String extractionType,
		String relativeInputPath,
		String filterId,
		String filterParameters,
		String inputEncoding,
		String relativeTargetPath,
		String targetEncoding)
	{
		docs.put(docId, new MergingInfo(docId, extractionType, relativeInputPath, filterId,
			filterParameters, inputEncoding, relativeTargetPath, targetEncoding));
	}
	
	/**
	 * Gets the full path of the manifest file.
	 * @return the full path of the manifest file.
	 */
	public String getPath () {
		return packageRoot+MANIFEST_FILENAME+MANIFEST_EXTENSION;
	}

	/**
	 * Saves the manifest file. This method assumes the root is set.
	 */
	public void Save () {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(getPath());

			writer.writeStartDocument();
			writer.writeComment("=================================================================", true);
			writer.writeComment("PLEASE, DO NOT RENAME, MOVE, MODIFY OR ALTER IN ANY WAY THIS FILE", true);
			writer.writeComment("=================================================================", true);
			writer.writeStartElement("manifest");
			writer.writeAttributeString("version", VERSION);
			writer.writeAttributeString("projectId", projectId);
			writer.writeAttributeString("packageId", packageId);
			writer.writeAttributeString("source", sourceLoc.toString());
			writer.writeAttributeString("target", targetLoc.toString());
			writer.writeAttributeString("originalSubDir", originalSubDir.replace('\\', '/'));
			writer.writeAttributeString("sourceSubDir", sourceSubDir.replace('\\', '/'));
			writer.writeAttributeString("targetSubDir", targetSubDir.replace('\\', '/'));
			writer.writeAttributeString("mergeSubDir", mergeSubDir.replace('\\', '/'));
			writer.writeAttributeString("tmSubDir", tmSubDir.replace('\\', '/'));
			SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
			date = DF.format(new java.util.Date());
			writer.writeAttributeString("date", date);
			writer.writeAttributeString("useApprovedOnly", (useApprovedOnly ? "1" : "0"));
			writer.writeAttributeString("updateApprovedFlag", (updateApprovedFlag ? "1" : "0"));
			writer.writeLineBreak();

			// creatorParams
			writer.writeStartElement("creatorParameters");
			writer.writeString(Base64.encodeString(creatorParams.toString()));
			writer.writeEndElementLineBreak();
			
			// Infor for the documents
			for ( MergingInfo item : docs.values() ) {
				writer.writeRawXML(item.writeToXML("doc", true));
				writer.writeLineBreak();
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

		    date = elem.getAttribute("date");
		    if ( Util.isEmpty(tmp) ) date = "Unknown";

		    tmp = elem.getAttribute("originalSubDir");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing originalSubDir attribute.");
		    originalSubDir = tmp.replace('/', File.separatorChar);

		    tmp = elem.getAttribute("sourceSubDir");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing sourceSubDir attribute.");
		    sourceSubDir = tmp.replace('/', File.separatorChar);

		    tmp = elem.getAttribute("targetSubDir");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing targetSubDir attribute.");
		    targetSubDir = tmp.replace('/', File.separatorChar);

		    tmp = elem.getAttribute("mergeSubDir");
		    if ( Util.isEmpty(tmp) ) throw new RuntimeException("Missing mergeSubDir attribute.");
		    mergeSubDir = tmp.replace('/', File.separatorChar);

		    tmp = elem.getAttribute("tmSubDir");
		    if ( Util.isEmpty(tmp) ) tmSubDir = "";
		    else tmSubDir = tmp.replace('/', File.separatorChar);
		    
		    // creatorParameters
		    NL = elem.getElementsByTagName("creatorParameters");
		    if ( NL.getLength() > 0 ) {
		    	creatorParams = Base64.decodeString(Util.getTextContent(NL.item(0)));
		    }
		    else {
		    	creatorParams = "";
		    }
		    
		    tmp = elem.getAttribute("useApprovedOnly");
		    if ( Util.isEmpty(tmp) ) useApprovedOnly = false;
		    else useApprovedOnly = !tmp.equals("0");
		    
		    tmp = elem.getAttribute("updateApprovedFlag");
		    if ( Util.isEmpty(tmp) ) this.updateApprovedFlag = true;
		    else updateApprovedFlag = !tmp.equals("0");
		    
		    // Documents
		    docs.clear();
		    NL = elem.getElementsByTagName("doc");
		    for ( int i=0; i<NL.getLength(); i++ ) {
		    	elem = (Element)NL.item(i);
		    	MergingInfo item = MergingInfo.readFromXML(elem);
		    	docs.put(item.getDocId(), item);
		    }
		    packageRoot = Util.ensureSeparator(Util.getDirectoryName(inputFile.getAbsolutePath()), false);
			updateFullDirectories();
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

//	/**
//	 * Checks the content of the manifest against the package where
//	 * it has been found.
//	 * @return The number of error found.
//	 */
//	public int checkPackageContent () {
//		int nErrors = 0;
//		Iterator<Integer> iter = docs.keySet().iterator();
//		int docId;
//		while ( iter.hasNext() ) {
//			docId = iter.next();
//////			mi = docs.get(docId);
//////			File F = new File(getFileToMergePath(docId));
////			if ( !F.exists() ) {
////				nErrors++;
////				mi.setExists(false);
////			}
//		}
//		return nErrors;
//	}

	private void updateFullDirectories () {
		originalDir = Util.ensureSeparator(packageRoot + originalSubDir, false);
		sourceDir = Util.ensureSeparator(packageRoot + sourceSubDir, false);
		targetDir = Util.ensureSeparator(packageRoot + targetSubDir, false);
		mergeDir = Util.ensureSeparator(packageRoot + mergeSubDir, false);
		tmDir = Util.ensureSeparator(packageRoot + tmSubDir, false);
	}

}
