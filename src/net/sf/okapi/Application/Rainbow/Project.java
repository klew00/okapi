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

package net.sf.okapi.Application.Rainbow;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.sf.okapi.Format.XML.XMLWriter;
import net.sf.okapi.Library.Base.PathBuilder;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.LanguageManager;

public class Project {

	public String            inputRoot;
	public boolean           useOutputRoot;
	public String            outputRoot;
	public String            path;
	public ArrayList<Input>  inputList;
	public PathBuilder       pathBuilder;
	public String            sourceLanguage;
	public String            sourceEncoding;
	public String            targetLanguage;
	public String            targetEncoding;
	public String            paramsFolder;
	public boolean           isModified;
	
	public Project (LanguageManager lm) {
		inputRoot = System.getProperty("user.home");
		paramsFolder = inputRoot;
		useOutputRoot = false;
		outputRoot = "";
		inputList = new ArrayList<Input>();
		pathBuilder = new PathBuilder();
		pathBuilder.setExtension(".out");
		sourceLanguage = Utils.getDefaultSourceLanguage().toUpperCase();
		targetLanguage = Utils.getDefaultTargetLanguage().toUpperCase();
		sourceEncoding = lm.getDefaultEncodingFromCode(sourceLanguage, Utils.getPlatformType());
		targetEncoding = lm.getDefaultEncodingFromCode(targetLanguage, Utils.getPlatformType());
		isModified = false;
	}
	
	/**
	 * Adds a document to the project.
	 * @param newPath Full path of the document to add.
	 * @param sourceEncoding Default sourceEncoding for the document (can be null).
	 * @param filterSettings Filter settings string for the document (can be null). 
	 * @return 0=Document added, 1=bad root, 2=exists already
	 */
	public int addDocument (String newPath,
			String sourceEncoding,
			String targetEncoding,
		String filterSettings)
	{
		// Is the root OK?
		if ( newPath.indexOf(inputRoot) == -1 ) return 1;
		newPath = newPath.substring(inputRoot.length()+1); // No leading separator
		
		// Does the path exists already?
		for ( Input tmpInp : inputList ) {
			if ( tmpInp.relativePath.equalsIgnoreCase(newPath) ) return 2; 
		}
		
		// Create the new entry and add it to the list
		Input inp = new Input();
		inp.sourceEncoding = ((sourceEncoding == null) ? "" : sourceEncoding);
		inp.targetEncoding = ((targetEncoding == null) ? "" : targetEncoding);
		inp.filterSettings = ((filterSettings == null) ? "" : filterSettings);
		inp.relativePath = newPath;
		inputList.add(inp);
		isModified = true;
		return 0;
	}

	/**
	 * Gets an input item from the list, based on its relative path name.
	 * @param relativePath Relative path of the item to search for.
	 * @return An Input object or null.
	 */
	public Input getItemFromRelativePath (String relativePath) {
		for ( Input inp : inputList ) {
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
			
			writer.writeStartElement("fileSet");
			writer.writeAttributeString("id", "1");
			writer.writeElementString("root", inputRoot);
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
			
			writer.writeElementString("parametersFolder", paramsFolder);
			
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

			Element elem1 = getFirstElement(rootElem, "fileSet");
			if ( elem1 == null ) throw new Exception("Element <fileSet> missing.");
			
			Element elem2 = getFirstElement(elem1, "root");
			if ( elem2 == null ) throw new Exception("Element <root> missing.");
			inputRoot = elem2.getTextContent();
			
			NodeList nl = elem1.getElementsByTagName("fi");
			Input item;
			for ( int i=0; i<nl.getLength(); i++ ) {
				elem2 = (Element)nl.item(i);
				item = new Input();
				item.filterSettings = elem2.getAttribute("fs");
				item.format = elem2.getAttribute("fo");
				item.sourceEncoding = elem2.getAttribute("se");
				item.targetEncoding = elem2.getAttribute("te");
				item.relativePath = elem2.getTextContent();
				inputList.add(item);
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
			paramsFolder = elem1.getTextContent();

			isModified = false;
			path = newPath;
		}
		catch (Exception E ) {
			throw E;
		}
	}
	
	public void setInputRoot (String newRoot) {
		if ( !inputRoot.equals(newRoot) ) {
			inputRoot = newRoot;
			isModified = true;
		}
	}

	public void setUseOutputRoot (boolean value) {
		if ( useOutputRoot != value ) {
			useOutputRoot = value;
			isModified = true;
		}
	}

	public void setOutputRoot (String newRoot) {
		if ( !outputRoot.equals(newRoot) ) {
			outputRoot = newRoot;
			isModified = true;
		}
	}

	public void setSourceLanguage (String newLanguage) {
		if ( !sourceLanguage.equals(newLanguage) ) {
			sourceLanguage = newLanguage;
			isModified = true;
		}
	}
	
	public void setSourceEncoding (String newEncoding) {
		if ( !sourceEncoding.equals(newEncoding) ) {
			sourceEncoding = newEncoding;
			isModified = true;
		}
	}
	
	public void setTargetLanguage (String newLanguage) {
		if ( !targetLanguage.equals(newLanguage) ) {
			targetLanguage = newLanguage;
			isModified = true;
		}
	}
	
	public void setTargetEncoding (String newEncoding) {
		if ( !targetEncoding.equals(newEncoding) ) {
			targetEncoding = newEncoding;
			isModified = true;
		}
	}
	
	public void setParametersFolder (String newParameters) {
		if ( !paramsFolder.equals(newParameters) ) {
			paramsFolder = newParameters;
			isModified = true;
		}
	}
	
	/**
	 * Builds the full path for a target file.
	 * @param relativeSourcePath
	 * @return the full path of the target file.
	 */
	public String buildTargetPath (String relativeSourcePath)
	{
		return pathBuilder.getPath(inputRoot + File.separator + relativeSourcePath,
			inputRoot,
			(useOutputRoot ? outputRoot : null ),
			targetLanguage);
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
}
