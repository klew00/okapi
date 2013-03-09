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
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class LanguageAndTokenParameters extends LanguageParameters {
	
	private List<String> tokenNames;
	private List<Integer> tokenIds;

	@Override
	protected void parameters_init() {

		super.parameters_init();
		
		tokenNames = new ArrayList<String>();
		tokenIds = new ArrayList<Integer>();
	}
	
	@Override
	protected void parameters_load(ParametersString buffer) {
		
		super.parameters_load(buffer);
		
//		ListUtil.stringAsList(tokenNames, buffer.getString("tokens"));
//		tokenIds = Tokens.getTokenIDs(tokenNames);
		setTokenNames(buffer.getString("tokens"));
	}

	@Override
	protected void parameters_reset() {
		
		super.parameters_reset();
		
		tokenNames.clear();
		tokenIds.clear();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		super.parameters_save(buffer);
		
		buffer.setString("tokens", ListUtil.listAsString(tokenNames));
	}

	public boolean supportsToken(String tokenName) {
		
		return (tokenNames != null) && (tokenNames.contains(tokenName) || (tokenNames.size() == 0));
	}
	
	public boolean supportsToken(int tokenId) {
		
		return (tokenIds != null) && (tokenIds.contains(tokenId) || (tokenIds.size() == 0));
	}

//	public void setTokenNames(List<String> tokenNames) {
//		
//		this.tokenNames = tokenNames;
//		tokenIds = Tokens.getTokenIDs(this.tokenNames);
//	}
	
	public void setTokenNames(String... tokenNames) {
		
		//setTokenNames(ListUtil.arrayAsList(tokenNames));
		if (tokenNames != null)
			this.tokenNames = ListUtil.arrayAsList(tokenNames);
		
		tokenIds = Tokens.getTokenIDs(this.tokenNames);
	}

	public List<String> getTokenNames() {
		
		return tokenNames;
	}
	
}
