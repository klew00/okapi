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
import net.sf.okapi.lib.extra.AbstractParameters;
import net.sf.okapi.steps.tokenization.locale.LocaleUtil;

public class LanguageAndTokenParameters extends AbstractParameters {

	/**
	 * @see languageMode;
	 */
	final public static int LANGUAGES_ALL = 0;
	final public static int LANGUAGES_ONLY_WHITE_LIST = 1;
	final public static int LANGUAGES_ALL_EXCEPT_BLACK_LIST = 2;
	
	/**
	 * @see tokenMode;
	 */
	final public static int TOKENS_ALL = 0;
	final public static int TOKENS_SELECTED = 1;
	
	/**
	 * The tokenization step will tokenize text only in the languages specified by languageMode:
	 * <li>LANGUAGES_ALL = 0 - text in any language will be tokenized.  
	 * <li>LANGUAGES_ONLY_WHITE_LIST = 1 - if text is in a language listed on languagesWhiteList, it will be tokenized. 
	 * <li>LANGUAGES_ALL_EXCEPT_BLACK_LIST = 2 - if text is in a language not listed on languagesBlackList, it will be tokenized.<p>
	 * Default: LANGUAGES_ALL
	 */
	private int languageMode = LANGUAGES_ALL; 
	private List<String> languageWhiteList;
	private List<String> languageBlackList;
		
	/**
	 * The tokenization step will extract only the tokens specified by tokenMode:
	 * <li>TOKENS_ALL = 0 - all registered token types will be tried to be extracted.  
	 * <li>TOKENS_ONLY_LISTED = 1 - only the token types listed on tokenTypes will be tried to be extracted.<p> 
	 * Default: TOKENS_ALL
	 */
	private int tokenMode = TOKENS_ALL;
	private List<String> tokenNames;

	@Override
	protected void parameters_init() {
		
		languageWhiteList = new ArrayList<String>();
		languageBlackList = new ArrayList<String>();
		tokenNames = new ArrayList<String>();
	}
	
	@Override
	protected void parameters_load(ParametersString buffer) {
		
		languageMode = buffer.getInteger("languageMode", LANGUAGES_ALL);
		setLanguageWhiteList(ListUtil.stringAsList(buffer.getString("languageWhiteList")));
		setLanguageWhiteList(ListUtil.stringAsList(buffer.getString("languageBlackList")));
		
		tokenMode = buffer.getInteger("tokenMode", TOKENS_ALL);
		ListUtil.stringAsList(tokenNames, buffer.getString("tokenNames"));		
	}

	@Override
	protected void parameters_reset() {
		
		languageMode = LANGUAGES_ALL;
		languageWhiteList.clear();
		languageBlackList.clear();
		
		tokenMode = TOKENS_ALL;
		tokenNames.clear();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setInteger("languageMode", languageMode);
		buffer.setString("languageWhiteList", ListUtil.listAsString(languageWhiteList));
		buffer.setString("languageBlackList", ListUtil.listAsString(languageBlackList));
		
		buffer.setInteger("tokenMode", tokenMode);
		buffer.setString("tokenNames", ListUtil.listAsString(tokenNames));
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

	public int getTokenMode() {
		
		return tokenMode;
	}

	public void setTokenMode(int tokenMode) {
		
		this.tokenMode = tokenMode;
	}

	public List<String> getTokenNames() {
		
		return tokenNames;
	}

	public void setTokenNames(List<String> tokenNames) {
		
		this.tokenNames = tokenNames;
	}

	public boolean supportsLanguage(String language) {
		
		if (languageMode == LANGUAGES_ALL_EXCEPT_BLACK_LIST && languageBlackList.contains(language)) return false;
		
		if (languageMode == LANGUAGES_ONLY_WHITE_LIST && !languageWhiteList.contains(language)) return false;
		
		return true;
	}	
	
	public boolean supportsTokenName(String tokenName) {
		
		if (tokenMode == TOKENS_SELECTED && !tokenNames.contains(tokenName)) return false;
		
		return true;
	}
}
