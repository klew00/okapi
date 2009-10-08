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

package net.sf.okapi.steps.wordcount;

import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.tokenization.Tokenizer;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.tokens.Tokens;
import net.sf.okapi.steps.wordcount.common.BaseCounter;

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
	
	static public long getCount(TextUnit textUnit, String language) {
		
		return getCount(WordCounter.class, textUnit, language);		
	}
	
	static public long getCount(TextContainer textContainer, String language) {
		
		return getCount(WordCounter.class, textContainer, language);		
	}

	static public long getCount(TextFragment textFragment, String language) {
		
		return getCount(WordCounter.class, textFragment, language);		
	}
	
	static public long getCount(String string, String language) {
		
		return getCount(WordCounter.class, string, language);		
	}

}
