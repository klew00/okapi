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

import java.util.regex.Pattern;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.filters.InlineCodeFinder;

public class Rule {

	public static final int       RULETYPE_STRING     = 0;
	public static final int       RULETYPE_CONTENT    = 1;
	public static final int       RULETYPE_COMMENT    = 2;
	public static final int       RULETYPE_NOTRANS    = 3;
	public static final int       RULETYPE_OPENGROUP  = 4;
	public static final int       RULETYPE_CLOSEGROUP = 5;
	
	protected String ruleName;
	protected String start;
	protected String end;
	protected String nameStart;
	protected String nameEnd;
	protected String nameFormat;
	protected int ruleType;
	protected boolean preserveWS;
	protected boolean unwrap;
	protected boolean useCodeFinder;
	protected InlineCodeFinder codeFinder;
	protected String propertyName;
	protected String propertyValue;
	protected String sample;
	// Runtime-only variable (don't serialize)
	protected Pattern pattern;

	public Rule () {
		start = "";
		end = "";
		nameStart = "";
		nameEnd = "";
		nameFormat = "";
		codeFinder = new InlineCodeFinder();
		preserveWS = true;
		unwrap = false;
		sample = "";
	}
	
	public Rule (Rule obj) {
		ruleName = obj.ruleName;
		start = obj.start;
		end = obj.end;
		nameStart = obj.nameStart;
		nameEnd = obj.nameEnd;
		nameFormat = obj.nameFormat;
		ruleType = obj.ruleType;
		preserveWS = obj.preserveWS;
		unwrap = obj.unwrap;
		useCodeFinder = obj.useCodeFinder;
		codeFinder = obj.codeFinder.clone();
		propertyName = obj.propertyName;
		propertyValue = obj.propertyValue;
		sample = obj.sample;
		pattern = obj.pattern;
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

	public String getSample () {
		return sample;
	}
	
	public void setSample (String value) {
		if ( value == null ) sample = "";
		else sample = value;
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

	public int getRuleType () {
		return ruleType;
	}
	
	public void setRuleType (int value) {
		ruleType = value;
	}
	
	public boolean preserveWS () {
		return preserveWS;
	}
	
	public void setPreserveWS (boolean value) {
		preserveWS = value;
	}

	public boolean unwrap () {
		return unwrap;
	}
	
	public void setUnwrap (boolean value) {
		unwrap = value;
	}
	
	public String getPropertyName () {
		return propertyName;
	}
	
	public void setPropertyName (String value) {
		propertyName = value;
	}

	public String getPropertyValue () {
		return propertyValue;
	}
	
	public void setPropertyValue (String value) {
		propertyValue = value;
	}

	public boolean useCodeFinder () {
		return useCodeFinder;
	}
	
	public void setUseCodeFinder (boolean value) {
		useCodeFinder = value;
	}
	
	public String getCodeFinderRules () {
		return codeFinder.toString();
	}
	
	public void setCodeFinderRules (String value) {
		codeFinder.fromString(value);
	}
	
	@Override
	public String toString () {
		ParametersString tmp = new ParametersString();
		tmp.setString("ruleName", ruleName);
		tmp.setInteger("ruleType", ruleType);
		tmp.setString("start", start);
		tmp.setString("end", end);
		tmp.setString("nameStart", nameStart);
		tmp.setString("nameEnd", nameEnd);
		tmp.setString("nameFormat", nameFormat);
		tmp.setBoolean("preserveWS", preserveWS);
		tmp.setBoolean("unwrap", unwrap);
		tmp.setBoolean("useCodeFinder", useCodeFinder);
		tmp.setString("propertyName", propertyName);
		tmp.setString("propertyValue", propertyValue);
		tmp.setString("sample", sample);
		tmp.setGroup("codeFinderRules", codeFinder.toString());
		return tmp.toString();
	}
	
	public void fromString (String data) {
		ParametersString tmp = new ParametersString(data);
		ruleName = tmp.getString("ruleName", ruleName);
		ruleType = tmp.getInteger("ruleType", ruleType);
		start = tmp.getString("start", start);
		end = tmp.getString("end", end);
		nameStart = tmp.getString("nameStart", nameStart);
		nameEnd = tmp.getString("nameEnd", nameEnd);
		nameFormat = tmp.getString("nameFormat", nameFormat);
		preserveWS = tmp.getBoolean("preserveWS", preserveWS);
		unwrap = tmp.getBoolean("unwrap", unwrap);
		propertyName = tmp.getString("propertyName", propertyName);
		propertyValue = tmp.getString("propertyValue", propertyValue);
		sample = tmp.getString("sample", sample);
		useCodeFinder = tmp.getBoolean("useCodeFinder", useCodeFinder);
		codeFinder.fromString(tmp.getGroup("codeFinderRules", ""));
	}
	
}
