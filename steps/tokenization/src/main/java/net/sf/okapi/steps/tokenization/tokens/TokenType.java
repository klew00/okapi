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

package net.sf.okapi.steps.tokenization.tokens;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.filters.plaintext.common.AbstractParameters;

public class TokenType extends AbstractParameters {

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
	
//-------------------------------------------------------------------------------------------------
	
	public String id;
	public String description;
	
	public TokenType() {
		
		super();
	}
	
	public TokenType(String id, String description) {
		
		super();
		
		this.id = id;
		this.description = description;
	}

	@Override
	protected void parameters_load(ParametersString buffer) {
		
		id = buffer.getString("id");
		description = buffer.getString("description");
	}
	
	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setString("id", id);
		buffer.setString("description", description);
	}

	@Override
	protected void parameters_reset() {
		
		id = "";
		description = "";
	}
}
