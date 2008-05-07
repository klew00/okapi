package net.sf.okapi.utilities.extraction;

import java.io.File;

import net.sf.okapi.Library.Base.BaseParameters;
import net.sf.okapi.Library.Base.FieldsString;

public class Parameters extends BaseParameters {
	
	public String       pkgType;
	public boolean      createZip;
	public boolean      includeMergeData;
	public String       pkgName;
	public String       outputFolder;

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
	}

	@Override
	public void reset() {
		pkgType = "omegat";
		createZip = false;
		pkgName = "pack1";
		includeMergeData = false;
		outputFolder = System.getProperty("user.home") + File.separator + "localization projects";
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
		return tmp.toString();
	}
	
	public String makePackageID () {
		//TODO
		return "TODO:packageID";
	}

}
