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

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.steps.tokenization.common.AbstractLexer;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.common.LexerRule;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class Recognizer extends AbstractLexer {
	 
	 private List<LexerRule> items;
	 private HashMap<LexerRule, Pattern> patterns;  
	  
	@Override
	protected boolean lexer_hasNext() {

		return false;
	}

	@Override
	protected void lexer_init() {
		
		patterns = new HashMap<LexerRule, Pattern>();
		items = getRules();
		
		for (LexerRule item : items) {
			
			Pattern pattern = Pattern.compile(item.getRule());
			patterns.put(item, pattern);
		}
							
	}

	@Override
	protected Lexem lexer_next() {

		return null;
	}

	@Override
	protected void lexer_open(String text, String language, Tokens tokens) {

	}

	public Lexems process(String text, String language, Tokens tokens) {
		
		Lexems lexems = new Lexems();
		Tokens wasteBin = new Tokens();
		
		for (LexerRule item : items) {
			
			List<Integer> inTokenIDs = item.getInTokenIDs();
			
			Pattern pattern = patterns.get(item);
			if (pattern == null) continue;
			
			for (Token token : tokens)			
				if (inTokenIDs.contains(token.getTokenId())) {
				
					Matcher matcher = pattern.matcher(token.getValue());
					
				    if (matcher.matches()) {
				    	
				    	lexems.add(new Lexem(item.getLexemId(), token.getValue(), token.getRange(), getLexerId()));
				    	wasteBin.add(token); // Remove replaced token
				    }
				}
		}
		
		for (Token token : wasteBin)			
			tokens.remove(token);
		
		return lexems;
	}

}
