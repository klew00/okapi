package net.sf.okapi.steps.tokenization.engine;

import net.sf.okapi.steps.tokenization.common.AbstractLexer;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.common.LexerRules;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class FilteringLexer extends AbstractLexer {

	@Override
	protected Class<? extends LexerRules> lexer_getRulesClass() {

		return LexerRules.class;
	}

	@Override
	public boolean lexer_hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void lexer_init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Lexem lexer_next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void lexer_open(String text, String language, Tokens tokens) {
		// TODO Auto-generated method stub
		
	}

	public Lexems process(String text, String language, Tokens tokens) {
		// TODO Auto-generated method stub
		return null;
	}

}
