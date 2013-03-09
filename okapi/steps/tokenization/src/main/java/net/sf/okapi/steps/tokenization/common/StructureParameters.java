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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.lib.extra.AbstractParameters;
import net.sf.okapi.lib.extra.Notification;

public class StructureParameters extends AbstractParameters {

	public String description;
	private List<StructureParametersItem> items = new ArrayList<StructureParametersItem>();	

	@Override
	protected void parameters_load(ParametersString buffer) {

		description = buffer.getString("description", "");
		loadGroup(buffer, items, StructureParametersItem.class);
		
		if (owner != null)
			owner.exec(this, Notification.PARAMETERS_CHANGED, null);
	}

	@Override
	protected void parameters_reset() {
		
		description = "";
		
		if (items == null) return;
		items.clear();		
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setString("description", description);
		saveGroup(buffer, items, StructureParametersItem.class);
	}

	public List<StructureParametersItem> getItems() {
		
		return items;
	}

}
