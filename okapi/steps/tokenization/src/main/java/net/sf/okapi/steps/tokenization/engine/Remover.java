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

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.steps.tokenization.common.AbstractLexer;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.common.LexerRule;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class Remover extends AbstractLexer {
	
	@Override
	public boolean lexer_hasNext() {
		
		return false; // Iterator is not used
	}

	@Override
	public void lexer_init() {

	}

	@Override
	public Lexem lexer_next() {

		return null; // Iterator is not used
	}

	@Override
	public void lexer_open(String text, LocaleId language, Tokens tokens) {
		
		 // Iterator is not used
	}

	public Lexems process(String text, LocaleId language, Tokens tokens) {

		if (tokens == null) return null;
				
		for (LexerRule rule : getRules()) {

			if (!checkRule(rule, language)) continue;
			
			List<Integer> inTokenIDs = rule.getInTokenIDs();
			
			for (Token token : tokens) {
				
				if (token.isDeleted()) continue;
				
				if (inTokenIDs.contains(token.getTokenId())) // Remove listed tokens
					token.delete();
			}				
		}
		
		return null;
	}

}
