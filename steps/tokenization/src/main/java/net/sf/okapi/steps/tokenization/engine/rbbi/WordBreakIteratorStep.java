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

package net.sf.okapi.steps.tokenization.engine.rbbi;

import java.util.TreeMap;

import net.sf.okapi.common.Util;
import net.sf.okapi.steps.tokenization.common.AbstractTokenizationStep;
import net.sf.okapi.steps.tokenization.tokens.Token;
import net.sf.okapi.steps.tokenization.tokens.TokenType;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.RuleBasedBreakIterator;
import com.ibm.icu.util.ULocale;

public class WordBreakIteratorStep extends AbstractTokenizationStep {

	// Cache for iterators reuse
	private TreeMap <String, RuleBasedBreakIterator> iterators = new TreeMap <String, RuleBasedBreakIterator>();
	
	@Override
	public void tokenize(String text, Tokens tokens, String language, String... tokenTypes) {
		
		// TODO Check if the given language and tokens are handled by this step
		
		if (Util.isEmpty(text)) return;
		if (tokens == null) return;
		
		RuleBasedBreakIterator iterator = null;
		
		if (iterators.containsKey(language))
			iterator = iterators.get(language);
		else {
			
			iterator = (RuleBasedBreakIterator) BreakIterator.getWordInstance(ULocale.createCanonical(language));
			iterators.put(language, iterator);
		}

		if (iterator == null) return;
		
		// TODO Get default rules
		// TODO Get custom rules from params
		// TODO Insert custom rules into defaults
		// TODO Set updated rules
		
		// TODO Tokenize
		
		iterator.setText(text);		
		int start = iterator.first();
		int status;
		
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			
			status = iterator.getRuleStatus();
			System.out.println(String.format("%d\t\t[%s]\t\t(%d, %d)", status, text.substring(start,end), start, end));
			
			if (status == 0)
				tokens.add(new Token(TokenType.UNKNOWN, start, end, text.substring(start,end), 1));
			else
				tokens.add(new Token(TokenType.WORD, start, end, text.substring(start,end), 1));
		}
		
//		System.out.println(tokens.toString());
	}

	
}
