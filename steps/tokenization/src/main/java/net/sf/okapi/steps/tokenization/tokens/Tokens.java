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

package net.sf.okapi.steps.tokenization.tokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.okapi.common.ListUtils;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

public class Tokens extends ArrayList<Token> {

	private static final long serialVersionUID = 2484560539089898608L;

	/**
	 * Return a list of Token objects. If tokenTypes are specified, only the tokens of those types will be placed
	 * in the resulting list. If tokenTypes is omitted, the list of all tokens will be returned. 
	 * @param tokenTypes Optional array of strings with token type constants.
	 * @return List of tokens.
	 */
	public Tokens getFilteredList(String... tokenTypes) {
		
		List<String> types = null; 
		
		if (tokenTypes == null)
			return this;
		else
			types = Arrays.asList(tokenTypes);

		Tokens res = new Tokens ();
		for (int i = 0; i < this.size(); i++) {
			
			Token token = this.get(i);
			
			if (token == null) continue;
			
			if (types.contains(token.typeID))
				res.add(token);
		}
		
		return res;
	}
	
	public void fixRanges(List<Integer> markerPositions) {
		
		for (Integer pos : markerPositions)			
			for (Token token : this) {
			
				if (token.range.start > pos)
					token.range.start += 2;
				
				if (token.range.end > pos)
					token.range.end += 2;
			}			
		}

	@Override
	public String toString() {

		List<String> res = new ArrayList<String>();
		
		for (Token token : this) {
			
			res.add(token.toString());
		}
		
		return ListUtils.listAsString(res, "\n");
	}

}
