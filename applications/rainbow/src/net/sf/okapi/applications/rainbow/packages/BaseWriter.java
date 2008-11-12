/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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
import java.io.IOException;

import net.sf.okapi.applications.rainbow.lib.TMXWriter;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.SkeletonUnit;

public abstract class BaseWriter implements IWriter {
	
	protected Manifest  manifest;
	protected int       docID;
	protected String    inputRoot;
	protected String    relativeWorkPath;
	protected String    relativeSourcePath;
	protected String    relativeTargetPath;
	protected String    sourceEncoding;
	protected String    targetEncoding;
	protected String    filterID;
	protected TMXWriter tmxWriter;
	protected String    tmxPath;
	
	public BaseWriter () {
		manifest = new Manifest();
		manifest.setReaderClass(getReaderClass());
		tmxWriter = new TMXWriter();
	}
	
	public void setParameters (String sourceLanguage,
		String targetLanguage,
		String projectID,
		String outputFolder,
		String packageID,
		String sourceRoot)
	{
		manifest.setSourceLanguage(sourceLanguage);
		manifest.setTargetLanguage(targetLanguage);
		manifest.setProjectID(projectID);
		manifest.setRoot(outputFolder);
		manifest.setPackageID(packageID);
		manifest.setPackageType(getPackageType());
		this.inputRoot = sourceRoot;
	}

	public void writeStartPackage () {
		// Create the root directory
		Util.createDirectories(manifest.getRoot());
		
		String tmp = manifest.getSourceLocation();
		if (( tmp != null ) && ( tmp.length() > 0 )) {
			Util.createDirectories(manifest.getRoot() + File.separator + tmp + File.separator);
		}
		
		tmp = manifest.getTargetLocation();
		if (( tmp != null ) && ( tmp.length() > 0 )) {
			Util.createDirectories(manifest.getRoot() + File.separator + tmp + File.separator);
		}

		tmp = manifest.getOriginalLocation();
		if (( tmp != null ) && ( tmp.length() > 0 )) {
			Util.createDirectories(manifest.getRoot() + File.separator + tmp + File.separator);
		}

		// No need to create the folder structure for the 'done' folder
		// It will be done when merging
		
		// Create the reference TMX (pre-translations found in the source files)
		if ( tmxPath == null ) {
			tmxPath = manifest.getRoot() + File.separator + "fromOriginal.tmx";
		}
		tmxWriter.create(tmxPath);
		tmxWriter.writeStartDocument(manifest.getSourceLanguage(),
			manifest.getTargetLanguage(), null, null, null, null, null);
	}

	public void writeEndPackage (boolean createZip) {
		try {
			// Save the manifest
			if ( manifest != null ) {
				manifest.Save();
			}
			if ( createZip ) {
				// Zip the package if needed
				Compression.zipDirectory(manifest.getRoot(), manifest.getRoot() + ".zip");
			}
	
			tmxWriter.writeEndDocument();
			tmxWriter.close();
			if ( tmxWriter.getItemCount() == 0 ) {
				File file = new File(tmxPath);
				file.delete();
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void createDocument (int docID,
		String relativeSourcePath,
		String relativeTargetPath,
		String sourceEncoding,
		String targetEncoding,
		String filterID,
		IParameters filterParams)
	{
		if ( relativeSourcePath == null ) throw new NullPointerException();
		if ( relativeTargetPath == null ) throw new NullPointerException();
		if ( sourceEncoding == null ) throw new NullPointerException();
		if ( targetEncoding == null ) throw new NullPointerException();
		if ( filterID == null ) throw new NullPointerException();

		this.docID = docID;
		this.relativeSourcePath = relativeSourcePath;
		this.relativeTargetPath = relativeTargetPath;
		this.sourceEncoding = sourceEncoding;
		this.targetEncoding = targetEncoding;
		this.filterID = filterID;
		
		// If needed copy the original input to the package
		String subFolder = manifest.getOriginalLocation();
		if (( subFolder == null ) || ( subFolder.length() == 0 )) return;
				
		String inputPath = inputRoot + File.separator + relativeSourcePath;
		String docPrefix = String.format("%d.", docID);
			
		String destination = manifest.getRoot() + File.separator + subFolder
			+ File.separator + docPrefix + "ori"; // docPrefix has a dot
		Util.copyFile(inputPath, destination, false);
			
		String paramsCopy = manifest.getRoot() + File.separator + subFolder
			+ File.separator + "fprm";
		if ( filterParams != null ) {
			filterParams.save(paramsCopy);
		}
	}
	
	public void writeStartGroup (Group resource) {
		// Do nothing
	}
	
	public void writeEndGroup (Group resource) {
		// Do nothing
	}
	
	public void writeSkeletonUnit (SkeletonUnit resource) {
		// Do nothing)
	}
}
