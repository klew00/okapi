/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
============================================================================*/

package net.sf.okapi.filters.regex;

import java.util.ArrayList;
import java.util.regex.Pattern;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.LocalizationDirectives;

public class Parameters extends BaseParameters {

	public boolean extractOuterStrings;
	public String startString;
	public String endString;
	public boolean useBSlashEscape;
	public ArrayList<Rule> rules;
	public int regexOptions;
	public String expression;
	public LocalizationDirectives locDir;
	public String mimeType;

	public Parameters () {
		locDir = new LocalizationDirectives();
		reset();
		toString(); // fill the list
	}
	
	public void reset () {
		rules = new ArrayList<Rule>();
		regexOptions = Pattern.DOTALL | Pattern.MULTILINE;
		startString = "\"";
		endString = "\"";
		extractOuterStrings = false;
		useBSlashEscape = true;
		locDir.reset();
		mimeType = "text/plain";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		boolean tmpBool1 = buffer.getBoolean("useLD", locDir.useLD());
		boolean tmpBool2 = buffer.getBoolean("localizeOutside", locDir.localizeOutside());
		locDir.setOptions(tmpBool1, tmpBool2);
		startString = buffer.getString("startString", startString);
		endString = buffer.getString("endString", endString);
		extractOuterStrings = buffer.getBoolean("extractOuterStrings", extractOuterStrings);
		useBSlashEscape = buffer.getBoolean("useBSlashEscape", useBSlashEscape);
		regexOptions = buffer.getInteger("regexOptions", regexOptions);
		mimeType = buffer.getString("mimeType", mimeType);
		Rule rule;
		int count = buffer.getInteger("ruleCount", 0);
		for ( int i=0; i<count; i++ ) {
			rule = new Rule();
			rule.fromString(buffer.getGroup(String.format("rule%d", i), null));
			rules.add(rule);
		}
	}
	
	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("useLD", locDir.useLD());
		buffer.setBoolean("localizeOutside", locDir.localizeOutside());
		buffer.setString("startString", startString);
		buffer.setString("endString", endString);
		buffer.setBoolean("extractOuterStrings", extractOuterStrings);
		buffer.setBoolean("useBSlashEscape", useBSlashEscape);
		buffer.setInteger("regexOptions", regexOptions);
		buffer.setString("mimeType", mimeType);
		buffer.setInteger("ruleCount", rules.size());
		for ( int i=0; i<rules.size(); i++ ) {
			buffer.setGroup(String.format("rule%d", i), rules.get(i).toString());
		}
		return buffer.toString();
	}
	
	public void compileRules () {
		for ( Rule rule : rules ) {
			// Compile the full pattern
			rule.pattern = Pattern.compile(
				"("+rule.start+")(.*?)("+rule.end+")",
				regexOptions);
			// Compile any used in-line code rules for this rule
			if ( rule.useCodeFinder ) {
				rule.codeFinder.compile();
			}
		}
	}
	
	public ArrayList<Rule> getRules () {
		return rules;
	}
}
