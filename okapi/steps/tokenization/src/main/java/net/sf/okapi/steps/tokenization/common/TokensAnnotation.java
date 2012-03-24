/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization.common;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

public class TokensAnnotation implements IAnnotation {

	private Tokens tokens = new Tokens();

	public TokensAnnotation (Tokens tokens) {
		super();
		this.tokens = tokens;
	}

	public void setTokens (Tokens tokens) {
		this.tokens = tokens;
	}

	public Tokens getTokens () {
		return tokens;
	} 
	
	public Tokens getFilteredList (String... tokenTypes) {
		if (tokens == null) return getTokens(); // return all
		return tokens.getFilteredList(tokenTypes);
	}

	public void addTokens (Tokens tokens) {
		this.tokens.addAll(tokens);
		// TODO Handle overlapping and duplicate ranges for the same token type
	}
	
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		for ( Token token : tokens ) {
			if ( sb.length()>0 ) sb.append(" ");
			sb.append(token.toString());
		}
		return sb.toString();
	}
}
