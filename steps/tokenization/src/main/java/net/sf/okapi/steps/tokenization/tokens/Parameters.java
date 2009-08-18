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

import java.net.URISyntaxException;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.filters.plaintext.common.AbstractParameters;

public class Parameters extends AbstractParameters {

	public Tokens tokens;
	public Tokens selectedTokens;
	
	@Override
	protected void parameters_init() {
		
		tokens = new Tokens();
		selectedTokens = new Tokens();
	}

	@Override
	protected void parameters_load(ParametersString buffer) {
		
		tokens.parameters_load(buffer);
	}

	@Override
	protected void parameters_reset() {
		
		tokens.clear();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		tokens.parameters_save(buffer);
	}

	public boolean loadTokens() {
		
		try {
			load(getClass().getResource("okf_tokens.fprm").toURI(), false);
						
		} catch (URISyntaxException e) {
			
			return false;
		}
		
		return true;
	}
	
	public void saveTokens() {
		
		save(getClass().getResource("okf_tokens.fprm").getPath());
	}

	public void addToken(String name, int value, String description) {
		
		tokens.add(new Token(name, description, value));
	}
	
	public void addSelectedToken(String name, int value, String description) {
		
		selectedTokens.add(new Token(name, description, value));
	}
}
