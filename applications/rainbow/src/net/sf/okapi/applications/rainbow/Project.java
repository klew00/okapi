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

	protected ArrayList<ArrayList<Input>> inputLists;
	protected String path;
	protected PathBuilder pathBuilder;
	protected boolean isModified;
	
	private ArrayList<String> inputRoots;
	private ArrayList<Boolean> useCustomInputRoots;
	private Hashtable<String, String> utilityParams;
	private boolean useOutputRoot;
	private String outputRoot;
	private String sourceLanguage;
	private String sourceEncoding;
	private String targetLanguage;
	private String targetEncoding;
	private boolean useCustomParamsFolder;
	private String customParamsFolder;
	private String lastOutputFolder;

	public Project (LanguageManager lm) {
		useCustomParamsFolder = false;
		customParamsFolder = System.getProperty("user.home");
		
		useOutputRoot = false;
		outputRoot = "";
		
		// Three lists
		inputLists = new ArrayList<ArrayList<Input>>();
		useCustomInputRoots = new ArrayList<Boolean>();
		inputRoots = new ArrayList<String>();
		
		inputLists.add(new ArrayList<Input>());
		inputRoots.add(System.getProperty("user.home"));
		useCustomInputRoots.add(false);
		
		inputLists.add(new ArrayList<Input>());
		inputRoots.add(System.getProperty("user.home"));
		useCustomInputRoots.add(false);
		
		inputLists.add(new ArrayList<Input>());
		inputRoots.add(System.getProperty("user.home"));
		useCustomInputRoots.add(false);
		
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
	 * @param allowDuplicates True to allow adding a path that is already there,
	 * false to not add the file and return 2 if it is a duplicate. 
	 * @return 0=Document added, 1=bad root, 2=exists already
	 */
	public int addDocument (int listIndex,
		String newPath,
		String sourceEncoding,
		String targetEncoding,
		String filterSettings,
		boolean allowDuplicates)
	{
		// Is the root OK?
		String inputRoot = getInputRoot(listIndex);
		if ( newPath.indexOf(inputRoot) == -1 ) return 1;
		newPath = newPath.substring(inputRoot.length()+1); // No leading separator
		
		// Does the path exists already?
		if ( !allowDuplicates ) {
			for ( Input tmpInp : inputLists.get(listIndex) ) {
				if ( tmpInp.relativePath.equalsIgnoreCase(newPath) ) return 2;
			}
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
	
	/**
	 * Gets the last input item from the list.
	 * @param listIndex Index of the input list where perform the operation.
	 * @return An input object or null.
	 */
	public Input getLastItem (int listIndex) {
		if ( inputLists.get(listIndex).size() == 0 ) return null;
		return inputLists.get(listIndex).get(inputLists.get(listIndex).size()-1);
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
				writer.writeStartElement("root");
				writer.writeAttributeString("useCustom", useCustomInputRoots.get(i) ? "1" : "0");
				writer.writeString(inputRoots.get(i).replace('\\', '/'));
				writer.writeEndElement(); // root
				for ( Input item : inputList ) {
					writer.writeStartElement("fi");
					writer.writeAttributeString("fs", item.filterSettings);
					writer.writeAttributeString("fo", item.format);
					writer.writeAttributeString("se", item.sourceEncoding);
					writer.writeAttributeString("te", item.targetEncoding);
					writer.writeString(item.relativePath.replace('\\', '/'));
					writer.writeEndElement(); // fi
				}
				writer.writeEndElement(); // fileSet
				i++;
			}
			
			writer.writeStartElement("output");
			writer.writeStartElement("root");
			writer.writeAttributeString("use", (useOutputRoot ? "1" : "0"));
			writer.writeString(outputRoot.replace('\\', '/'));
			writer.writeEndElement(); // root
			writer.writeStartElement("subFolder");
			writer.writeAttributeString("use", (pathBuilder.useSubfolder() ? "1" : "0"));
			writer.writeString(pathBuilder.getSubfolder().replace('\\', '/'));
			writer.writeEndElement(); // subFolder
			writer.writeStartElement("extension");
			writer.writeAttributeString("use", (pathBuilder.useExtension() ? "1" : "0"));
			writer.writeAttributeString("style", String.format("%d", pathBuilder.getExtensionType()));
			writer.writeString(pathBuilder.getExtension()); 
			writer.writeEndElement(); // extension
			writer.writeStartElement("replace");
			writer.writeAttributeString("use", (pathBuilder.useReplace() ? "1" : "0"));
			writer.writeAttributeString("oldText", pathBuilder.getSearch().replace('\\', '/'));
			writer.writeAttributeString("newText", pathBuilder.getReplace().replace('\\', '/'));
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
			writer.writeString(customParamsFolder.replace('\\', '/'));
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

	/**
	 * Gets the first element of a given name for a given parent.
	 * @param parent The parent element.
	 * @param name The name of the element to search for.
	 * @return The first element fount, or null if none is found.
	 */
	private Element getFirstElement (Element parent,
		String name)
	{
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
			File file = new File(newPath);
			Document doc = Fact.newDocumentBuilder().parse(file);
			
			Element rootElem = doc.getDocumentElement();
			String tmp = rootElem.getAttribute("version");
			if ( !tmp.equals("4") ) {
				throw new Exception("Unsupported version of the project file.");
			}

			Element elem1;
			Element elem2;
			NodeList n1 = rootElem.getElementsByTagName("fileSet");
			for ( int i=0; i<n1.getLength(); i++ ) {
				elem1 = (Element)n1.item(i);

				elem2 = getFirstElement(elem1, "root");
				if ( elem2 == null ) throw new Exception("Element <root> missing.");
				// Use !=='0' for backward compatibility: empty value will == custom
				useCustomInputRoots.set(i, !elem2.getAttribute("useCustom").equals("0"));
				inputRoots.set(i, Util.getTextContent(elem2).replace('/', File.separatorChar));

				NodeList n2 = elem1.getElementsByTagName("fi");
				Input item;
				for ( int j=0; j<n2.getLength(); j++ ) {
					elem2 = (Element)n2.item(j);
					item = new Input();
					item.filterSettings = elem2.getAttribute("fs");
					item.format = elem2.getAttribute("fo");
					item.sourceEncoding = elem2.getAttribute("se");
					item.targetEncoding = elem2.getAttribute("te");
					item.relativePath = Util.getTextContent(elem2).replace('/', File.separatorChar);
					inputLists.get(i).add(item);
				}
			}

			elem1 = getFirstElement(rootElem, "output");
			if ( elem1 == null ) throw new Exception("Element <output> missing.");
			
			elem2 = getFirstElement(elem1, "root");
			if ( elem2 != null ) {
				useOutputRoot = elem2.getAttribute("use").equals("1");
				outputRoot = Util.getTextContent(elem2).replace('/', File.separatorChar);
			}
			
			elem2 = getFirstElement(elem1, "subFolder");
			if ( elem2 != null ) {
				pathBuilder.setUseSubfolder(elem2.getAttribute("use").equals("1"));
				pathBuilder.setSubfolder(Util.getTextContent(elem2).replace('/', File.separatorChar));
			}
			
			elem2 = getFirstElement(elem1, "extension");
			if ( elem2 != null ) {
				pathBuilder.setUseExtension(elem2.getAttribute("use").equals("1"));
				int n = Integer.valueOf(elem2.getAttribute("style"));
				if (( n < 0 ) || ( n > 2 )) n = 2; // Sanity check
				pathBuilder.setExtensionType(n);
				pathBuilder.setExtension(Util.getTextContent(elem2));
			}

			elem2 = getFirstElement(elem1, "replace");
			if ( elem2 != null ) {
				pathBuilder.setUseReplace(elem2.getAttribute("use").equals("1"));
				pathBuilder.setSearch(elem2.getAttribute("oldText").replace('/', File.separatorChar));
				pathBuilder.setReplace(elem2.getAttribute("newText").replace('/', File.separatorChar));
			}

			elem2 = getFirstElement(elem1, "prefix");
			if ( elem2 != null ) {
				pathBuilder.setUsePrefix(elem2.getAttribute("use").equals("1"));
				pathBuilder.setPrefix(Util.getTextContent(elem2));
			}

			elem2 = getFirstElement(elem1, "suffix");
			if ( elem2 != null ) {
				pathBuilder.setUseSuffix(elem2.getAttribute("use").equals("1"));
				pathBuilder.setSuffix(Util.getTextContent(elem2));
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
			customParamsFolder = Util.getTextContent(elem1).replace('/', File.separatorChar);
			
			// Parameters for the utilities
			elem1 = getFirstElement(rootElem, "utilities");
			if ( elem1 != null ) {
				n1 = rootElem.getElementsByTagName("params");
				for ( int i=0; i<n1.getLength(); i++ ) {
					elem2 = (Element)n1.item(i);
					utilityParams.put(elem2.getAttribute("id"), Util.getTextContent(elem2));
				}
			}

			isModified = false;
			// Make sure we set the absolute path, as it may be used for root
			path = file.getAbsolutePath();
		}
		catch (Exception E ) {
			throw E;
		}
	}

	/**
	 * Sets the input root for a given list.
	 * @param listIndex Index of the list to set.
	 * @param newRoot The new root (If null or empty: use the project's folder).
	 * @param useCustom True to use the passed newRoot, false to use the auto-root.
	 */
	public void setInputRoot (int listIndex,
		String newRoot,
		boolean useCustom)
	{
		// Empty or null root = auto root.
		if (( newRoot == null ) || ( newRoot.length() == 0 )) {
			useCustom = false;
		}
		// Set the root and the flag
		if ( useCustom ) {
			if ( !useCustomInputRoots.get(listIndex) ) isModified = true;
			useCustomInputRoots.set(listIndex, true);
			if ( !inputRoots.get(listIndex).equals(newRoot) ) {
				inputRoots.set(listIndex, newRoot);
				isModified = true;
			}
		}
		else {
			if ( useCustomInputRoots.get(listIndex) ) {
				useCustomInputRoots.set(listIndex, false);
				isModified = true;
			}
		}
	}
	
	public String getInputRoot (int listIndex) {
		if ( useCustomInputRoots.get(listIndex) ) {
			return inputRoots.get(listIndex);
		}
		// Else: use the same folder as the project
		if ( path == null ) {
			return System.getProperty("user.home");
		}
		return Util.getDirectoryName(path);
	}
	
	public String getInputRootDisplay (int listIndex) {
		if ( useCustomInputRoots.get(listIndex) ) {
			return "<Custom>:  " + inputRoots.get(listIndex);
		}
		// Else: use the same folder as the project
		return "<Auto>:  " + ((path == null)
			? System.getProperty("user.home")
			: Util.getDirectoryName(path));
	}
	
	public boolean useCustomeInputRoot (int listIndex) {
		return useCustomInputRoots.get(listIndex);
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
			if ( displayMode ) folder = "<Auto>:  ";
			folder += System.getProperty("user.home");
		}
		else {
			if ( displayMode ) folder = "<Auto>:  ";			
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
		String inputRoot = getInputRoot(listIndex);
		return pathBuilder.getPath(inputRoot + File.separator + relativeSourcePath,
			inputRoot,
			(useOutputRoot ? outputRoot : null ),
			sourceLanguage,
			targetLanguage);
	}
	
	public String buildRelativeTargetPath (int listIndex,
		String relativeSourcePath)
	{
		String inputRoot = getInputRoot(listIndex);
		String tmp = pathBuilder.getPath(inputRoot + File.separator + relativeSourcePath,
			inputRoot,
			(useOutputRoot ? outputRoot : null ),
			sourceLanguage,
			targetLanguage);
		if ( useOutputRoot ) {
			return tmp.substring(inputRoot.length());
		}
		else return tmp.substring(outputRoot.length());
	}
	
	public String buildOutputRoot (int listIndex) {
		if ( useOutputRoot ) return outputRoot;
		else return getInputRoot(listIndex);
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
			inputs[++i] = getInputRoot(listIndex) + File.separator + item.relativePath;
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
		String prev = utilityParams.get(utilityID);
		if ( prev != null ) {
			if ( !prev.equals(parameters) ) isModified = true;
		}
		utilityParams.put(utilityID, parameters);
	}
	
	public String getLastOutputFolder () {
		return lastOutputFolder;
	}
	
	public void setLastOutpoutFolder (String value) {
		// Do not set isModified as lastOutputFolder is not saved
		lastOutputFolder = value;
	}
	
	public String getProjectFolder () {
		if ( path == null ) return System.getProperty("user.home"); 
		else return Util.getDirectoryName(path);
	}

}
