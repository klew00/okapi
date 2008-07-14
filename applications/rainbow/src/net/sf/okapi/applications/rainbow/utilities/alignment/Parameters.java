package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	public String       tmxPath;
	public boolean      segment;
	public String       srxPath;
	

	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		tmxPath = tmp.get("tmxPath", tmxPath);
		segment = tmp.get("segment", segment);
		srxPath = tmp.get("srxPath", tmxPath);
	}

	@Override
	public void reset () {
		tmxPath = "output.tmx";
		segment = false;
		srxPath = "default.srx";
	}

	@Override
	public String toString () {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("tmxPath", tmxPath);
		tmp.add("srxPath", srxPath);
		tmp.add("segment", segment);
		return tmp.toString();
	}
}
