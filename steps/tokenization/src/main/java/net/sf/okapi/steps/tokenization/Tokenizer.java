/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.plaintext.common.TextUnitUtils;
import net.sf.okapi.steps.tokenization.tokens.Tokens;
import net.sf.okapi.steps.tokenization.tokens.TokensAnnotation;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

public class Tokenizer {

	protected static TokenizationStep ts = new TokenizationStep();
	
	protected Tokens tokenizeString(String text, String language, String... tokenTypes) {
			
		Tokens res = new Tokens();
		
		if (ts == null)
			return res;
		
		Parameters params = (Parameters) ts.getParameters();
		params.reset();
		
		params.tokenizeSource = true;
		params.tokenizeTargets = false;
		
		params.languageMode = Parameters.LANGUAGES_ONLY_WHITE_LIST;
		params.languageWhiteList = language;
		
		params.tokenMode = Parameters.TOKENS_ONLY_LISTED;
		
		if (!Util.isEmpty(tokenTypes))
			params.tokenTypes = tokenTypes.toString();
		
		ts.handleEvent(new Event(EventType.START_BATCH));
		
		TextUnit tu = TextUnitUtils.buildTU(text);
		Event event = new Event(EventType.TEXT_UNIT, tu);
		
		ts.handleEvent(event);
		
		// Move tokens from the event's annotation to result
		TokensAnnotation ta = TextUnitUtils.getSourceAnnotation(tu, TokensAnnotation.class);
		if (ta != null)
			res.addAll(ta.getTokens());
		
		ts.handleEvent(new Event(EventType.END_BATCH));
		
		return res;
	}
	
	static private Tokens doTokenize(Object text, String language, String... tokenTypes) {
		
		if (text == null) return null;
		if (Util.isEmpty(language)) return null;
		
		if (text instanceof TextUnit) {
		
			TextUnit tu = (TextUnit) text;
			
			if (tu.hasTarget(language))
				return doTokenize(tu.getTarget(language), language, tokenTypes);
			else
				return doTokenize(tu.getSource(), language, tokenTypes);
		}
		else if (text instanceof TextContainer) {
			
			TextContainer tc = (TextContainer) text;
			
			return doTokenize(tc.getContent(), language, tokenTypes);
			
		}
		else if (text instanceof TextFragment) {
			
			TextFragment tf = (TextFragment) text;
			
			return doTokenize(TextUnitUtils.getText(tf), language, tokenTypes);
		}
		else if (text instanceof String) {
			
			Tokenizer tokenizer = new Tokenizer();
			if (tokenizer == null) return null;
				
			return tokenizer.tokenizeString((String) text, language, tokenTypes);
		}
		
		return null;		
	}
	
	static public Tokens tokenize(TextUnit textUnit, String language, String... tokenTypes) {
		
		return doTokenize(textUnit, language, tokenTypes);		
	}
	
	static public Tokens tokenize(TextContainer textContainer, String language, String... tokenTypes) {
		
		return doTokenize(textContainer, language, tokenTypes);		
	}
	
	static public Tokens tokenize(TextFragment textFragment, String language, String... tokenTypes) {
		
		return doTokenize(textFragment, language, tokenTypes);		
	}
	
	static public Tokens tokenize(String string, String language, String... tokenTypes) {
		
		return doTokenize(string, language, tokenTypes);		
	}
}
