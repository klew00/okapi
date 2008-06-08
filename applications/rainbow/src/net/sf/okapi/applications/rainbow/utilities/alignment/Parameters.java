package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	/**
	 * Full path of the TMX document to create.
	 */
	public String       tmxPath;
	

	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		tmxPath = tmp.get("tmxPath", tmxPath);
	}

	@Override
	public void reset () {
		tmxPath = "output.tmx";
	}

	@Override
	public String toString () {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("tmxPath", tmxPath);
		return tmp.toString();
	}
}
