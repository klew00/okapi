package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	public static final int  TYPE_KEEPORIGINAL   = 0;
	public static final int  TYPE_XNREPLACE      = 1;
	
	protected int       type;
	protected boolean   addPrefix;
	protected String    prefix;
	protected boolean   addSuffix;
	protected String    suffix;
	protected boolean   applyToExistingTarget;
	protected boolean   addName;
	
	
	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		type = tmp.get("type", type);
		addPrefix = tmp.get("addPrefix", addPrefix);
		prefix = tmp.get("prefix", prefix);
		addSuffix = tmp.get("addPrefix", addSuffix);
		suffix = tmp.get("suffix", suffix);
		applyToExistingTarget = tmp.get("applyToExistingTarget", applyToExistingTarget);
		addName = tmp.get("addName", addName);
	}

	@Override
	public void reset() {
		type = 0;
		addPrefix = false;
		prefix = "[[";
		addSuffix = false;
		suffix = "]]";
		applyToExistingTarget = false;
		addName = false;
	}

	@Override
	public String toString() {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("type", type);
		tmp.add("addPrefix", addPrefix);
		tmp.add("prefix", prefix);
		tmp.add("addSuffix", addSuffix);
		tmp.add("suffix", suffix);
		tmp.add("applyToExistingTarget", applyToExistingTarget);
		tmp.add("addName", addName);
		return tmp.toString();
	}
	
}
