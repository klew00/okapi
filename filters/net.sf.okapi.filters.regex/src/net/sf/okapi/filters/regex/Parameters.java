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

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

	protected boolean        extractOuterStrings;
	protected String         startString;
	protected String         endString;
	protected boolean        useBSlashEscape;
	
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
		useBSlashEscape = true;
	}

	public String toString ()
	{
		FieldsString tmp = new FieldsString();
		tmp.add("startString", startString);
		tmp.add("endString", endString);
		tmp.add("extractOuterStrings", extractOuterStrings);
		tmp.add("useBSlashEscape", useBSlashEscape);
		tmp.add("ruleCount", rules.size());
		for ( int i=0; i<rules.size(); i++ ) {
			tmp.addGroup(String.format("rule%d", i), rules.get(i).toString());
		}
		return tmp.toString();
	}
	
	public void fromString (String data) {
		FieldsString tmp = new FieldsString(data);
		startString = tmp.get("startString", startString);
		endString = tmp.get("endString", endString);
		extractOuterStrings = tmp.get("extractOuterStrings", extractOuterStrings);
		useBSlashEscape = tmp.get("useBSlashEscape", useBSlashEscape);
		Rule rule;
		int count = tmp.get("ruleCount", 0);
		for ( int i=0; i<count; i++ ) {
			rule = new Rule();
			rule.fromString(tmp.get(String.format("rule%d", i), null));
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
