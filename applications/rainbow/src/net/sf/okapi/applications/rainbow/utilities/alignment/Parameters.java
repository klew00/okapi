package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	protected String    tmxPath;
	protected boolean   segment;
	protected String    srxPath;
	protected boolean   singleInput;   
	protected boolean   allowEmptyTarget;   
	

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
		srxPath = tmp.get("srxPath", srxPath);
		singleInput = tmp.get("singleInput", singleInput);
		allowEmptyTarget = tmp.get("allowEmptyTarget", allowEmptyTarget);
	}

	@Override
	public void reset () {
		tmxPath = "output.tmx";
		segment = false;
		srxPath = "default.srx";
		singleInput = true;
		allowEmptyTarget = false;
	}

	@Override
	public String toString () {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("tmxPath", tmxPath);
		tmp.add("srxPath", srxPath);
		tmp.add("segment", segment);
		tmp.add("singleInput", singleInput);
		tmp.add("allowEmptyTarget", allowEmptyTarget);
		return tmp.toString();
	}
}
