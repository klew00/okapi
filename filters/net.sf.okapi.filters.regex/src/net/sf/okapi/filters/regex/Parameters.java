package net.sf.okapi.filters.regex;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

	protected boolean   extractOuterStrings;
	protected String    startString;
	protected String    endString;
	
	protected ArrayList<Rule>     rules;
	protected String              expression;
	

	public Parameters () {
		reset();
	}
	
	public void reset () {
		super.reset();
		rules = new ArrayList<Rule>();
		extractOuterStrings = false;
		startString = "\"";
		endString = "\"";
	}

	public String toString ()
	{
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("extractOuterStrings", extractOuterStrings);
		tmp.add("startString", startString);
		tmp.add("endString", endString);
		return tmp.toString();
	}
	
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);

		// Parse the fields
		extractOuterStrings = tmp.get("extractOuterStrings", extractOuterStrings);
		startString = tmp.get("startString", startString);
		endString = tmp.get("endString", endString);
	}
	
	public void compileRules () {
		StringBuilder tmp = new StringBuilder();
		for ( Rule rule : rules ) {
			if ( tmp.length() > 0 ) tmp.append("|");
			tmp.append("("+rule.start+")");
		}
		expression = tmp.toString();
	}
	
	
}
