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
