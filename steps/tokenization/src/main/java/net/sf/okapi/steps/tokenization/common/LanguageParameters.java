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
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.lib.extra.AbstractParameters;

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
	private List<LocaleId> languageWhiteList;
	private List<LocaleId> languageBlackList;
		
	@Override
	protected void parameters_init() {
		languageWhiteList = new ArrayList<LocaleId>();
		languageBlackList = new ArrayList<LocaleId>();
	}
	
	@Override
	protected void parameters_load(ParametersString buffer) {
		languageMode = buffer.getInteger("languageMode", LANGUAGES_ALL);
		setLanguageWhiteList(ListUtil.stringAsLanguageList(buffer.getString("languageWhiteList")));
		setLanguageBlackList(ListUtil.stringAsLanguageList(buffer.getString("languageBlackList")));
		if (( languageMode == LANGUAGES_ONLY_WHITE_LIST ) && Util.isEmpty(languageWhiteList) ) {
			languageMode = LANGUAGES_ALL;
		}
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
		buffer.setString("languageWhiteList", ListUtil.languageListAsString(languageWhiteList));
		buffer.setString("languageBlackList", ListUtil.languageListAsString(languageBlackList));
	}

	public int getLanguageMode() {
		return languageMode;
	}

	public void setLanguageMode(int languageMode) {
		this.languageMode = languageMode;
	}

	public List<LocaleId> getLanguageWhiteList() {
		return languageWhiteList;
	}

	public void setLanguageWhiteList(List<LocaleId> languageWhiteList) {
		this.languageWhiteList = languageWhiteList;
	}

	public List<LocaleId> getLanguageBlackList() {
		return languageBlackList;
	}

	public void setLanguageBlackList(List<LocaleId> languageBlackList) {
		this.languageBlackList = languageBlackList;
	}

	public boolean supportsLanguage(LocaleId language) {
		if (languageMode == LANGUAGES_ALL_EXCEPT_BLACK_LIST && languageBlackList.contains(language)) return false;
		if (languageMode == LANGUAGES_ONLY_WHITE_LIST && !languageWhiteList.contains(language)) return false;
		return true;
	}		
}
