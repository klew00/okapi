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
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	/**
	 * Type of package.
	 */
	public String       pkgType;
	
	/**
	 * True to zip the package. 
	 */
	public boolean      createZip;
	
	/**
	 * True to generate also the data needed to merge back the
	 * extracted data.
	 */
	public boolean      includeMergeData;
	
	/**
	 * Base-name of the package. 
	 */
	public String       pkgName;
	
	/**
	 * Folder where to output the package.
	 */
	public String       outputFolder;
	
	/**
	 * When possible, include target items in package.
	 */
	public boolean      includeTargets;

	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString(String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		pkgType = tmp.get("pkgtype", pkgType);
		createZip = tmp.get("createzip", createZip);
		pkgName = tmp.get("pkgname", pkgName);
		outputFolder = tmp.get("outputfolder", pkgName);
		includeMergeData = tmp.get("includemergedata", includeMergeData);
		includeTargets = tmp.get("includetargets", includeTargets);
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
	}

	@Override
	public String toString() {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("pkgType", pkgType);
		tmp.add("createZip", createZip);
		tmp.add("pkgname", pkgName);
		tmp.add("outputfolder", outputFolder);
		tmp.add("includemergedata", includeMergeData);
		tmp.add("includetargets", includeTargets);
		return tmp.toString();
	}
	
	public String makePackageID () {
		//TODO: create package ID value
		return "TODO:packageID";
	}

}
