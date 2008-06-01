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

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;

public abstract class BaseWriter implements IWriter {
	
	protected Manifest  manifest;
	protected int       docID;
	protected String    inputRoot;
	protected String    relativeInputPath;
	protected String    relativeOutputPath;
	
	public BaseWriter () {
		manifest = new Manifest();
	}
	
	public void setParameters (String sourceLanguage,
		String targetLanguage,
		String projectID,
		String outputDir,
		String packageID,
		String sourceRoot)
	{
		manifest.setSourceLanguage(sourceLanguage);
		manifest.setTargetLanguage(targetLanguage);
		manifest.setProjectID(projectID);
		manifest.setRoot(outputDir);
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
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void createDocument (int docID,
		String relativeInputPath,
		String relativeOutputPath,
		String inputEncoding,
		String outputEncoding,
		String filterSettings,
		IParameters filterParams)
	{
		if ( relativeInputPath == null ) throw new NullPointerException();
		if ( relativeOutputPath == null ) throw new NullPointerException();
		if ( inputEncoding == null ) throw new NullPointerException();
		if ( outputEncoding == null ) throw new NullPointerException();

		this.docID = docID;
		this.relativeInputPath = relativeInputPath;
		this.relativeOutputPath = relativeOutputPath;
		
		try {
			// If needed copy the original input to the package
			String tmp = manifest.getOriginalLocation();
			if (( tmp == null ) || ( tmp.length() == 0 )) return;
				
			String inputPath = inputRoot + File.separator + relativeInputPath;
			String docPrefix = String.format("%d.", docID);
			
			String destination = manifest.getRoot() + File.separator + tmp
				+ File.separator + docPrefix + ".ori";
			Util.copyFile(inputPath, destination, false);
			
			String name = filterParams.getPath();
			if ( name == null ) name = filterSettings + ".fprm"; // Defaults
			String paramsCopy = Util.getFilename(name, true);
			paramsCopy = manifest.getRoot() + File.separator + tmp
				+ File.separator + paramsCopy;
			filterParams.save(paramsCopy, docPrefix);
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
}
