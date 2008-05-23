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

import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.common.Util;

public abstract class BaseWriter implements IWriter {
	
	protected Manifest  manifest;
	protected ILog      log;
	
	public BaseWriter (ILog log) {
		this.log = log;
		manifest = new Manifest(log);
	}
	
	public void setParameters (String sourceLanguage,
		String targetLanguage,
		String projectID,
		String outputDir,
		String packageID)
	{
		manifest.setSourceLanguage(sourceLanguage);
		manifest.setTargetLanguage(targetLanguage);
		manifest.setProjectID(projectID);
		manifest.setRoot(outputDir);
		manifest.setPackageID(packageID);
		manifest.setPackageType(getPackageType());
	}

	public void writeStartPackage () {
		// Create the directory
		Util.createDirectories(manifest.getRoot());
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
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		}
	}
}
