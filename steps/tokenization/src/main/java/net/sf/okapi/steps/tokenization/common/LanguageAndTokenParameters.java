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

public class LanguageAndTokenParameters extends LanguageParameters {

	/**
	 * @see tokenMode;
	 */
	final public static int TOKENS_ALL = 0;
	final public static int TOKENS_SELECTED = 1;
	
	/**
	 * The set of tokens is specified by tokenMode:
	 * <li>TOKENS_ALL = 0 - all registered token names  
	 * <li>TOKENS_ONLY_LISTED = 1 - only the token names listed on tokenNames<p> 
	 * Default: TOKENS_ALL
	 */
	private int tokenMode = TOKENS_ALL;
	private List<String> tokenNames;

	@Override
	protected void parameters_init() {

		super.parameters_init();
		
		tokenNames = new ArrayList<String>();
	}
	
	@Override
	protected void parameters_load(ParametersString buffer) {
		
		super.parameters_load(buffer);
		
		tokenMode = buffer.getInteger("tokenMode", TOKENS_ALL);
		ListUtil.stringAsList(tokenNames, buffer.getString("tokenNames"));		
	}

	@Override
	protected void parameters_reset() {
		
		super.parameters_reset();
		
		tokenMode = TOKENS_ALL;
		tokenNames.clear();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		super.parameters_save(buffer);
		
		buffer.setInteger("tokenMode", tokenMode);
		buffer.setString("tokenNames", ListUtil.listAsString(tokenNames));
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

	public boolean supportsTokenName(String tokenName) {
		
		if (tokenMode == TOKENS_SELECTED && !tokenNames.contains(tokenName)) return false;
		
		return true;
	}
}
