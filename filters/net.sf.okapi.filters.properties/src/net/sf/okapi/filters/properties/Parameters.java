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
import net.sf.okapi.common.FieldsString;
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
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("useLD", locDir.useLD());
		tmp.add("localizeOutside", locDir.localizeOutside());
		tmp.add("useCodeFinder", useCodeFinder);
		tmp.add("useKeyCondition", useKeyCondition);
		tmp.add("extractOnlyMatchingKey", extractOnlyMatchingKey);
		tmp.add("keyCondition", keyCondition);
		tmp.add("escapeExtendedChars", escapeExtendedChars);
		return tmp.toString();
	}
	
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		reset();
		// Parse the fields
		boolean tmpBool1 = tmp.get("useLD", locDir.useLD());
		boolean tmpBool2 = tmp.get("localizeOutside", locDir.localizeOutside());
		locDir.setOptions(tmpBool1, tmpBool2);
		useCodeFinder = tmp.get("useCodeFinder", useCodeFinder);
		useKeyCondition = tmp.get("useKeyCondition", useKeyCondition);
		extractOnlyMatchingKey = tmp.get("extractOnlyMatchingKey", extractOnlyMatchingKey);
		keyCondition = tmp.get("keyCondition", keyCondition);
		escapeExtendedChars = tmp.get("escapeExtendedChars", escapeExtendedChars);
	}
}
