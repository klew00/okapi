/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.filters.plaintext.common.AbstractParameters;

/**
 * Tokenization step parameters
 * 
 * @version 0.1 06.07.2009
 */

public class Parameters extends AbstractParameters {

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
	final public static int TOKENS_ONLY_LISTED = 1;
	
	public boolean tokenizeSource;
	public boolean tokenizeTargets;
	
	/**
	 * The tokenization step will tokenize text only in the languages specified by languageMode:
	 * <li>LANGUAGES_ALL = 0 - text in any language will be tokenized.  
	 * <li>LANGUAGES_ONLY_WHITE_LIST = 1 - if text is in a language listed on languagesWhiteList, it will be tokenized. 
	 * <li>LANGUAGES_ALL_EXCEPT_BLACK_LIST = 2 - if text is in a language not listed on languagesBlackList, it will be tokenized.<p>
	 * Default: LANGUAGES_ALL
	 */
	public int languageMode = LANGUAGES_ALL; 
	public String languageWhiteList;
	public String languageBlackList;
		
	/**
	 * The tokenization step will extract only the tokens specified by tokenMode:
	 * <li>TOKENS_ALL = 0 - all registered token types will be tried to be extracted.  
	 * <li>TOKENS_ONLY_LISTED = 1 - only the token types listed on tokenTypes will be tried to be extracted.<p> 
	 * Default: TOKENS_ALL
	 */
	public int tokenMode = TOKENS_ALL;
	public String tokenTypes;
	
	// Also inherits a list of steps from CompoundStepParameters
	
	@Override
	protected void parameters_reset() {

		tokenizeSource = true;
		tokenizeTargets = false;
		
		languageMode = LANGUAGES_ALL;
		languageWhiteList = "";
		languageBlackList = "";
		
		tokenMode = TOKENS_ALL;
		tokenTypes = "";
	}

	@Override
	protected void parameters_load(ParametersString buffer) {

		tokenizeSource = buffer.getBoolean("tokenizeSource", true);
		tokenizeTargets = buffer.getBoolean("tokenizeTargets", false);
		
		languageMode = buffer.getInteger("languageMode", LANGUAGES_ALL);
		languageWhiteList = buffer.getString("languageWhiteList");
		languageBlackList = buffer.getString("languageBlackList");
		
		tokenMode = buffer.getInteger("tokenMode", TOKENS_ALL);
		tokenTypes = buffer.getString("tokenTypes");
	}
	
	@Override
	protected void parameters_save(ParametersString buffer) {

		buffer.setBoolean("tokenizeSource", tokenizeSource);
		buffer.setBoolean("tokenizeTargets", tokenizeTargets);
		
		buffer.setInteger("languageMode", languageMode);
		buffer.setString("languageWhiteList", languageWhiteList);
		buffer.setString("languageBlackList", languageBlackList);
		
		buffer.setInteger("tokenMode", tokenMode);
		buffer.setString("tokenTypes", tokenTypes);
	}	
}
