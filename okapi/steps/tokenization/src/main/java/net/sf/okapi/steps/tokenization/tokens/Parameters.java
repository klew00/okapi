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

import java.util.ArrayList;
import java.util.List;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.lib.extra.AbstractParameters;

public class Parameters extends AbstractParameters {

	private List<TokenItem> items;
	
	@Override
	protected void parameters_init() {
		
		items = new ArrayList<TokenItem>();
	}

	@Override
	protected void parameters_load(ParametersString buffer) {
		
		loadGroup(buffer, items, TokenItem.class);
	}

	@Override
	protected void parameters_reset() {
		
		items.clear();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		//items.parameters_save(buffer);
		saveGroup(buffer, items, TokenItem.class);
	}

	public boolean loadItems() {
		
		return loadFromResource("tokens.tprm");
	}
	
	public void saveItems() {
		
		saveToResource("tokens.tprm");
	}

//	protected int generateId() {
//		// Slow, as used only from UI  
//		
//		int max = 0;
//		for (TokenItem item : items) {
//			
//			if (item == null) continue;
//			if (max < item.getId())
//				max = item.getId();
//		}
//		
//		//return (max > 0) ? max + 1: 0;
//		return max + 1;
//	}
	
	public void addTokenItem(String name, String description) {
		
		items.add(new TokenItem(name, description));
	}

	public List<TokenItem> getItems() {
		
		return items;
	}
	
}
