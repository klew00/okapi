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

package net.sf.okapi.steps.wordcount.engine;

import net.sf.okapi.steps.common.counting.BaseCounter;
import net.sf.okapi.steps.tokenization.engine.Token;
import net.sf.okapi.steps.tokenization.engine.Tokenizer;
import net.sf.okapi.steps.tokenization.engine.Tokens;

/**
 * Word Count engine. Contains static methods to calculate number of words in a given text fragment. 
 * 
 * @version 0.1 07.07.2009
 */

public class WordCounter extends BaseCounter {

	@Override
	protected long doGetCount(String text, String language) {
		
		Tokens tokens = Tokenizer.tokenize(text, language, new String[]{Token.WORD});		
		if (tokens == null) return 0;
		
		return tokens.size();
	}
	
	/**
	 * 
	 * @param text any of the following:
	 * <li> TextUnit
	 * <li> TextContainer
	 * <li> TextFragment
	 * <li> String
	 * @param language RFC 4646, 4647 language tag (like "en-us")
	 * @return word count in the current text
	 */
	static public long getCount(Object text, String language) {
		
		return getCount(WordCounter.class, text, language);		
	}

}
