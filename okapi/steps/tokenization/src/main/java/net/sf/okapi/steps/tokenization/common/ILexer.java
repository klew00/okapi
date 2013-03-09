/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

/**
 * Common set of methods to extract lexems from a string or a list of tokens.
 * 
 * <p>There are 2 ways of extraction provided: iterator and a single call. The implementing class can implement either of the 2 methods.
 * The iterator way is normally more memory-effective, though the single call can be desirable when an existing lexer/tokenizer object is
 * being wrapped around by this interface.
 * 
 * <p>Lexers, implementing this interface, can do the following things:
 * <li> split up a string into lexems
 * <li> split up longer lexems on a token list, providing further tokenization of existing tokens
 * <li> merge shorter lexems on a token list, providing further tokenization of existing tokens.
 * <pre>
 * </pre>
 * Example of iterator:
 * <pre>
 * ILexer lexer; 
 * Tokens tokens = new Tokens();
 * 
 * lexer.open("Tokenize me!", tokens);
 * 
 * while (lexer.hasNext())
 *     Lexem lexem = lexer.next();
 *     
 * lexer.close;
 *  </pre>
 * Example of a single call:
 * <pre>
 * ILexer lexer; 
 * Tokens tokens = new Tokens();
 * 
 * Lexems lexems = lexer.process("Tokenize me!", tokens);
 *  </pre>
 *  
**/
public interface ILexer {

	/**
	 * Initializes the lexer.
	 */
	void init();
	
//	/**
//	 * Assigns an ID to the lexer. !!! Non-serializable. 
//	 * @param lexerId 
//	 */
//	void setLexerId(int lexerId);
//	
//	/**
//	 * Gets the previously assigned lexer ID. !!! Non-serializable.
//	 * @return The lexer ID. 
//	 */
//	int getLexerId();
//			
	/**
	 * Gets the current rules for this lexer.
	 * @return The current rules for this lexer
	 */
	LexerRules getRules();

	/**
	 * Sets new rules for this lexer.
	 * @param rules The new rules to use
	 */
	void setRules(LexerRules rules);

	/**
	 * Starts processing a string or a list of tokens, extracting lexems from them.
	 * @param text The string to be processed
	 * @param language The language of the text
	 * @param tokens The string to be processed
	 */
	void open(String text, LocaleId language, Tokens tokens);
	
	/**
	 * Alternative non-iterator way of extracting lexems. In opposite to open()-hasNext()-next()-close(), 
	 * all extraction is done by a single method call. Implementations might be less memory-effective compared to the iterator. 
	 * @param text The string to be processed
	 * @param language The language of the text
	 * @param tokens The string to be processed
	 * @return A list of extracted lexems
	 */
	Lexems process(String text, LocaleId language, Tokens tokens);
	
	/**
	 * Indicates if there is a lexem extracted.
	 * @return True if there is at least one lexem has been extracted, false if none
	 */
	boolean hasNext();
	
	/**
	 * Gets the next lexem available.
	 * @return The next lexem available or null if there are no events
	 */
	Lexem next();
	
	/**
	 * Called after the lexer is done with extracting lexems.
	 */
	void close ();
	
	/**
	 * Cancels the current process.
	 */
	void cancel ();
		
}
