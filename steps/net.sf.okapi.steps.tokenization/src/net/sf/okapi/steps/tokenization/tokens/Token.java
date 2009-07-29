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

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.Range;
import net.sf.okapi.filters.plaintext.common.IParametersHandler;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

public class Token implements IParametersHandler {

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
	
//-------------------------------------------------------------------------------------------------
	
	public String name;
	public String description;
	public int value;
	public Range range;
	public String lexem;
	public int score;
	public Object owner;
	
	public Token() {
		
		super();
	}

	public Token(String name, String description, int value) {
		
		super();
		
		this.name = name;
		this.description = description;
		this.value = value;
	}

	public void parameters_load(ParametersString buffer) {
		
		name = buffer.getString("name");
		description = buffer.getString("description");
		value = buffer.getInteger("value");
	}
	
	public void parameters_save(ParametersString buffer) {
		
		buffer.setString("name", name);
		buffer.setString("description", description);
		buffer.setInteger("value", value);
	}
}

