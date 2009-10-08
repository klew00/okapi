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

package net.sf.okapi.steps.tokenization.common;

import net.sf.okapi.common.Range;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

public class Token {
	
	final static public String UNKNOWN 			= "UNKNOWN";
	final static public String WORD 			= "WORD"; 
	final static public String PUNCTUATION 		= "PUNCTUATION";
	final static public String WHITESPACE 		= "WHITESPACE";
	final static public String DATE 			= "DATE";
	final static public String NUMBER 			= "NUMBER";
	final static public String CURRENCY 		= "CURRENCY";
	final static public String NAME 			= "NAME";
	final static public String EMAIL 			= "EMAIL";
	final static public String INTERNET 		= "INTERNET";
	final static public String ABBREVIATION 	= "ABBREVIATION";
	
	/**
	 * Integer identifier of the token. 
	 * !!! Non-serializable. 
	 */
	private int tokenId; 
	
	/**
	 * Underlying lexem extracted by a tokenizer.
	 */
	private Lexem lexem;
	
	/**
	 * Percentage reflecting trustworthiness of the token recognition.
	 */
	private int score;
	
	public Token(int tokenId, Lexem lexem, int score) {
		
		super();
		
		this.tokenId = tokenId;
		this.lexem = lexem;
		this.score = score;
	}

	/**
	 * Gets integer identifier of the token. 
	 * !!! Non-serializable. 
	 */
	public int getTokenId() {
		
		return tokenId;
	}

	public Lexem getLexem() {
		
		return lexem;
	}

	public Range getRange() {
		
		if (lexem == null) return new Range(0, 0);
		
		return lexem.getRange();
	}

	public int getScore() {
		
		return score;
	}

	public int getLexerId() {
		
		if (lexem == null) return 0;
		
		return lexem.getLexerId();
	}

	public int getLexemId() {
		
		if (lexem == null) return 0;
		
		return lexem.getId();
	}
	
	public String getValue() {
		
		if (lexem == null) return "";
		
		return lexem.getValue();
	}

	public String getName() {
		
		return Tokens.getTokenName(tokenId);
	}
	
	public String getDescription() {
				
		return Tokens.getTokenDescription(tokenId);
	}

	@Override
	public String toString() {
		
		return String.format("%-15s\t%d\t%3d%%\t%s", 
				getName(), tokenId, score, lexem.toString());
	}
		
}

