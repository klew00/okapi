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

import net.sf.okapi.common.Range;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

public class Token {

	final static public String UNKNOWN = "TOKEN_UNKNOWN";
	final static public String WORD = "TOKEN_WORD"; 
	final static public String PUNCTUATION = "TOKEN_PUNCTUATION";
	final static public String WHITESPACE = "TOKEN_WHITESPACE";
	final static public String DATE = "TOKEN_DATE";
	final static public String NUMBER = "TOKEN_NUMBER";
	final static public String CURRENCY = "TOKEN_CURRENCY";
	final static public String NAME = "TOKEN_NAME";
	final static public String EMAIL = "TOKEN_EMAIL";
	final static public String INTERNET = "TOKEN_INTERNET";
	final static public String ABBREVIATION = "TOKEN_ABBREVIATION";
	
//-------------------------------------------------------------------------------------------------
	
	public String type;
	public Range range;
	public String lexem;
	public int score;
	public Object owner;
}
