package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.resource.IContainer;

public class InlineCodeFinder {

	private ArrayList<String>     rules;
	private String                sample;
	private Pattern               pattern;

	
	public InlineCodeFinder () {
		rules = new ArrayList<String>();
	}
	
	public ArrayList<String> getRules () {
		return rules;
	}
	
	public String getSample () {
		return sample;
	}
	
	public void setSample (String value) {
		sample = value;
	}
	
	public void compile () {
		StringBuilder tmp = new StringBuilder();
		for ( String rule : rules ) {
			if ( tmp.length() > 0 ) tmp.append("|");
			tmp.append("("+rule+")");
		}
		pattern = Pattern.compile(tmp.toString(), Pattern.MULTILINE);
	}

	public void process (IContainer segment) {
		String tmp = segment.getCodedText();
		Matcher m = pattern.matcher(tmp);
		int start = 0;
		while ( m.find(start) ) {
			//segment.changeToCode(m.start(), m.end(), IContainer2.CODE_ISOLATED, ++id)
			start = m.end();
			if ( start >= tmp.length() ) break;
		}
	}

}
