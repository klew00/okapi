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

package net.sf.okapi.filters.properties;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.common.filters.LocalizationDirectives;

public class Parameters extends BaseParameters {

	protected boolean                  useCodeFinder;
	protected InlineCodeFinder         codeFinder;
	protected boolean                  escapeExtendedChars;
	protected boolean                  useKeyCondition;
	protected boolean                  extractOnlyMatchingKey;
	protected String                   keyCondition;
	protected boolean                  extraComments;
	protected LocalizationDirectives   locDir;


	public Parameters () {
		locDir = new LocalizationDirectives();
		codeFinder = new InlineCodeFinder();
		reset();
	}
	
	public void reset () {
		super.reset();
		locDir.reset();
		escapeExtendedChars = true;

		useCodeFinder = true;
		// Default in-line codes: special escaped-chars and printf-style variable
		codeFinder.addRule("%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]");
		codeFinder.addRule("(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		//TODO: Add Java-style variables. this is too basic
		codeFinder.addRule("\\{\\d.*?\\}");
		
		useKeyCondition = false;
		extractOnlyMatchingKey = true;
		keyCondition = ".*text.*";

		extraComments = true;
	}

	public String toString () {
		setBoolean("useLD", locDir.useLD());
		setBoolean("localizeOutside", locDir.localizeOutside());
		setBoolean("useCodeFinder", useCodeFinder);
		setBoolean("useKeyCondition", useKeyCondition);
		setBoolean("extractOnlyMatchingKey", extractOnlyMatchingKey);
		setString("keyCondition", keyCondition);
		setBoolean("escapeExtendedChars", escapeExtendedChars);
		return super.toString();
	}
	
	public void fromString (String data) {
		reset();
		super.fromString(data);
		boolean tmpBool1 = getBoolean("useLD", locDir.useLD());
		boolean tmpBool2 = getBoolean("localizeOutside", locDir.localizeOutside());
		locDir.setOptions(tmpBool1, tmpBool2);
		useCodeFinder = getBoolean("useCodeFinder", useCodeFinder);
		useKeyCondition = getBoolean("useKeyCondition", useKeyCondition);
		extractOnlyMatchingKey = getBoolean("extractOnlyMatchingKey", extractOnlyMatchingKey);
		keyCondition = getString("keyCondition", keyCondition);
		escapeExtendedChars = getBoolean("escapeExtendedChars", escapeExtendedChars);
	}
}
