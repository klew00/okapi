/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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
===========================================================================*/

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
	protected String expr;
	protected int ruleType;
	protected boolean preserveWS;
	protected boolean useCodeFinder;
	protected InlineCodeFinder codeFinder;
	protected String propertyName;
	protected String propertyValue;
	protected String sample;
	protected int sourceGroup;
	protected int targetGroup;
	protected int nameGroup;
	protected int noteGroup;
	// Runtime-only variable (don't serialize)
	protected Pattern pattern;

	public Rule () {
		ruleName = "";
		expr = "";
		sourceGroup = -1;
		targetGroup = -1;
		nameGroup = -1;
		noteGroup = -1;
		codeFinder = new InlineCodeFinder();
		preserveWS = true;
		sample = "";
	}
	
	public Rule (Rule obj) {
		ruleName = obj.ruleName;
		expr = obj.expr;
		sourceGroup = obj.sourceGroup;
		targetGroup = obj.targetGroup;
		nameGroup = obj.nameGroup;
		noteGroup = obj.noteGroup;
		ruleType = obj.ruleType;
		preserveWS = obj.preserveWS;
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
	
	public String getExpression () {
		return expr;
	}
	
	public void setExpression (String value) {
		if ( value == null ) throw new NullPointerException();
		expr = value;
	}

	public String getSample () {
		return sample;
	}
	
	public void setSample (String value) {
		if ( value == null ) sample = "";
		else sample = value;
	}

	public int getRuleType () {
		return ruleType;
	}
	
	public void setRuleType (int value) {
		ruleType = value;
	}
	
	public int getSourceGroup () {
		return sourceGroup;
	}
	
	public void setSourceGroup (int index) {
		sourceGroup = index;
	}
	
	public int getTargetGroup () {
		return targetGroup;
	}
	
	public void setTargetGroup (int index) {
		targetGroup = index;
	}
	
	public int getNameGroup () {
		return nameGroup;
	}
	
	public void setNameGroup (int index) {
		nameGroup = index;
	}
	
	public int getNoteGroup () {
		return noteGroup;
	}
	
	public void setNoteGroup (int index) {
		noteGroup = index;
	}
	
	public boolean preserveWS () {
		return preserveWS;
	}
	
	public void setPreserveWS (boolean value) {
		preserveWS = value;
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
		tmp.setString("expr", expr);
		tmp.setInteger("groupSource", sourceGroup);
		tmp.setInteger("groupTarget", targetGroup);
		tmp.setInteger("groupName", nameGroup);
		tmp.setInteger("groupNote", noteGroup);
		tmp.setBoolean("preserveWS", preserveWS);
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
		expr = tmp.getString("expr", expr);
		sourceGroup = tmp.getInteger("groupSource", sourceGroup);
		targetGroup = tmp.getInteger("groupTarget", targetGroup);
		nameGroup = tmp.getInteger("groupName", nameGroup);
		noteGroup = tmp.getInteger("groupNote", noteGroup);
		preserveWS = tmp.getBoolean("preserveWS", preserveWS);
		propertyName = tmp.getString("propertyName", propertyName);
		propertyValue = tmp.getString("propertyValue", propertyValue);
		sample = tmp.getString("sample", sample);
		useCodeFinder = tmp.getBoolean("useCodeFinder", useCodeFinder);
		codeFinder.fromString(tmp.getGroup("codeFinderRules", ""));
	}
	
}
