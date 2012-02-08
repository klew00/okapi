/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.properties;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.common.filters.LocalizationDirectives;

public class Parameters extends BaseParameters {

	public boolean useCodeFinder;
	public InlineCodeFinder codeFinder;
	public boolean escapeExtendedChars;
	public boolean useKeyCondition;
	public boolean extractOnlyMatchingKey;
	public String keyCondition;
	public boolean extraComments;
	public boolean commentsAreNotes;
	public LocalizationDirectives locDir;
	public boolean convertLFandTab;
	public String subfilter;
	

	public Parameters () {
		locDir = new LocalizationDirectives();
		codeFinder = new InlineCodeFinder();
		reset();
		toString(); // fill the list
	}
	
	public void reset () {
		locDir.reset();
		escapeExtendedChars = true;
		
		convertLFandTab = true;

		useCodeFinder = true;
		codeFinder.reset();
		codeFinder.setSample("%s, %d, {1}, \\n, \\r, \\t, etc.");
		codeFinder.setUseAllRulesWhenTesting(true);
		// Default in-line codes: special escaped-chars and printf-style variable
		codeFinder.addRule("%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]");
		codeFinder.addRule("(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		//TODO: Add Java-style variables. this is too basic
		codeFinder.addRule("\\{\\d[^\\\\]*?\\}");		
		// Basic HTML/XML
		codeFinder.addRule("\\<(/?)\\w+[^>]*?>");
		
		useKeyCondition = false;
		extractOnlyMatchingKey = true;
		keyCondition = ".*text.*";

		extraComments = false;
		commentsAreNotes = true;
		subfilter = null;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("convertLFandTab", convertLFandTab);
		buffer.setBoolean("useLD", locDir.useLD());
		buffer.setBoolean("localizeOutside", locDir.localizeOutside());
		buffer.setBoolean("useKeyCondition", useKeyCondition);
		buffer.setBoolean("extractOnlyMatchingKey", extractOnlyMatchingKey);
		buffer.setString("keyCondition", keyCondition);
		buffer.setBoolean("extraComments", extraComments);
		buffer.setBoolean("commentsAreNotes", commentsAreNotes);
		buffer.setBoolean("escapeExtendedChars", escapeExtendedChars);
		buffer.setBoolean("useCodeFinder", useCodeFinder);
		buffer.setGroup("codeFinderRules", codeFinder.toString());
		buffer.setString("subfilter", subfilter);
		return buffer.toString();
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		convertLFandTab = buffer.getBoolean("convertLFandTab", convertLFandTab);
		boolean tmpBool1 = buffer.getBoolean("useLD", locDir.useLD());
		boolean tmpBool2 = buffer.getBoolean("localizeOutside", locDir.localizeOutside());
		locDir.setOptions(tmpBool1, tmpBool2);
		useKeyCondition = buffer.getBoolean("useKeyCondition", useKeyCondition);
		extractOnlyMatchingKey = buffer.getBoolean("extractOnlyMatchingKey", extractOnlyMatchingKey);
		keyCondition = buffer.getString("keyCondition", keyCondition);
		extraComments = buffer.getBoolean("extraComments",extraComments);
		commentsAreNotes = buffer.getBoolean("commentsAreNotes", commentsAreNotes);
		escapeExtendedChars = buffer.getBoolean("escapeExtendedChars", escapeExtendedChars);
		useCodeFinder = buffer.getBoolean("useCodeFinder", useCodeFinder);
		codeFinder.fromString(buffer.getGroup("codeFinderRules", ""));
		subfilter = buffer.getString("subfilter", subfilter);
	}

	public boolean isUseCodeFinder() {
		return useCodeFinder;
	}

	public void setUseCodeFinder(boolean useCodeFinder) {
		this.useCodeFinder = useCodeFinder;
	}

	public InlineCodeFinder getCodeFinder() {
		return codeFinder;
	}

	public void setCodeFinder(InlineCodeFinder codeFinder) {
		this.codeFinder = codeFinder;
	}

	public boolean isEscapeExtendedChars() {
		return escapeExtendedChars;
	}

	public void setEscapeExtendedChars(boolean escapeExtendedChars) {
		this.escapeExtendedChars = escapeExtendedChars;
	}

	public boolean isUseKeyCondition() {
		return useKeyCondition;
	}

	public void setUseKeyCondition(boolean useKeyCondition) {
		this.useKeyCondition = useKeyCondition;
	}

	public boolean isExtractOnlyMatchingKey() {
		return extractOnlyMatchingKey;
	}

	public void setExtractOnlyMatchingKey(boolean extractOnlyMatchingKey) {
		this.extractOnlyMatchingKey = extractOnlyMatchingKey;
	}

	public String getKeyCondition() {
		return keyCondition;
	}

	public void setKeyCondition(String keyCondition) {
		this.keyCondition = keyCondition;
	}

	public boolean isExtraComments() {
		return extraComments;
	}

	public void setExtraComments(boolean extraComments) {
		this.extraComments = extraComments;
	}

	public boolean isCommentsAreNotes() {
		return commentsAreNotes;
	}

	public void setCommentsAreNotes(boolean commentsAreNotes) {
		this.commentsAreNotes = commentsAreNotes;
	}

	public LocalizationDirectives getLocDir() {
		return locDir;
	}

	public void setLocDir(LocalizationDirectives locDir) {
		this.locDir = locDir;
	}

	public boolean isConvertLFandTab() {
		return convertLFandTab;
	}

	public void setConvertLFandTab(boolean convertLFandTab) {
		this.convertLFandTab = convertLFandTab;
	}

	public String getSubfilter() {
		return subfilter;
	}

	public void setSubfilter(String subfilter) {
		this.subfilter = subfilter;
	}
}
