/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Format.JSON;

import net.sf.okapi.Filter.ILocalizationDirectives;
import net.sf.okapi.Filter.InlineCodeFinder;
import net.sf.okapi.Filter.LocalizationDirectives;
import net.sf.okapi.Library.Base.BaseParameters;
import net.sf.okapi.Library.Base.FieldsString;

class Parameters extends BaseParameters {

	public ILocalizationDirectives   m_LD;
	public InlineCodeFinder          m_CodeFinder;
	public boolean                   m_bUseCodeFinder;
	public boolean                   m_bExtractStandalone;
	public boolean                   m_bEscapeExtendedChars;
	public boolean                   m_bExtractAllPairs;
	public String                    m_sExceptions;

	public Parameters () {
		m_LD = (ILocalizationDirectives)new LocalizationDirectives();
		m_CodeFinder = new InlineCodeFinder();
		reset();
	}
	
	public void reset ()
	{
		m_LD.reset();
		m_LD.setOptions(false, true, true); // Override: no LD use by default
		m_CodeFinder.reset();
		m_bUseCodeFinder = true;

		// Default in-line codes: special escaped-chars and printf-style variable
		m_CodeFinder.addRule("%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]");
		m_CodeFinder.addRule("(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		m_CodeFinder.createExpression();

		m_bExtractStandalone = true;
		m_bExtractAllPairs = true;
		m_sExceptions = "";

		m_bEscapeExtendedChars = true;
	}

	public String toString ()
	{
		// Store the parameters in fields
		FieldsString Tmp = new FieldsString();
		Tmp.add("useld", m_LD.useDirectives());
		Tmp.add("localizeoutside", m_LD.localizeOutside());
		Tmp.add("userules", m_bUseCodeFinder);
		Tmp.add("extstandalone", m_bExtractStandalone);
		Tmp.add("extallpairs", m_bExtractAllPairs);
		Tmp.add("exceptions", m_sExceptions);
		Tmp.add("escapechars", m_bEscapeExtendedChars);
		Tmp.addGroup("rules", m_CodeFinder.getOptions());
		return Tmp.toString();
	}
	
	public void fromString (String p_sData)
	{
		// Read the file content as a set of fields
		FieldsString Tmp = new FieldsString(p_sData);

		// Parse the fields
		boolean bTmp1 = Tmp.get("useld", m_LD.useDirectives());
		boolean bTmp2 = Tmp.get("localizeoutside", m_LD.localizeOutside());
		m_LD.setOptions(bTmp1, bTmp2, true);
		m_bUseCodeFinder = Tmp.get("userules", m_bUseCodeFinder);
		m_bExtractStandalone = Tmp.get("extstandalone", m_bExtractStandalone);
		m_bExtractAllPairs = Tmp.get("extallpairs", m_bExtractAllPairs);
		m_sExceptions = Tmp.get("exceptions", m_sExceptions);
		m_bEscapeExtendedChars = Tmp.get("escapechars", m_bEscapeExtendedChars);
		m_CodeFinder.setOptions(Tmp.getGroup("rules", m_CodeFinder.getOptions()));
	}
}
