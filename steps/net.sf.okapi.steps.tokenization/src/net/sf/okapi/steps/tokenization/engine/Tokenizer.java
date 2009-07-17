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

package net.sf.okapi.steps.tokenization.engine;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.common.utils.TextUnitUtils;
import net.sf.okapi.steps.tokenization.TokenizationStep;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

public class Tokenizer {

	protected Tokens doTokenize(Object text, String language, String... tokenTypes) {
		
		@SuppressWarnings("unused")
		TokenizationStep ts = new TokenizationStep();  
			
		return null;		
	}
	
	static protected Tokens _tokenize(Object text, String language, String... tokenTypes) {
		
		if (text == null) return null;
		if (Util.isEmpty(language)) return null;
		
		if (text instanceof TextUnit) {
		
			TextUnit tu = (TextUnit) text;
			
			if (tu.hasTarget(language))
				return _tokenize(tu.getTarget(language), language, tokenTypes);
			else
				return _tokenize(tu.getSource(), language, tokenTypes);
		}
		else if (text instanceof TextContainer) {
			
			TextContainer tc = (TextContainer) text;
			
			return _tokenize(tc.getContent(), language, tokenTypes);
			
		}
		else if (text instanceof TextFragment) {
			
			TextFragment tf = (TextFragment) text;
			
			return _tokenize(TextUnitUtils.getText(tf), language, tokenTypes);
		}
		else if (text instanceof String) {
			
			Tokenizer tokenizer = new Tokenizer();
			if (tokenizer == null) return null;
				
			return tokenizer.doTokenize((String) text, language, tokenTypes);
		}
		
		return null;		
	}
	
	static public Tokens tokenize(Object text, String language, String... tokenTypes) {
		
		return _tokenize(text, language, tokenTypes);		
	}
	
}
