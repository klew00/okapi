package net.sf.okapi.applications.rainbow.utilities.linebreakconversion;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;
import net.sf.okapi.common.Util;

public class Parameters extends BaseParameters {

	private String      lineBreak;

	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString(String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		lineBreak = tmp.get("lineBreak", lineBreak);
	}

	@Override
	public void reset() {
		if ( (lineBreak = System.getProperty("line.separator") )
			== null ) lineBreak = Util.LINEBREAK_DOS;
	}

	@Override
	public String toString() {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("lineBreak", lineBreak);
		return tmp.toString();
	}
	
}
