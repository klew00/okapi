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

package net.sf.okapi.steps.tokenization.ui.mapping.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.Util;
import net.sf.okapi.lib.extra.AbstractParameters;

public class Parameters extends AbstractParameters {

	private List<MappingItem> items; 
	
	@Override
	protected void parameters_init() {
		
		items = new ArrayList<MappingItem>(); 
	}

	@Override
	protected void parameters_load(ParametersString buffer) {
		
		loadGroup(buffer, items, MappingItem.class);
	}

	@Override
	protected void parameters_reset() {
		
		items.clear();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		saveGroup(buffer, items, MappingItem.class);
	}
	
	public void addMapping(String editorClass, String parametersClass) {
		
		MappingItem item = new MappingItem();
		
		item.editorClass = editorClass;
		item.parametersClass = parametersClass;
		
		items.add(item);
	}

	public List<MappingItem> getItems() {
		
		return items;
	}

	public String getParametersClass(String editorClass) {
		
		if (Util.isEmpty(editorClass)) return "";
			
		for (MappingItem item : items) {
			
			if (item.editorClass.equalsIgnoreCase(editorClass))
				return item.parametersClass;
		} 
		
		return "";
	}
	
	public String getEditorClass(String parametersClass) {
		
		if (Util.isEmpty(parametersClass)) return "";
		
		for (MappingItem item : items) {
			
			if (item.parametersClass.equalsIgnoreCase(parametersClass))
				return item.editorClass;
		} 
		
		return "";
	}
	
//	public boolean loadMapping() {
//		
//		try {
//			load(getClass().getResource("mapper.tprm").toURI(), false);
//						
//		} catch (URISyntaxException e) {
//			
//			return false;
//		}
//		
//		return true;
//	}
//	
//	public void saveMapping() {
//		
//		save(getClass().getResource("mapper.tprm").getPath());
//	}
	
}
