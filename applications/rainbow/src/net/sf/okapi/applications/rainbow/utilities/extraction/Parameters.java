/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities.extraction;

import java.io.File;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	/**
	 * Type of package.
	 */
	protected String pkgType;
	
	/**
	 * True to zip the package. 
	 */
	protected boolean createZip;
	
	/**
	 * Base-name of the package. 
	 */
	protected String pkgName;
	
	/**
	 * Folder where to output the package.
	 */
	protected String outputFolder;
	
	/**
	 * When possible, include target items in package.
	 */
	protected boolean includeTargets;

	/**
	 * Pre-segment the output when possible.
	 */
	protected boolean preSegment;
	
	/**
	 * Path of the SRX file to use for source segmentation.
	 */
	protected String sourceSRX;
	
	/**
	 * Path of the SRX file to use for target segmentation.
	 */
	protected String targetSRX;

	/**
	 * Indicates if the output should be pre-translated.
	 */
	protected boolean preTranslate;
	
	/**
	 * Path of the TM fiel to use for pre-translation.
	 */
	protected String tmPath;

	public Parameters () {
		reset();
	}
	
	public void reset() {
		pkgType = "xliff";
		createZip = false;
		pkgName = "pack1";
		outputFolder = System.getProperty("user.home") + File.separator
			+ "Localization Projects";
		includeTargets = true;
		preSegment = false;
		sourceSRX = "";
		targetSRX = "";
		preTranslate = false;
		tmPath = "";
	}

	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		pkgType = buffer.getString("pkgType", pkgType);
		createZip = buffer.getBoolean("createZip", createZip);
		pkgName = buffer.getString("pkgName", pkgName);
		outputFolder = buffer.getString("outputFolder", outputFolder);
		includeTargets = buffer.getBoolean("includeTargets", includeTargets);
		preSegment = buffer.getBoolean("preSegment", preSegment);
		sourceSRX = buffer.getString("sourceSRX", sourceSRX);
		targetSRX = buffer.getString("targetSRX", targetSRX);
		preTranslate = buffer.getBoolean("preTranslate", preTranslate);
		tmPath = buffer.getString("tmPath", tmPath);
	}

	public String toString() {
		buffer.reset();
		buffer.setString("pkgType", pkgType);
		buffer.setBoolean("createZip", createZip);
		buffer.setString("pkgName", pkgName);
		buffer.setString("outputFolder", outputFolder);
		buffer.setBoolean("includeTargets", includeTargets);
		buffer.setBoolean("preSegment", preSegment);
		buffer.setString("sourceSRX", sourceSRX);
		buffer.setString("targetSRX", targetSRX);
		buffer.setBoolean("preTranslate", preTranslate);
		buffer.setString("tmPath", tmPath);
		return buffer.toString();
	}
	
	public String makePackageID () {
		//TODO: create package ID value
		return "TODO:packageID";
	}

}
