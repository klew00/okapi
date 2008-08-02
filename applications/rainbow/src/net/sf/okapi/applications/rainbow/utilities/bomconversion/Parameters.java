package net.sf.okapi.applications.rainbow.utilities.bomconversion;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

	private boolean     removeBOM;
	private boolean     alsoNonUTF8;

	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString(String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		removeBOM = tmp.get("removeBOM", removeBOM);
		alsoNonUTF8 = tmp.get("alsoNonUTF8", alsoNonUTF8);
	}

	@Override
	public void reset() {
		removeBOM = false;
		alsoNonUTF8 = false;
	}

	@Override
	public String toString() {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("removeBOM", removeBOM);
		tmp.add("alsoNonUTF8", alsoNonUTF8);
		return tmp.toString();
	}
	
}
