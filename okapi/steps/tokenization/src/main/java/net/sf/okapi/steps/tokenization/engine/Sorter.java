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

import java.util.Collections;
import java.util.Comparator;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.steps.tokenization.common.AbstractLexer;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class Sorter extends AbstractLexer {

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
	protected void lexer_open(String text, LocaleId language, Tokens tokens) {
	}

	private Comparator<Token> rangeComparator = new Comparator<Token>() {
      public int compare(Token token1, Token token2) {

      	int s1 = token1.getLexem().getRange().start;
      	int s2 = token2.getLexem().getRange().start;
      	
      	if (s1 < s2) return -1;        	      	
      	if (s1 > s2) return 1;
      	
      	if (s1 == s2) {
      		
      		int e1 = token1.getLexem().getRange().end;
          	int e2 = token2.getLexem().getRange().end;

          	// Longer tokens go first
      		if (e1 < e2) return 1;
      		if (e1 > e2) return -1;
      	}
      	
      	return 0;
      }    
	};

	public Lexems process(String text, LocaleId language, Tokens tokens) {
		Collections.sort(tokens, rangeComparator);
		return null;
	}

}
