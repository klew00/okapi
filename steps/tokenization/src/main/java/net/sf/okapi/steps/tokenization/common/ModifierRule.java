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

public class ModifierRule extends LexerRule {

	private List<Integer> inTokenIDs;
	private List<String> inTokenNames;

	@Override
	protected void parameters_init() {

		super.parameters_init();
		
		inTokenIDs = new ArrayList<Integer>();
		inTokenNames = new ArrayList<String>();
	}
	
	@Override
	protected void parameters_load(ParametersString buffer) {

		super.parameters_load(buffer);
		
		ListUtil.stringAsList(inTokenNames, buffer.getString("inTokenNames"));

		// Convert token names to a list of IDs
		inTokenIDs.clear();
		
		for (String tokenName : inTokenNames)			
			inTokenIDs.add(Tokens.getTokenId(tokenName));
	}

	@Override
	protected void parameters_reset() {

		super.parameters_reset();
		inTokenIDs.clear();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {

		super.parameters_save(buffer);

		// Convert IDs to token names
		inTokenNames.clear();
		
		for (Integer tokenId : inTokenIDs)
			inTokenNames.add(Tokens.getTokenName(tokenId));

		buffer.setString("inTokenNames", ListUtil.listAsString(inTokenNames));
	}

	public List<Integer> getInTokenIDs() {
		
		return inTokenIDs;
	}
			
}
