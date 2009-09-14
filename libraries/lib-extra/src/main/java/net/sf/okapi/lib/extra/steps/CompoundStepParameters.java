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

package net.sf.okapi.lib.extra.steps;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.lib.extra.AbstractParameters;
import net.sf.okapi.lib.extra.Notification;

public class CompoundStepParameters extends AbstractParameters {

	public String description;
	private List<CompoundStepParametersItem> items = new ArrayList<CompoundStepParametersItem>();	

	@Override
	protected void parameters_load(ParametersString buffer) {

		description = buffer.getString("description", "");
		loadGroup(buffer, "CompoundStepParametersItem", items, CompoundStepParametersItem.class);
		
		instantiateItems(); // Parameters' editors also need instances to fetch desctiptions etc.
		
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
		saveGroup(buffer, "CompoundStepParametersItem", items);
	}

	public List<CompoundStepParametersItem> getItems() {
		
		return items;
	}

	/**
	 * Instantiate steps stored in CompoundStepItem.stepClass, load parameters in CompoundStepItem.parametersLocation  
	 */
	public void instantiateItems() {
		
		for (CompoundStepParametersItem item : items) {
			
			try {
				if (item == null) continue;
				item.step = (IPipelineStep) Class.forName(item.stepClass).newInstance();
				if (item.step == null) continue;
				
				item.parameters = item.step.getParameters();  
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
		
	}
	
}
