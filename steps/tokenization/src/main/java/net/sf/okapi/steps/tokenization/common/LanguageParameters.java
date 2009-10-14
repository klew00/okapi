/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization.common;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.Util;
import net.sf.okapi.lib.extra.AbstractParameters;
import net.sf.okapi.steps.tokenization.locale.LocaleUtil;

public class LanguageParameters extends AbstractParameters {

	/**
	 * @see languageMode;
	 */
	final public static int LANGUAGES_ALL = 0;
	final public static int LANGUAGES_ONLY_WHITE_LIST = 1;
	final public static int LANGUAGES_ALL_EXCEPT_BLACK_LIST = 2;
	
	/**
	 * The set of languages is specified by languageMode:
	 * <li>LANGUAGES_ALL = 0 - all languages  
	 * <li>LANGUAGES_ONLY_WHITE_LIST = 1 - only the languages listed on languagesWhiteList 
	 * <li>LANGUAGES_ALL_EXCEPT_BLACK_LIST = 2 - all languages except those not listed on languagesBlackList<p>
	 * Default: LANGUAGES_ALL
	 */
	private int languageMode = LANGUAGES_ALL; 
	private List<String> languageWhiteList;
	private List<String> languageBlackList;
		
	@Override
	protected void parameters_init() {
		
		languageWhiteList = new ArrayList<String>();
		languageBlackList = new ArrayList<String>();
	}
	
	@Override
	protected void parameters_load(ParametersString buffer) {
		
		languageMode = buffer.getInteger("languageMode", LANGUAGES_ALL);
		setLanguageWhiteList(ListUtil.stringAsList(buffer.getString("languageWhiteList")));
		setLanguageBlackList(ListUtil.stringAsList(buffer.getString("languageBlackList")));
		
		if (languageMode == LANGUAGES_ONLY_WHITE_LIST && Util.isEmpty(languageWhiteList))
			languageMode = LANGUAGES_ALL;
	}

	@Override
	protected void parameters_reset() {
		
		languageMode = LANGUAGES_ALL;
		languageWhiteList.clear();
		languageBlackList.clear();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setInteger("languageMode", languageMode);
		buffer.setString("languageWhiteList", ListUtil.listAsString(languageWhiteList));
		buffer.setString("languageBlackList", ListUtil.listAsString(languageBlackList));
	}

	public int getLanguageMode() {
		
		return languageMode;
	}

	public void setLanguageMode(int languageMode) {
		
		this.languageMode = languageMode;
	}

	public List<String> getLanguageWhiteList() {
		
		return languageWhiteList;
	}

	public void setLanguageWhiteList(List<String> languageWhiteList) {
		
		this.languageWhiteList = LocaleUtil.normalizeLanguageCodes_Okapi(languageWhiteList);
	}

	public List<String> getLanguageBlackList() {
		
		return languageBlackList;
	}

	public void setLanguageBlackList(List<String> languageBlackList) {
		
		this.languageBlackList = LocaleUtil.normalizeLanguageCodes_Okapi(languageBlackList);
	}

	public boolean supportsLanguage(String language) {
		
		if (languageMode == LANGUAGES_ALL_EXCEPT_BLACK_LIST && languageBlackList.contains(language)) return false;
		
		if (languageMode == LANGUAGES_ONLY_WHITE_LIST && !languageWhiteList.contains(language)) return false;
		
		return true;
	}		
}
