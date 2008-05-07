package net.sf.okapi.utilities.textrewriting;

import net.sf.okapi.Library.Base.BaseParameters;
import net.sf.okapi.Library.Base.FieldsString;

public class Parameters extends BaseParameters {
	
	public static final int  ACTION_NOCHANGE     = 0;
	public static final int  ACTION_MASK         = 1;
	
	public int          action;

	public Parameters () {
		reset();
	}

	@Override
	public void fromString(String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		action = tmp.get("action", action);
	}

	@Override
	public void reset() {
		action = ACTION_MASK; //ACTION_NOCHANGE;
	}

	@Override
	public String toString() {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("action", action);
		return tmp.toString();
	}

}
