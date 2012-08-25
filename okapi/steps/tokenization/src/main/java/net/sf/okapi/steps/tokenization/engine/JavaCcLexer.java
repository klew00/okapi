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

import java.io.IOException;
import java.io.StringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.steps.tokenization.common.AbstractLexer;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.engine.javacc.ParseException;
import net.sf.okapi.steps.tokenization.engine.javacc.SimpleCharStream;
import net.sf.okapi.steps.tokenization.engine.javacc.TokenMgrError;
import net.sf.okapi.steps.tokenization.engine.javacc.WordTokenizer;
import net.sf.okapi.steps.tokenization.engine.javacc.WordTokenizerTokenManager;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class JavaCcLexer extends AbstractLexer {
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private SimpleCharStream stream;
	private WordTokenizer tokenizer;
	private boolean hasNext;
	
	@Override
	protected boolean lexer_hasNext() {

		return hasNext;
	}

	@Override
	protected void lexer_init() {
		
		hasNext = false;
	}

	@Override
	protected Lexem lexer_next() {
	
		net.sf.okapi.steps.tokenization.engine.javacc.Token token = null;
		
		try {
			token = tokenizer.nextToken();
			
		} catch (TokenMgrError e) {
			
			logger.debug("JavaCC error: " + e.getMessage());
			return null;
			
		} catch (Error e) {
		
			logger.debug("JavaCC error: " + e.getMessage());
			return null;
		
		} catch (ParseException e) {

			logger.debug("JavaCC parsing exception: " + e.getMessage());
			return null;
			
		} catch (IOException e) {

			logger.debug("JavaCC IO exception: " + e.getMessage());
			return null;
		}

		if (token == null) {
			
			hasNext = false;
			return null;
		}
		
		int end = stream.bufpos + 1;
		int start = end - token.image.length();
		
		if (start < 0) return null;
		if (start > end) return null;
		
		int lexemId = token.kind;
		Lexem lexem = new Lexem(lexemId, token.image, start, end);
		
		return lexem;
	}

	@Override
	protected void lexer_open (String text, LocaleId language, Tokens tokens) {
		
		StringReader sr = new StringReader(text);
		stream = new SimpleCharStream(sr);
		WordTokenizerTokenManager tm = new WordTokenizerTokenManager(stream);
		
		tokenizer = new WordTokenizer(tm);
		hasNext = true;
	}

	public Lexems process(String text, LocaleId language, Tokens tokens) {
		return null;
	}

}
