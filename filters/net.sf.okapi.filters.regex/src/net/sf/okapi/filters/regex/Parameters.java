/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.regex;

import java.util.ArrayList;
import java.util.regex.Pattern;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.LocalizationDirectives;

public class Parameters extends BaseParameters {

	protected boolean                  extractOuterStrings;
	protected String                   startString;
	protected String                   endString;
	protected boolean                  useBSlashEscape;
	protected ArrayList<Rule>          rules;
	protected int                      regexOptions;
	protected String                   expression;
	protected LocalizationDirectives   locDir;
	

	public Parameters () {
		locDir = new LocalizationDirectives();
		reset();
	}
	
	public void reset () {
		super.reset();
		rules = new ArrayList<Rule>();
		regexOptions = Pattern.DOTALL | Pattern.MULTILINE;
		startString = "\"";
		endString = "\"";
		extractOuterStrings = false;
		useBSlashEscape = true;
		locDir.reset();
	}

	@Override
	public String toString () {
		setBoolean("useLD", locDir.useLD());
		setBoolean("localizeOutside", locDir.localizeOutside());
		setString("startString", startString);
		setString("endString", endString);
		setBoolean("extractOuterStrings", extractOuterStrings);
		setBoolean("useBSlashEscape", useBSlashEscape);
		setInteger("regexOptions", regexOptions);
		setInteger("ruleCount", rules.size());
		for ( int i=0; i<rules.size(); i++ ) {
			setGroup(String.format("rule%d", i), rules.get(i).toString());
		}
		return super.toString();
	}
	
	public void fromString (String data) {
		reset();
		super.fromString(data);
		boolean tmpBool1 = getBoolean("useLD", locDir.useLD());
		boolean tmpBool2 = getBoolean("localizeOutside", locDir.localizeOutside());
		locDir.setOptions(tmpBool1, tmpBool2);
		startString = getString("startString", startString);
		endString = getString("endString", endString);
		extractOuterStrings = getBoolean("extractOuterStrings", extractOuterStrings);
		useBSlashEscape = getBoolean("useBSlashEscape", useBSlashEscape);
		regexOptions = getInteger("regexOptions", regexOptions);
		Rule rule;
		int count = getInteger("ruleCount", 0);
		for ( int i=0; i<count; i++ ) {
			rule = new Rule();
			rule.fromString(getGroup(String.format("rule%d", i), null));
			rules.add(rule);
		}
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
