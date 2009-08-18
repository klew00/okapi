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

import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.plaintext.common.TextUnitUtils;
import net.sf.okapi.steps.tokenization.TokenizationStep;
import net.sf.okapi.steps.tokenization.tokens.Tokens;
import net.sf.okapi.steps.tokenization.tokens.TokensAnnotation;

public abstract class TokenCountStep extends BaseCountStep {

	private TokenizationStep ts = null;
	
	protected abstract String getToken();
	
	@Override
	protected long getCount(TextUnit textUnit) {
		
		TokensAnnotation ta = TextUnitUtils.getSourceAnnotation(textUnit, TokensAnnotation.class);
		Tokens tokens = null;
		
		if (ta != null) {
			
			tokens = ta.getFilteredList(new String[]{getToken()});			
		}
		else {
			
			if (ts == null)
				ts = new TokenizationStep();
			
			if (ts == null) return 0;
			
			tokens = ts.tokenize(TextUnitUtils.getSourceText(textUnit, true), getLanguage(), new String[]{getToken()});
		}
		
		if (tokens == null) return 0;
		
		return tokens.size();		
	}

}
