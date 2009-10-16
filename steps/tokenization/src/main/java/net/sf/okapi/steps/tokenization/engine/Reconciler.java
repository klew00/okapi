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

public class Reconciler extends AbstractLexer {

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
		
		for (int i = 0; i < tokens.size(); i++) {
			
			Token token1 = tokens.get(i);
			if (token1.isDeleted()) continue;
			
			for (int j = 0; j < tokens.size(); j++) {
		
				if (i >= j) continue;
				
				Token token2 = tokens.get(j);				
				if (token2.isDeleted()) continue;
				if (token2 == token1) continue;				
			
				Range r1 = token1.getRange();
				Range r2 = token2.getRange();
												
				if (r1.start == r2.start && r1.end == r2.end) { // Same range
					
					if (token1.getTokenId() == token2.getTokenId()) { // Tokens are identical, remove duplication
						
						token2.delete();
						continue;
					}
					
					
				}
					
					
				
				
				
				

				// If the token's range includes other tokens' ranges, destroy those.
//				if (//checkEqual(token, token2) || // Remove duplicate tokens 
//				contains(token.getLexem().getRange(), token2.getLexem().getRange())) // Remove overlapped tokens					
//				//wasteBin.add(token2);
//				token2.delete();

			}
		}	

		return null;
	}

}
