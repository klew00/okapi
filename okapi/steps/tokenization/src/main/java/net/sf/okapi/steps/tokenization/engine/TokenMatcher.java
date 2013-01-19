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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.steps.tokenization.common.AbstractLexer;
import net.sf.okapi.steps.tokenization.common.InputTokenAnnotation;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.common.LexerRule;
import net.sf.okapi.steps.tokenization.common.LexerRules;
import net.sf.okapi.steps.tokenization.common.RegexRule;
import net.sf.okapi.steps.tokenization.common.RegexRules;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class TokenMatcher extends AbstractLexer {
	 
	private LexerRules rules;
	private LinkedHashMap<LexerRule, Pattern> patterns;  
	  
	@Override
		protected Class<? extends LexerRules> lexer_getRulesClass() {

			return RegexRules.class;
		}
	 
	@Override
	protected boolean lexer_hasNext() {

		return false;
	}

	@Override
	protected void lexer_init() {
		
//		patterns = new HashMap<LexerRule, Pattern>();
//		rules = getRules();
//		
//		for (LexerRule rule : rules) {
//			
//			Pattern pattern = Pattern.compile(rule.getPattern());
//			patterns.put(rule, pattern);
//		}
		
		patterns = new LinkedHashMap<LexerRule, Pattern>();
		rules = getRules();
		
		for (LexerRule item : rules) {
			
			RegexRule rule = (RegexRule) item;
			
			Pattern pattern = null;
			if (rule.getPattern() != null)
				pattern = Pattern.compile(rule.getPattern(), rule.getRegexOptions());
			
			patterns.put(rule, pattern);
		}
	}

	@Override
	protected Lexem lexer_next() {

		return null;
	}

	@Override
	protected void lexer_open(String text, LocaleId language, Tokens tokens) {

	}

	public Lexems process(String text, LocaleId language, Tokens tokens) {
		
		Lexems lexems = new Lexems();
		
		for (LexerRule item : rules) {
			
			RegexRule rule = (RegexRule) item;
			
			if (!checkRule(rule, language)) continue;
			List<Integer> inTokenIDs = rule.getInTokenIDs();
			
			Pattern pattern = patterns.get(rule);
			if (pattern == null) continue;
			
			for (Token token : tokens) {
				
				//if (token.isDeleted()) continue;
				
				if (inTokenIDs.contains(token.getTokenId())) {
					
					Matcher matcher = pattern.matcher(token.getValue());
					
				    if (matcher.matches()) {
				    	
				    	Lexem lexem = new Lexem(rule.getLexemId(), token.getValue(), token.getRange());
				    	lexem.setAnnotation(new InputTokenAnnotation(token));
				    	lexems.add(lexem);
				    	
				    	if (!rule.getKeepInput())
				    		token.delete(); // Remove replaced token				    	
				    }
				}
			}				
		}
		
		return lexems;
	}

}
