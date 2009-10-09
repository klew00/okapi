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

public class DuplicateCleaner extends AbstractLexer {

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

	private boolean checkEqual(Token token1, Token token2) {
	
		if (token1 == null || token2 == null) return false;
		
		Range r1 = token1.getRange();
		Range r2 = token2.getRange();
		
		return r1.start == r2.start && r1.end == r2.end && token1.getTokenId() == token2.getTokenId();
	}

	public Lexems process(String text, String language, Tokens tokens) {

		// If 2 tokens are identical, destroy one.
		// TODO Find something better than o(n2) 
		Tokens wasteBin = new Tokens();
		
		for (Token token : tokens) {

			if (wasteBin.indexOf(token) != -1) continue; // Skip the to be deleted
			
			for (Token token2 : tokens) {
				
				if (token2 == token) continue;
				
					if (checkEqual(token, token2))					
						wasteBin.add(token2);
			}
		}
		
		for (Token token : wasteBin)			
			tokens.remove(token);		

		return null;
	}

}
