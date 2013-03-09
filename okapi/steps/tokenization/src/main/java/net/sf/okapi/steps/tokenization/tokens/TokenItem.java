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
import net.sf.okapi.lib.extra.AbstractParameters;

public class TokenItem extends AbstractParameters {

	private String name;
	private String description;
	
	public TokenItem() {
		
		super();
	}

	public TokenItem(String name, String description) {
		
		super();
		
		this.name = name;
		this.description = description;
	}

	@Override
	protected void parameters_load(ParametersString buffer) {
		
		name = buffer.getString("name");
		description = buffer.getString("description");
	}
	
	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setString("name", name);
		buffer.setString("description", description);
	}

	@Override
	protected void parameters_reset() {
		
		name = "";
		description = "";
	}

	public String getName() {
		
		return name;
	}

	public String getDescription() {
		
		return description;
	}

}
