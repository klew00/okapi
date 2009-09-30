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

package net.sf.okapi.steps.tokenization.tokens;

import net.sf.okapi.common.Range;

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
	 */
	private int id; 
	
	/**
	 * Substring of text captured as the given token.
	 */
	private String value;
	
	/**
	 * Range of text captured as the given token.
	 */
	private Range range;
	
	/**
	 * Percentage reflecting trustworthiness of the token recognition.
	 */
	private int score;
	
	/**
	 * ID of the rule that captured this token. Used by the scoring engine internally.
	 */
	private int ruleId;
	
	public Token(int id, String value, int start, int end, int score, int ruleId) {
		
		super();
		
		this.id = id;
		this.range = new Range(start, end);
		this.value = value;
		this.score = score;
		this.ruleId = ruleId;
	}

	public int getId() {
		
		return id;
	}

	public String getValue() {
		
		return value;
	}

	public Range getRange() {
		
		return range;
	}

	public int getScore() {
		
		return score;
	}

	public int getRuleId() {
		
		return ruleId;
	}
	
	public String getName() {
		
		return Tokens.getTokenName(id);
	}
	
	public String getDescription() {
				
		return Tokens.getTokenDescription(id);
	}

	@Override
	public String toString() {
		
		return String.format("%s [%d]\t\t(Range: %d, %d)\t\t[%s]\t(Score: %d)\t(Rule: %d)", 
				getName(), id, range.start, range.end, value, score, ruleId);
	}
		
}

