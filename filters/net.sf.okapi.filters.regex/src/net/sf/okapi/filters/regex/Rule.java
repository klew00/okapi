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

import net.sf.okapi.common.FieldsString;
import net.sf.okapi.common.filters.InlineCodeFinder;

public class Rule {

	public static final int       RULETYPE_STRING     = 0;
	public static final int       RULETYPE_CONTENT    = 1;
	public static final int       RULETYPE_COMMENT    = 2;
	public static final int       RULETYPE_NOTRANS    = 3;
	public static final int       RULETYPE_OPENGROUP  = 4;
	public static final int       RULETYPE_CLOSEGROUP = 5;
	
	protected String              ruleName;
	protected String              start;
	protected String              end;
	protected String              nameStart;
	protected String              nameEnd;
	protected String              nameFormat;
	protected String              splitters;
	protected int                 ruleType;
	protected boolean             preserveWS;
	protected boolean             useCodeFinder;
	protected InlineCodeFinder    codeFinder;

	public Rule () {
		start = "";
		end = "";
		nameStart = "";
		nameEnd = "";
		nameFormat = "";
		codeFinder = new InlineCodeFinder();
	}
	
	public Rule (Rule obj) {
		ruleName = obj.ruleName;
		start = obj.start;
		end = obj.end;
		nameStart = obj.nameStart;
		nameEnd = obj.nameEnd;
		nameFormat = obj.nameFormat;
		splitters = obj.splitters;
		ruleType = obj.ruleType;
		preserveWS = obj.preserveWS;
		useCodeFinder = obj.useCodeFinder;
		codeFinder = obj.codeFinder.clone();
	}
	
	public String getRuleName () {
		return ruleName;
	}
	
	public void setRuleName (String value) {
		ruleName = value;
	}
	
	public String getStart () {
		return start;
	}
	
	public void setStart (String value) {
		if ( value == null ) throw new NullPointerException();
		start = value;
	}

	public String getEnd () {
		return end;
	}
	
	public void setEnd (String value) {
		if ( value == null ) throw new NullPointerException();
		end = value;
	}

	public String getNameStart () {
		return nameStart;
	}
	
	public void setNameStart (String value) {
		if ( value == null ) throw new NullPointerException();
		nameStart = value;
	}

	public String getNameEnd () {
		return nameEnd;
	}
	
	public void setNameEnd (String value) {
		if ( value == null ) throw new NullPointerException();
		nameEnd = value;
	}

	public String getNameFormat () {
		return nameFormat;
	}
	
	public void setNameFormat (String value) {
		if ( value == null ) throw new NullPointerException();
		nameFormat = value;
	}

	public String getSplitters () {
		return splitters;
	}
	
	public void setSplitters (String value) {
		splitters = value;
	}
	
	public int getRuleType () {
		return ruleType;
	}
	
	public void setRuleType (int value) {
		ruleType = value;
	}
	
	public boolean preserveSpace () {
		return preserveWS;
	}
	
	public void setPreserveSpace (boolean value) {
		preserveWS = value;
	}

	public boolean useCodeFinder () {
		return useCodeFinder;
	}
	
	public void setUseCodeFinder (boolean value) {
		useCodeFinder = value;
	}

	@Override
	public String toString () {
		FieldsString tmp = new FieldsString();
		tmp.add("ruleName", ruleName);
		tmp.add("ruleType", ruleType);
		tmp.add("start", start);
		tmp.add("end", end);
		tmp.add("nameStart", nameStart);
		tmp.add("nameEnd", nameEnd);
		tmp.add("nameFormat", nameFormat);
		tmp.add("preserveWS", preserveWS);
		tmp.add("useCodeFinder", useCodeFinder);
		//TODO: save finder rules
		return tmp.toString();
	}
	
	public void fromString (String data) {
		FieldsString tmp = new FieldsString(data);
		ruleName = tmp.get("ruleName", ruleName);
		ruleType = tmp.get("ruleType", ruleType);
		start = tmp.get("start", start);
		end = tmp.get("end", end);
		nameStart = tmp.get("nameStart", nameStart);
		nameEnd = tmp.get("nameEnd", nameEnd);
		nameFormat = tmp.get("nameFormat", nameFormat);
		preserveWS = tmp.get("preserveWS", preserveWS);
		useCodeFinder = tmp.get("useCodeFinder", useCodeFinder);
		//TODO: get rules for finder
	}
	
}
