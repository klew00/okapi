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

package net.sf.okapi.filters.plaintext.common;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;

/**
 * The root of the filters hierarchy. Defines generic methods for all kinds of filters.
 * 
 * @version 0.1, 10.06.2009
 */

public abstract class AbstractFilter extends OkapiComponent implements IFilter {
	
	private String mimeType;		
	List<FilterConfiguration> configList = new ArrayList<FilterConfiguration>();

//	@Override
//	protected void component_create() {
//	public AbstractFilter() {
//		
//		configList = new ArrayList<FilterConfiguration>();
//	}
	
	public AbstractFilter() {
		
		super();
	}

	/**
	 * Sets the input document mime type.
	 * 
	 * @param mimeType the new mime type
	 */
	protected void setMimeType(String mimeType) {
		
		this.mimeType = mimeType;
	}

	/**
	 * Gets the input document mime type.
	 * 
	 * @return the mime type
	 */

	public String getMimeType() {
		
		return mimeType;
	}
	
	protected boolean addConfiguration(			
			boolean clearAllExisting,
			String configId,
			String name,
			String description,
			String parametersLocation) {
		
		if (configList == null) return false;
		
		if (clearAllExisting) configList.clear();
		
		return configList.add(new FilterConfiguration(
				configId,
				getMimeType(),
				getClass().getName(),
				name, description, parametersLocation));
	}
	
	protected boolean addConfigurations(List<FilterConfiguration> configs) {
	
		if (configList == null) return false;
		
		return configList.addAll(configs);
	}
	
	protected FilterConfiguration findConfiguration(String configId) {
		
		if (Util.isEmpty(configList)) return null;
		
		for (FilterConfiguration config : configList) {
			
			if (config == null) continue;
			if (config.configId.equalsIgnoreCase(configId)) 
				return config;
		}
		
		return null;
	}
	
	protected boolean removeConfiguration(String configId) {
		
		return configList.remove(findConfiguration(configId));
	}
				
	public List<FilterConfiguration> getConfigurations () {
		
		List<FilterConfiguration> res = new ArrayList<FilterConfiguration>();
		
		for (FilterConfiguration fc : configList) 
			res.add(new FilterConfiguration(
				fc.configId,
				getMimeType(),
				getClass().getName(),
				fc.name, fc.description, fc.parametersLocation));
		
		return res;
	}

	public boolean setConfiguration(String configId) {
		
		return true;
	}
	

}

