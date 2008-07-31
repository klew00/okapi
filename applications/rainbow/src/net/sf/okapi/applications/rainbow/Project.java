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

package net.sf.okapi.applications.rainbow;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.lib.PathBuilder;
import net.sf.okapi.applications.rainbow.lib.Utils;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.ui.UIUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Project {

	protected ArrayList<ArrayList<Input>>   inputLists;
	private ArrayList<String>               inputRoots;
	private Hashtable<String, String>       utilityParams;
	
	protected String              path;
	protected PathBuilder         pathBuilder;
	protected boolean             isModified;
	
	private boolean               useOutputRoot;
	private String                outputRoot;
	private String                sourceLanguage;
	private String                sourceEncoding;
	private String                targetLanguage;
	private String                targetEncoding;
	private boolean               useCustomParamsFolder;
	private String                customParamsFolder;
	
	private String                lastOutputFolder;
	

	public Project (LanguageManager lm) {
		useCustomParamsFolder = false;
		customParamsFolder = System.getProperty("user.home");
		useOutputRoot = false;
		outputRoot = "";
		
		// Three lists
		inputLists = new ArrayList<ArrayList<Input>>();
		inputRoots = new ArrayList<String>();
		inputLists.add(new ArrayList<Input>());
		inputRoots.add(System.getProperty("user.home"));
		inputLists.add(new ArrayList<Input>());
		inputRoots.add(System.getProperty("user.home"));
		inputLists.add(new ArrayList<Input>());
		inputRoots.add(System.getProperty("user.home"));
		
		utilityParams = new Hashtable<String, String>();
		
		pathBuilder = new PathBuilder();
		pathBuilder.setExtension(".out");
		sourceLanguage = Utils.getDefaultSourceLanguage().toUpperCase();
		targetLanguage = Utils.getDefaultTargetLanguage().toUpperCase();
		sourceEncoding = lm.getDefaultEncodingFromCode(sourceLanguage, UIUtil.getPlatformType());
		targetEncoding = lm.getDefaultEncodingFromCode(targetLanguage, UIUtil.getPlatformType());
		isModified = false;
	}
	
	public ArrayList<Input> getList (int index) {
		return inputLists.get(index);
	}
	
	/**
	 * Adds a document to the project.
	 * @param listIndex Index of the input list where perform the operation.
	 * @param newPath Full path of the document to add.
	 * @param sourceEncoding Default sourceEncoding for the document (can be null).
	 * @param filterSettings Filter settings string for the document (can be null). 
	 * @return 0=Document added, 1=bad root, 2=exists already
	 */
	public int addDocument (int listIndex,
		String newPath,
		String sourceEncoding,
		String targetEncoding,
		String filterSettings)
	{
		// Is the root OK?
		if ( newPath.indexOf(inputRoots.get(listIndex)) == -1 ) return 1;
		newPath = newPath.substring(inputRoots.get(listIndex).length()+1); // No leading separator
		
		// Does the path exists already?
		for ( Input tmpInp : inputLists.get(listIndex) ) {
			if ( tmpInp.relativePath.equalsIgnoreCase(newPath) ) return 2; 
		}
		
		// Create the new entry and add it to the list
		Input inp = new Input();
		inp.sourceEncoding = ((sourceEncoding == null) ? "" : sourceEncoding);
		inp.targetEncoding = ((targetEncoding == null) ? "" : targetEncoding);
		inp.filterSettings = ((filterSettings == null) ? "" : filterSettings);
		inp.relativePath = newPath;
		inputLists.get(listIndex).add(inp);
		isModified = true;
		return 0;
	}

	/**
	 * Gets an input item from the list, based on its relative path name.
	 * @param listIndex Index of the input list where perform the operation.
	 * @param relativePath Relative path of the item to search for.
	 * @return An Input object or null.
	 */
	public Input getItemFromRelativePath (int listIndex,
		String relativePath)
	{
		for ( Input inp : inputLists.get(listIndex) ) {
			if ( inp.relativePath.equalsIgnoreCase(relativePath) ) return inp;
		}
		return null;
	}
	
	public void save (String newPath)
		throws Exception 
	{
		XMLWriter writer = null;
		try {
			writer = new XMLWriter();
			writer.create(newPath);
			writer.writeStartDocument();
			writer.writeStartElement("rainbowProject");
			writer.writeAttributeString("version", "4");
			
			int i=0;
			for ( ArrayList<Input> inputList : inputLists ) {
				writer.writeStartElement("fileSet");
				writer.writeAttributeString("id", String.format("%d", i+1));
				writer.writeElementString("root", inputRoots.get(i));
				for ( Input item : inputList ) {
					writer.writeStartElement("fi");
					writer.writeAttributeString("fs", item.filterSettings);
					writer.writeAttributeString("fo", item.format);
					writer.writeAttributeString("se", item.sourceEncoding);
					writer.writeAttributeString("te", item.targetEncoding);
					writer.writeString(item.relativePath);
					writer.writeEndElement(); // fi
				}
				writer.writeEndElement(); // fileSet
				i++;
			}
			
			writer.writeStartElement("output");
			writer.writeStartElement("root");
			writer.writeAttributeString("use", (useOutputRoot ? "1" : "0"));
			writer.writeString(outputRoot);
			writer.writeEndElement(); // root
			writer.writeStartElement("subFolder");
			writer.writeAttributeString("use", (pathBuilder.useSubfolder() ? "1" : "0"));
			writer.writeString(pathBuilder.getSubfolder());
			writer.writeEndElement(); // subFolder
			writer.writeStartElement("extension");
			writer.writeAttributeString("use", (pathBuilder.useExtension() ? "1" : "0"));
			writer.writeAttributeString("style", String.format("%d", pathBuilder.getExtensionType()));
			writer.writeString(pathBuilder.getExtension()); 
			writer.writeEndElement(); // extension
			writer.writeStartElement("replace");
			writer.writeAttributeString("use", (pathBuilder.useReplace() ? "1" : "0"));
			writer.writeAttributeString("oldText", pathBuilder.getSearch());
			writer.writeAttributeString("newText", pathBuilder.getReplace());
			writer.writeEndElement(); // replace
			writer.writeStartElement("prefix");
			writer.writeAttributeString("use", (pathBuilder.usePrefix() ? "1" : "0"));
			writer.writeString(pathBuilder.getPrefix());
			writer.writeEndElement(); // prefix
			writer.writeStartElement("suffix");
			writer.writeAttributeString("use", (pathBuilder.useSuffix() ? "1" : "0"));
			writer.writeString(pathBuilder.getSuffix());
			writer.writeEndElement(); // suffix
			writer.writeEndElement(); // output

			writer.writeStartElement("options");
			writer.writeAttributeString("sourceLanguage", sourceLanguage);
			writer.writeAttributeString("sourceEncoding", sourceEncoding);
			writer.writeAttributeString("targetLanguage", targetLanguage);
			writer.writeAttributeString("targetEncoding", targetEncoding);
			writer.writeEndElement(); // options
			
			writer.writeStartElement("parametersFolder");
			writer.writeAttributeString("useCustom", useCustomParamsFolder ? "1" : "0");
			writer.writeString(customParamsFolder);
			writer.writeEndElement(); // parametersFolder
			
			writer.writeStartElement("utilities");
			writer.writeAttributeString("xml:spaces", "preserve");
			for ( String utilityID : utilityParams.keySet() ) {
				writer.writeStartElement("params");
				writer.writeAttributeString("id", utilityID);
				writer.writeString(utilityParams.get(utilityID));
				writer.writeEndElement(); // params
			}
			writer.writeEndElement(); // utilities
			
			writer.writeEndElement(); // rainbowProject
			writer.writeEndDocument();
			isModified = false;
			path = newPath;
		}
		finally {
			if ( writer != null ) writer.close();
		}
	}

	private Element getFirstElement (Element parent,
		String name) {
		NodeList nl = parent.getElementsByTagName(name);
		if (( nl == null ) || ( nl.getLength() == 0 )) return null;
		else return (Element)nl.item(0);
	}
	
	/**
	 * Loads an existing project. The project must have been just created before.
	 * @param newPath Full path of the project file to load.
	 * @throws Exception
	 */
	public void load (String newPath)
		throws Exception
	{
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document doc = Fact.newDocumentBuilder().parse(new File(newPath));
			
			Element rootElem = doc.getDocumentElement();
			String tmp = rootElem.getAttribute("version");
			if ( !tmp.equals("4") ) {
				throw new Exception("Unsupported version of the project file.");
			}

			Element elem1;
			Element elem2;
			NodeList n1 = rootElem.getElementsByTagName("fileSet");
			//TODO: Allow for un-ordered list of fileSet ???
			for ( int i=0; i<n1.getLength(); i++ ) {
				elem1 = (Element)n1.item(i);

				elem2 = getFirstElement(elem1, "root");
				if ( elem2 == null ) throw new Exception("Element <root> missing.");
				inputRoots.set(i, elem2.getTextContent());

				NodeList n2 = elem1.getElementsByTagName("fi");
				Input item;
				for ( int j=0; j<n2.getLength(); j++ ) {
					elem2 = (Element)n2.item(j);
					item = new Input();
					item.filterSettings = elem2.getAttribute("fs");
					item.format = elem2.getAttribute("fo");
					item.sourceEncoding = elem2.getAttribute("se");
					item.targetEncoding = elem2.getAttribute("te");
					item.relativePath = elem2.getTextContent();
					inputLists.get(i).add(item);
				}
			}

			elem1 = getFirstElement(rootElem, "output");
			if ( elem1 == null ) throw new Exception("Element <output> missing.");
			
			elem2 = getFirstElement(elem1, "root");
			if ( elem2 != null ) {
				useOutputRoot = elem2.getAttribute("use").equals("1");
				outputRoot = elem2.getTextContent();
			}
			
			elem2 = getFirstElement(elem1, "subFolder");
			if ( elem2 != null ) {
				pathBuilder.setUseSubfolder(elem2.getAttribute("use").equals("1"));
				pathBuilder.setSubfolder(elem2.getTextContent());
			}
			
			elem2 = getFirstElement(elem1, "extension");
			if ( elem2 != null ) {
				pathBuilder.setUseExtension(elem2.getAttribute("use").equals("1"));
				int n = Integer.valueOf(elem2.getAttribute("style"));
				if (( n < 0 ) || ( n > 2 )) n = 2; // Sanity check
				pathBuilder.setExtensionType(n);
				pathBuilder.setExtension(elem2.getTextContent());
			}

			elem2 = getFirstElement(elem1, "replace");
			if ( elem2 != null ) {
				pathBuilder.setUseReplace(elem2.getAttribute("use").equals("1"));
				pathBuilder.setSearch(elem2.getAttribute("oldText"));
				pathBuilder.setReplace(elem2.getAttribute("newText"));
			}

			elem2 = getFirstElement(elem1, "prefix");
			if ( elem2 != null ) {
				pathBuilder.setUsePrefix(elem2.getAttribute("use").equals("1"));
				pathBuilder.setPrefix(elem2.getTextContent());
			}

			elem2 = getFirstElement(elem1, "suffix");
			if ( elem2 != null ) {
				pathBuilder.setUseSuffix(elem2.getAttribute("use").equals("1"));
				pathBuilder.setSuffix(elem2.getTextContent());
			}

			elem1 = getFirstElement(rootElem, "options");
			if ( elem1 == null ) throw new Exception("Element <options> missing.");
			sourceLanguage = elem1.getAttribute("sourceLanguage");
			targetLanguage = elem1.getAttribute("targetLanguage");
			sourceEncoding = elem1.getAttribute("sourceEncoding");
			targetEncoding = elem1.getAttribute("targetEncoding");
			
			elem1 = getFirstElement(rootElem, "parametersFolder");
			if ( elem1 == null ) throw new Exception("Element <parametersFolder> missing.");
			useCustomParamsFolder = elem1.getAttribute("useCustom").equals("1");
			customParamsFolder = elem1.getTextContent();
			
			// Parameters for the utilities
			elem1 = getFirstElement(rootElem, "utilities");
			if ( elem1 != null ) {
				n1 = rootElem.getElementsByTagName("params");
				for ( int i=0; i<n1.getLength(); i++ ) {
					elem2 = (Element)n1.item(i);
					utilityParams.put(elem2.getAttribute("id"), elem2.getTextContent());
				}
			}

			isModified = false;
			path = newPath;
		}
		catch (Exception E ) {
			throw E;
		}
	}
	
	public void setInputRoot (int listIndex,
		String newRoot)
	{
		if ( !inputRoots.get(listIndex).equals(newRoot) ) {
			inputRoots.set(listIndex, newRoot);
			isModified = true;
		}
	}
	
	public String getInputRoot (int listIndex) {
		return inputRoots.get(listIndex);
	}

	public void setUseOutputRoot (boolean value) {
		if ( useOutputRoot != value ) {
			useOutputRoot = value;
			isModified = true;
		}
	}
	
	public boolean getUseOutputRoot () {
		return useOutputRoot;
	}

	public void setOutputRoot (String newRoot) {
		if ( !outputRoot.equals(newRoot) ) {
			outputRoot = newRoot;
			isModified = true;
		}
	}
	
	public String getOutputRoot () {
		return outputRoot;
	}

	public void setSourceLanguage (String newLanguage) {
		if ( !sourceLanguage.equals(newLanguage) ) {
			sourceLanguage = newLanguage;
			isModified = true;
		}
	}
	
	public String getSourceLanguage () {
		return sourceLanguage;
	}
	
	public void setSourceEncoding (String newEncoding) {
		if ( !sourceEncoding.equals(newEncoding) ) {
			sourceEncoding = newEncoding;
			isModified = true;
		}
	}
	
	public String getSourceEncoding () {
		return sourceEncoding;
	}
	
	public void setTargetLanguage (String newLanguage) {
		if ( !targetLanguage.equals(newLanguage) ) {
			targetLanguage = newLanguage;
			isModified = true;
		}
	}
	
	public String getTargetLanguage () {
		return targetLanguage;
	}
	
	public void setTargetEncoding (String newEncoding) {
		if ( !targetEncoding.equals(newEncoding) ) {
			targetEncoding = newEncoding;
			isModified = true;
		}
	}
	
	public String getTargetEncoding () {
		return targetEncoding;
	}
	
	public void setCustomParametersFolder (String newParametersFolder) {
		if ( !customParamsFolder.equals(newParametersFolder) ) {
			customParamsFolder = newParametersFolder;
			isModified = true;
		}
	}

	public boolean useCustomParametersFolder () {
		return useCustomParamsFolder;
	}

	public void setUseCustomParametersFolder (boolean value) {
		if ( useCustomParamsFolder != value ) {
			useCustomParamsFolder = value;	
			isModified = true;
		}
	}
	
	public String getParametersFolder () {
		return getParametersFolder(useCustomParamsFolder, false);
	}
	
	public String getParametersFolder (boolean displayMode) {
		return getParametersFolder(useCustomParamsFolder, displayMode);
	}
	
	public String getParametersFolder (boolean useCustom,
		boolean displayMode)
	{
		if ( useCustom ) return customParamsFolder;
		// Else: use the same folder as the project
		String prjFolder = path;
		String folder = "";
		if ( prjFolder == null ) {
			if ( displayMode ) folder = "<User's home>: ";
			folder += System.getProperty("user.home");
		}
		else {
			if ( displayMode ) folder = "<This project's folder>: ";			
			folder += Util.getDirectoryName(prjFolder);
		}
		return folder;
	}
	
	/**
	 * Builds the full path for a target file.
	 * @param listIndex list to work with.
	 * @param relativeSourcePath
	 * @return the full path of the target file.
	 */
	public String buildTargetPath (int listIndex,
		String relativeSourcePath)
	{
		return pathBuilder.getPath(inputRoots.get(listIndex) + File.separator + relativeSourcePath,
			inputRoots.get(listIndex),
			(useOutputRoot ? outputRoot : null ),
			sourceLanguage,
			targetLanguage);
	}
	
	public String buildRelativeTargetPath (int listIndex,
		String relativeSourcePath)
	{
		String tmp = pathBuilder.getPath(inputRoots.get(listIndex) + File.separator + relativeSourcePath,
				inputRoots.get(listIndex),
			(useOutputRoot ? outputRoot : null ),
			sourceLanguage,
			targetLanguage);
		if ( useOutputRoot ) {
			return tmp.substring(inputRoots.get(listIndex).length());
		}
		else return tmp.substring(outputRoot.length());
	}
	
	public String buildOutputRoot (int listIndex) {
		if ( useOutputRoot ) return outputRoot;
		else return inputRoots.get(listIndex);
	}
	
	public String buildSourceEncoding (Input item) {
		return ((item.sourceEncoding.length() == 0)
			? sourceEncoding
			: item.sourceEncoding);
	}

	public String buildTargetEncoding (Input item) {
		return ((item.targetEncoding.length() == 0)
			? targetEncoding
			: item.targetEncoding);
	}
	
	public String[] getInputPaths (int listIndex) {
		String[] inputs = new String[inputLists.get(listIndex).size()];
		int i = -1;
		for ( Input item : inputLists.get(listIndex) ) {
			inputs[++i] = inputRoots.get(listIndex) + File.separator + item.relativePath;
		}
		return inputs;
	}
	
	public String getUtilityParameters (String utilityID) {
		if ( !utilityParams.containsKey(utilityID) ) return "";
		else return utilityParams.get(utilityID);
	}
	
	public void setUtilityParameters (String utilityID,
		String parameters)
	{
		utilityParams.put(utilityID, parameters);
	}
	
	public String getLastOutputFolder () {
		return lastOutputFolder;
	}
	
	public void setLastOutpoutFolder (String value) {
		lastOutputFolder = value;
	}
}
