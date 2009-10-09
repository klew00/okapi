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

package net.sf.okapi.steps.tokenization.engine;

import net.sf.okapi.common.Range;
import net.sf.okapi.steps.tokenization.common.AbstractLexer;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class Replacer extends AbstractLexer {

	@Override
	protected boolean lexer_hasNext() {

		return false;
	}

	@Override
	protected void lexer_init() {


	}

	@Override
	protected Lexem lexer_next() {

		return null;
	}

	@Override
	protected void lexer_open(String text, String language, Tokens tokens) {


	}

//	private boolean checkEqual(Token token1, Token token2) {
//		
//		if (token1 == null || token2 == null) return false;
//		
//		Range r1 = token1.getRange();
//		Range r2 = token2.getRange();
//		
//		return r1.start == r2.start && r1.end == r2.end && token1.getTokenId() == token2.getTokenId();
//	}
	
	/**
	 * Returns true if range2 is within range.
	 * @param range
	 * @param range2
	 * @return
	 */
	private boolean contains(Range range, Range range2) {

		// Exact matches are dropped
		return (range.start < range2.start && range.end >= range2.end) ||
			(range.start <= range2.start && range.end > range2.end);
	}

	public Lexems process(String text, String language, Tokens tokens) {

		// If the token's range includes other tokens' ranges, destroy those.
		// TODO Find something better than o(n2) 
		Tokens wasteBin = new Tokens();
		
		for (Token token : tokens) {

			if (wasteBin.indexOf(token) != -1) continue; // Skip the to be deleted
			
			for (Token token2 : tokens) {
				
				if (token2 == token) continue;
				
					if (//checkEqual(token, token2) || // Remove duplicate tokens 
							contains(token.getLexem().getRange(), token2.getLexem().getRange())) // Remove overlapped tokens					
						wasteBin.add(token2);
			}
		}
		
		for (Token token : wasteBin)			
			tokens.remove(token);		

		return null;
	}

}
