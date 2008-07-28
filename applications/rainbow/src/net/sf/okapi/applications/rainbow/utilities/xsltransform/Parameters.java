package net.sf.okapi.applications.rainbow.utilities.xsltransform;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	/**
	 * Path of the XSLT document.
	 */
	private String      xsltPath;
	private String      paramList;
	

	public Parameters () {
		reset();
	}
	
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		xsltPath = tmp.get("xsltPath", xsltPath);
		paramList = tmp.get("paramList", paramList);
	}

	@Override
	public void reset () {
		xsltPath = "transform.xslt";
		paramList = "";
	}

	public String toString () {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("xsltPath", xsltPath);
		tmp.add("paramList", paramList);
		return tmp.toString();
	}
	
}
