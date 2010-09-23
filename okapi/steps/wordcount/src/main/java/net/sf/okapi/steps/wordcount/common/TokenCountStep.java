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

package net.sf.okapi.steps.wordcount.common;

import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.steps.tokenization.Tokenizer;
import net.sf.okapi.steps.tokenization.common.TokensAnnotation;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public abstract class TokenCountStep extends BaseCountStep {

	protected abstract String getTokenName();
	
	@Override
	protected long count(TextUnit textUnit) {		
		TokensAnnotation ta = TextUnitUtil.getSourceAnnotation(textUnit, TokensAnnotation.class);
		Tokens tokens = null;
		
		if (ta != null)			
			tokens = ta.getFilteredList(getTokenName());			
		else
			tokens = Tokenizer.tokenize(textUnit.getSource(), getLanguage(), getTokenName());
		
		if (tokens == null) return 0;
		
		return tokens.size();		
	}
	
		
	@Override
	protected long count(Segment segment) {		
		TokensAnnotation ta = segment.getAnnotation(TokensAnnotation.class);
		Tokens tokens = null;
		
		if (ta != null)			
			tokens = ta.getFilteredList(getTokenName());			
		else
			tokens = Tokenizer.tokenize(segment.getContent(), getLanguage(), getTokenName());
		
		if (tokens == null) return 0;
		
		return tokens.size();		
	}
}
