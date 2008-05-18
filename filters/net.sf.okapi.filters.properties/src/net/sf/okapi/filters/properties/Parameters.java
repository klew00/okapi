/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.properties;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

//	public ILocalizationDirectives   m_LD;
//	public InlineCodeFinder          m_CodeFinder;
	public boolean                   useCodeFinder;
	public boolean                   escapeExtendedChars;
	public boolean                   useKeyCondition;
	public boolean                   extractOnlyMatchingKey;
	public String                    keyCondition;
	public boolean                   extraComments;

	public Parameters () {
//		m_LD = (ILocalizationDirectives)new LocalizationDirectives();
//		m_CodeFinder = new InlineCodeFinder();
		reset();
	}
	
	public void reset () {
//		m_LD.reset();
//		m_CodeFinder.reset();
		useCodeFinder = true;
		escapeExtendedChars = true;
		
		useKeyCondition = false;
		extractOnlyMatchingKey = true;
		keyCondition = ".*text.*";

		extraComments = true;

		// Default inline codes: special escaped-chars and printf-style variable
/*		m_CodeFinder.addRule("%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]");
		m_CodeFinder.addRule("(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		//TODO: Add Java-style variables. this is too basic
		m_CodeFinder.addRule("\\{\\d.*?\\}");
*/ //		m_CodeFinder.createExpression();
	}

	public String toString ()
	{
		// Store the parameters in fields
		FieldsString Tmp = new FieldsString();
//		Tmp.add("useld", m_LD.useDirectives());
//		Tmp.add("localizeoutside", m_LD.localizeOutside());
		Tmp.add("userules", useCodeFinder);
		Tmp.add("usekeycondition", useKeyCondition);
		Tmp.add("extractonlykeycond", extractOnlyMatchingKey);
		Tmp.add("keycondition", keyCondition);
		Tmp.add("escapechars", escapeExtendedChars);
//		Tmp.addGroup("rules", m_CodeFinder.getOptions());
		return Tmp.toString();
	}
	
	public void fromString (String p_sData)
	{
		// Read the file content as a set of fields
		FieldsString Tmp = new FieldsString(p_sData);

		// Parse the fields
//		boolean bTmp1 = Tmp.get("useld", m_LD.useDirectives());
//		boolean bTmp2 = Tmp.get("localizeoutside", m_LD.localizeOutside());
//		m_LD.setOptions(bTmp1, bTmp2, true);
		useCodeFinder = Tmp.get("userules", useCodeFinder);
		useKeyCondition = Tmp.get("usekeycondition", useKeyCondition);
		extractOnlyMatchingKey = Tmp.get("extractonlykeycond", extractOnlyMatchingKey);
		keyCondition = Tmp.get("keycondition", keyCondition);
		escapeExtendedChars = Tmp.get("escapechars", escapeExtendedChars);
//		m_CodeFinder.setOptions(Tmp.getGroup("rules", m_CodeFinder.getOptions()));
	}
}
