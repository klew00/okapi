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

	public boolean                useCodeFinder;
	public InlineCodeFinder       codeFinder;
	public boolean                escapeExtendedChars;
	public boolean                useKeyCondition;
	public boolean                extractOnlyMatchingKey;
	public String                 keyCondition;
	public boolean                extraComments;
	public LocalizationDirectives locDir;


	public Parameters () {
		locDir = new LocalizationDirectives();
		codeFinder = new InlineCodeFinder();
		reset();
	}
	
	public void reset () {
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

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("useLD", locDir.useLD());
		buffer.setBoolean("localizeOutside", locDir.localizeOutside());
		buffer.setBoolean("useCodeFinder", useCodeFinder);
		buffer.setBoolean("useKeyCondition", useKeyCondition);
		buffer.setBoolean("extractOnlyMatchingKey", extractOnlyMatchingKey);
		buffer.setString("keyCondition", keyCondition);
		buffer.setBoolean("escapeExtendedChars", escapeExtendedChars);
		return buffer.toString();
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		boolean tmpBool1 = buffer.getBoolean("useLD", locDir.useLD());
		boolean tmpBool2 = buffer.getBoolean("localizeOutside", locDir.localizeOutside());
		locDir.setOptions(tmpBool1, tmpBool2);
		useCodeFinder = buffer.getBoolean("useCodeFinder", useCodeFinder);
		useKeyCondition = buffer.getBoolean("useKeyCondition", useKeyCondition);
		extractOnlyMatchingKey = buffer.getBoolean("extractOnlyMatchingKey", extractOnlyMatchingKey);
		keyCondition = buffer.getString("keyCondition", keyCondition);
		escapeExtendedChars = buffer.getBoolean("escapeExtendedChars", escapeExtendedChars);
	}
}
