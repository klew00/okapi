package net.sf.okapi.filters.regex;

import java.util.ArrayList;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

	protected boolean        extractOuterStrings;
	protected String         startString;
	protected String         endString;
	
	protected ArrayList<Rule>     rules;
	protected String              expression;
	

	public Parameters () {
		reset();
	}
	
	public void reset () {
		super.reset();
		rules = new ArrayList<Rule>();
		startString = "\"";
		endString = "\"";
		extractOuterStrings = false;
	}

	public String toString ()
	{
		FieldsString tmp = new FieldsString();
		tmp.add("startString", startString);
		tmp.add("endString", endString);
		tmp.add("extractOuterStrings", extractOuterStrings);
		return tmp.toString();
	}
	
	public void fromString (String data) {
		FieldsString tmp = new FieldsString(data);
		startString = tmp.get("startString", startString);
		endString = tmp.get("endString", endString);
		extractOuterStrings = tmp.get("extractOuterStrings", extractOuterStrings);
	}
	
	public void compileRules () {
		StringBuilder tmp = new StringBuilder();
		for ( Rule rule : rules ) {
			if ( tmp.length() > 0 ) tmp.append("|");
			tmp.append("("+rule.start+")");
		}
		expression = tmp.toString();
	}
	
	public ArrayList<Rule> getRules () {
		return rules;
	}
}
