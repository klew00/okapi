/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

package net.sf.okapi.common.filters;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;

public class FilterUtil {

	/**
	 * Creates an instance of the filter for a given configuration identifier
	 * and loads its corresponding parameters. Only Okapi default filter configurations 
	 * are accepted. 
	 * @param configId the filter configuration identifier. Can only be one of default filter 
	 * configurations.
	 * @return a new {@link IFilter} object (with its parameters loaded) for the given
	 * configuration identifier, or null if the object could not be created.
	 */
	public static IFilter createFilter(String configId) {
		IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(fcMapper, true, true);
		return fcMapper.createFilter(configId);
	}
	
	/**
	 * Creates an instance of the filter for a given configuration identifier
	 * and loads its corresponding parameters.
	 * @param filterClass class of the filter.
	 * @param configId the filter configuration identifier. Can be either one of Okapi 
	 * default filter configurations or one of the built-in configurations defined in
	 * the filter class.
	 * @return a new {@link IFilter} object (with its parameters loaded) for the given
	 * configuration identifier, or null if the object could not be created.
	 */
	public static IFilter createFilter(Class<? extends IFilter> filterClass, String configId) {
		IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(fcMapper, true, true);
		fcMapper.addConfigurations(filterClass.getName());
		return fcMapper.createFilter(configId);
	}
	
	/**
	 * Creates an instance of the filter for a given configuration identifier
	 * and loads its corresponding parameters. This method accepts a list of the
	 * URLs of fprm files defining custom configurations, and can be used to create
	 * a filter and configure its sub-filters in one call. 
	 * @param configId the filter configuration identifier. Can be either one of Okapi 
	 * default filter configurations or one of the custom configurations defined in
	 * the fprm files.
	 * @param customConfigs a list of the URLs of fprm files defining custom configurations.
	 * Every file name denote by the URLs should be the configuration identifier and have 
	 * the .fprm extension.
	 * @return a new {@link IFilter} object (with its parameters loaded) for the given
	 * configuration identifier, or null if the object could not be created.
	 */
	public static IFilter createFilter(String configId, URL... customConfigs) {
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(fcMapper, true, true);
		
		for (URL customConfig : customConfigs) {
			addCustomConfig(fcMapper, customConfig);			
		}
		
		IFilter filter = fcMapper.createFilter(configId);
		filter.setFilterConfigurationMapper(fcMapper);
		return filter;
	}
	
	/**
	 * Adds to a given {@link FilterConfigurationMapper} object the custom configuration 
	 * defined in the fprm file denoted by a given URL. 
	 * @param fcMapper the given {@link FilterConfigurationMapper}.
	 * @param customConfig the URL of a fprm file defining the custom configuration
	 * the filter should be loaded from.
	 * @return the configuration identifier or null if the configuration was not added.
	 */
	public static String addCustomConfig(FilterConfigurationMapper fcMapper, 
			URL customConfig) {
		String configId = null;
		try {
			String path = customConfig.toURI().getPath();
			String root = Util.getDirectoryName(path) + File.separator;
			configId = Util.getFilename(path, false);
			fcMapper.setCustomConfigurationsDirectory(root);
			fcMapper.addCustomConfiguration(configId);
			fcMapper.updateCustomConfigurations();
		} catch (URISyntaxException e) {
			throw new OkapiIOException(e);
		}
		return configId; 
	}
	
	/**
	 * Creates an instance of the filter for a given URL of a fprm file defining a
	 * custom configuration. The file name should be the configuration identifier 
	 * and have the .fprm extension.
	 * @param customConfig the URL of a fprm file defining the custom configuration
	 * the filter should be loaded from.
	 * @return a new {@link IFilter} object (with its parameters loaded) for the given
	 * configuration identifier, or null if the object could not be created.
	 */
	public static IFilter createFilter(URL customConfig) {
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(fcMapper, true, true);
		return fcMapper.createFilter(addCustomConfig(fcMapper, customConfig));
	}

}
