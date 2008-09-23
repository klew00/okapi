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

package net.sf.okapi.applications.rainbow.utilities.extraction;

import java.io.File;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	/**
	 * Type of package.
	 */
	protected String    pkgType;
	
	/**
	 * True to zip the package. 
	 */
	protected boolean   createZip;
	
	/**
	 * True to generate also the data needed to merge back the
	 * extracted data.
	 */
	protected boolean   includeMergeData;
	
	/**
	 * Base-name of the package. 
	 */
	protected String    pkgName;
	
	/**
	 * Folder where to output the package.
	 */
	protected String    outputFolder;
	
	/**
	 * When possible, include target items in package.
	 */
	protected boolean   includeTargets;

	/**
	 * Pre-segment the output when possible.
	 */
	protected boolean   presegment;
	
	/**
	 * Path of the SRX file to use for source segmentation.
	 */
	protected String    sourceSRX;
	
	/**
	 * Path of the SRX file to use for target segmentation.
	 */
	protected String    targetSRX;


	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString(String data) {
		reset();
		super.fromString(data);
		pkgType = getString("pkgtype", pkgType);
		createZip = getBoolean("createzip", createZip);
		pkgName = getString("pkgname", pkgName);
		outputFolder = getString("outputfolder", pkgName);
		includeMergeData = getBoolean("includemergedata", includeMergeData);
		includeTargets = getBoolean("includetargets", includeTargets);
		presegment = getBoolean("presegment", presegment);
		sourceSRX = getString("sourceSRX", sourceSRX);
		targetSRX = getString("targetSRX", targetSRX);
	}

	@Override
	public void reset() {
		pkgType = "xliff";
		createZip = false;
		pkgName = "pack1";
		includeMergeData = false;
		outputFolder = System.getProperty("user.home") + File.separator
			+ "Localization Projects";
		includeTargets = true;
		presegment = false;
		sourceSRX = "";
		targetSRX = "";
	}

	@Override
	public String toString() {
		setString("pkgType", pkgType);
		setBoolean("createZip", createZip);
		setString("pkgname", pkgName);
		setString("outputfolder", outputFolder);
		setBoolean("includemergedata", includeMergeData);
		setBoolean("includetargets", includeTargets);
		setBoolean("presegment", presegment);
		setString("sourceSRX", sourceSRX);
		setString("targetSRX", targetSRX);
		return super.toString();
	}
	
	public String makePackageID () {
		//TODO: create package ID value
		return "TODO:packageID";
	}

}
