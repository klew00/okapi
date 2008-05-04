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
		newPath = newPath.substring(inputRoot.length());
		
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
			writer.writeStartElement("extension");
			writer.writeAttributeString("use", (pathBuilder.useExtension() ? "1" : "0"));
			writer.writeAttributeString("style", String.format("%d", pathBuilder.getExtensionType()));
			writer.writeString(pathBuilder.getExtension());
			writer.writeEndElement(); // extension
			writer.writeEndElement(); // output

			writer.writeStartElement("options");
			writer.writeAttributeString("sourceLanguage", sourceLanguage);
			writer.writeAttributeString("sourceEncodinge", sourceEncoding);
			writer.writeAttributeString("targetLanguage", targetLanguage);
			writer.writeAttributeString("targetEncoding", targetEncoding);
			writer.writeEndElement(); // options
			
			writer.writeElementString("parametersProject", paramsFolder);
			
			writer.writeEndElement(); // rainbowProject
			writer.writeEndDocument();
			isModified = false;
			path = newPath;
		}
		finally {
			if ( writer != null ) writer.close();
		}
	}
	
	public void load (String newPath)
		throws Exception
	{
		//TODO
		isModified = false;
		path = newPath;
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
	
	public String buildTargetPath (String relativeSourcePath,
		String targetLanguage)
	{
		return pathBuilder.getPath(inputRoot + File.separator + relativeSourcePath,
			inputRoot,
			(useOutputRoot ? outputRoot : null ),
			targetLanguage);
	}
}
