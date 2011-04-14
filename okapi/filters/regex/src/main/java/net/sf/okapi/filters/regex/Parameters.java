/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

import java.util.ArrayList;
import java.util.regex.Pattern;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.LocalizationDirectives;

public class Parameters extends BaseParameters {

	private static final String EXTRACTOUTERSTRINGS = "extractOuterStrings";
	private static final String STARTSTRING = "startString";
	private static final String ENDSTRING = "endString";
	private static final String USEBSLASHESCAPE = "useBSlashEscape";
	private static final String USEDOUBLECHARESCAPE = "useDoubleCharEscape";
	private static final String ONELEVELGROUP = "oneLevelGroups";
	private static final String USELD = "useLd";
	private static final String LOCALIZEOUTSIDE = "localizeOutside";
	private static final String REGEXOPTIONS = "regexOptions";
	private static final String MIMETYPE = "mimeType";
	
	private boolean extractOuterStrings;
	private String startString;
	private String endString;
	private boolean useBSlashEscape;
	private boolean useDoubleCharEscape;
	private int regexOptions;
	private LocalizationDirectives localizationDirectives;
	private String mimeType;
	private boolean oneLevelGroups;
	private ArrayList<Rule> rules;

	public Parameters () {
		localizationDirectives = new LocalizationDirectives();
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
		useDoubleCharEscape = false;
		localizationDirectives.reset();
		mimeType = "text/plain";
		oneLevelGroups = false;
	}

	public boolean getExtractOuterStrings () {
		return extractOuterStrings;
	}

	public void setExtractOuterStrings (boolean extractOuterStrings) {
		this.extractOuterStrings = extractOuterStrings;
	}

	public String getStartString () {
		return startString;
	}

	public void setStartString (String startString) {
		this.startString = startString;
	}

	public String getEndString () {
		return endString;
	}

	public void setEndString (String endString) {
		this.endString = endString;
	}

	public boolean getUseBSlashEscape () {
		return useBSlashEscape;
	}

	public void setUseBSlashEscape (boolean useBSlashEscape) {
		this.useBSlashEscape = useBSlashEscape;
	}
	
	public boolean getUseDoubleCharEscape () {
		return useDoubleCharEscape;
	}
	
	public void setUseDoubleCharEscape (boolean useDoubleCharEscape) {
		this.useDoubleCharEscape = useDoubleCharEscape;
	}
	
	public int getRegexOptions () {
		return regexOptions;
	}

	public void setRegexOptions (int regexOptions) {
		this.regexOptions = regexOptions;
	}
	
	public LocalizationDirectives getLocalizationDirectives () {
		return localizationDirectives;
	}
	
	public void setLocalizationDirectives (
		LocalizationDirectives localizationDirectives) {
		this.localizationDirectives = localizationDirectives;
	}
	
	public String getMimeType () {
		return mimeType;
	}
	
	public void setMimeType (String mimeType) {
		this.mimeType = mimeType;
	}
	
	public boolean getOneLevelGroups () {
		return oneLevelGroups;
	}
	
	public void setOneLevelGroups (boolean oneLevelGroups) {
		this.oneLevelGroups = oneLevelGroups;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		boolean tmpBool1 = buffer.getBoolean(USELD, localizationDirectives.useLD());
		boolean tmpBool2 = buffer.getBoolean(LOCALIZEOUTSIDE, localizationDirectives.localizeOutside());
		localizationDirectives.setOptions(tmpBool1, tmpBool2);
		startString = buffer.getString(STARTSTRING, startString);
		endString = buffer.getString(ENDSTRING, endString);
		extractOuterStrings = buffer.getBoolean(EXTRACTOUTERSTRINGS, extractOuterStrings);
		useBSlashEscape = buffer.getBoolean(USEBSLASHESCAPE, useBSlashEscape);
		useDoubleCharEscape = buffer.getBoolean(USEDOUBLECHARESCAPE, useDoubleCharEscape);
		oneLevelGroups = buffer.getBoolean(ONELEVELGROUP, oneLevelGroups);
		regexOptions = buffer.getInteger(REGEXOPTIONS, regexOptions);
		mimeType = buffer.getString(MIMETYPE, mimeType);
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
		buffer.setBoolean(USELD, localizationDirectives.useLD());
		buffer.setBoolean(LOCALIZEOUTSIDE, localizationDirectives.localizeOutside());
		buffer.setString(STARTSTRING, startString);
		buffer.setString(ENDSTRING, endString);
		buffer.setBoolean(EXTRACTOUTERSTRINGS, extractOuterStrings);
		buffer.setBoolean(USEBSLASHESCAPE, useBSlashEscape);
		buffer.setBoolean(USEDOUBLECHARESCAPE, useDoubleCharEscape);
		buffer.setBoolean(ONELEVELGROUP, oneLevelGroups);
		buffer.setInteger(REGEXOPTIONS, regexOptions);
		buffer.setString(MIMETYPE, mimeType);
		buffer.setInteger("ruleCount", rules.size());
		for ( int i=0; i<rules.size(); i++ ) {
			buffer.setGroup(String.format("rule%d", i), rules.get(i).toString());
		}
		return buffer.toString();
	}
	
	public void compileRules () {
		for ( Rule rule : rules ) {
			// Compile the full pattern
			rule.pattern = Pattern.compile(rule.expr, regexOptions);
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
