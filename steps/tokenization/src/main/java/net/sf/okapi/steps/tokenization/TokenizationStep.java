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

import net.sf.okapi.steps.tokenization.common.CompoundStep;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

/**
 * 
 * 
 * @version 0.1 06.07.2009
 */

public class TokenizationStep extends CompoundStep {

	Parameters params;
	
	public TokenizationStep() {
		
		super();
		
		setParameters(new Parameters());
		setName("Tokenization");
		setDescription("Extracts tokens from the text units content of a document.");
		
		params = getParameters(Parameters.class);
		if (params == null) return;
		
		params.loadFromResource("okf_tokenizer.fprm");				
	}

	public void tokenize(String text, Tokens tokens, String language, String... tokenTypes) {
		
		// TODO Goes through registered internal tokenizers, passes them their params, analyzes & collects results
	}
			
}
