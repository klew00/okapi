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

package net.sf.okapi.common.filters;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.exceptions.OkapiFilterCreationException;

/**
 * Default implementation of the {@link IFilterConfigurationMapper} interface.
 */
public class FilterConfigurationMapper implements IFilterConfigurationMapper {

	private LinkedHashMap<String, FilterConfiguration> configMap;
	private LinkedHashMap<String, String> editorMap;
	
	/**
	 * Creates a new FilterConfigurationMapper object with no mappings.
	 */
	public FilterConfigurationMapper () {
		configMap = new LinkedHashMap<String, FilterConfiguration>();
		editorMap = new LinkedHashMap<String, String>();
	}

	public void addConfigurations (String filterClass) {
		// Instantiate the filter to get the available configurations
		IFilter filter = null;
		try {
			filter = (IFilter)Class.forName(filterClass).newInstance();
		}
		catch ( InstantiationException e ) {
			throw new OkapiFilterCreationException("Cannot instantiate filter.", e);
		}
		catch ( IllegalAccessException e ) {
			throw new OkapiFilterCreationException("Cannot instantiate filter.", e);
		}
		catch ( ClassNotFoundException e ) {
			throw new OkapiFilterCreationException("Cannot instantiate filter.", e);
		}
		// Get the available configurations for thsi filter
		List<FilterConfiguration> list = filter.getConfigurations();
		// Add the configurations to the mapper
		for ( FilterConfiguration config : list ) {
			addConfiguration(config);
		}
	}


	public void addConfiguration (FilterConfiguration config)
	{
		configMap.put(config.configId, config);
	}

	public IFilter createFilter (String configId) {
		return createFilter(configId, null);
	}
	
	public IFilter createFilter (String configId,
		IFilter existingFilter)
	{
		// Get the configuration object for the given configId
		FilterConfiguration fc = configMap.get(configId);
		if ( fc == null ) return null;
		
		// Instantiate the filter (or re-use one)
		IFilter filter = instantiateFilter(fc, existingFilter);
		
		// Always load the parameters (if there are parameters)
		if ( fc.parameters != null ) {
			IParameters params = filter.getParameters();
			if ( params == null ) {
				throw new RuntimeException(String.format(
					"Cannot create default parameters for '%s'.", fc.configId));
			}
			if ( fc.custom ) {
				params = getCustomParameters(fc, filter);
			}
			else {
				// Note that we cannot assume the parameters are the same
				// if we re-used an existing filter, as we cannot compare the 
				// configuration identifiers
				URL url = filter.getClass().getResource(fc.parameters);
				try {
					params.load(url.toURI(), false);
				}
				catch ( URISyntaxException e ) {
					throw new RuntimeException(String.format(
						"URI syntax error '%s'.", url.getPath()));
				}
			}
		}
		
		return filter;
	}

	public IParametersEditor createParametersEditor (String configId) {
		return createParametersEditor(configId, null);
	}
	
	public IParametersEditor createParametersEditor (String configId,
		IFilter existingFilter)
	{
		FilterConfiguration fc = configMap.get(configId);
		if ( fc == null ) return null;
		if ( fc.parameters == null ) return null;

		IFilter filter = instantiateFilter(fc, existingFilter);

		// Get the default parameters object
		IParameters params = filter.getParameters();
		if ( params == null ) {
			return null; // This filter does not have parameters
		}
		
		// Lookup the editor class based on the parameters class
		String editorClass = editorMap.get(params.getClass().getName());
		if ( editorClass == null ) return null;
		
		// Else: instantiate the editor
		IParametersEditor editor = null;
		try {
			editor = (IParametersEditor)Class.forName(editorClass).newInstance();
		}
		catch ( InstantiationException e ) {
			throw new OkapiFilterCreationException(
				String.format("Cannot instantiate the editor '%s'", editorClass), e);
		}
		catch ( IllegalAccessException e ) {
			throw new OkapiFilterCreationException(
				String.format("Cannot instantiate the editor '%s'", editorClass), e);
		}
		catch ( ClassNotFoundException e ) {
			throw new OkapiFilterCreationException(
				String.format("Cannot instantiate the editor '%s'", editorClass), e);
		}
		return editor;
	}

	public FilterConfiguration getConfiguration (String configId) {
		return configMap.get(configId);
	}

	public List<FilterConfiguration> getConfigurations (String mimeType) {
		ArrayList<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		for ( FilterConfiguration config : configMap.values() ) {
			if ( config.mimeType != null ) {
				if ( config.equals(mimeType) ) {
					list.add(config);
				}
			}
		}
		return list;
	}

	public FilterConfiguration getDefaultConfiguration (String mimeType) {
		for ( FilterConfiguration config : configMap.values() ) {
			if ( config.mimeType != null ) {
				if ( config.mimeType.equals(mimeType) ) {
					return config;
				}
			}
		}
		return null;
	}

	public void removeConfiguration (String configId) {
		configMap.remove(configId);
	}

	public IParameters getCustomParameters (FilterConfiguration config) {
		return getCustomParameters(config, null);
	}

	/**
	 * Gets the parameters for a given custom filter configuration. This
	 * default implementation gets the custom data from a file located
	 * in the current directory at the time the method is called. 
	 */
	public IParameters getCustomParameters (FilterConfiguration config,
		IFilter existingFilter)
	{
		// Instantiate a filter (or re-use one)
		IFilter filter = instantiateFilter(config, existingFilter);

		// Get the default parameters object
		IParameters params = filter.getParameters();
		if ( params == null ) {
			return null; // This filter does not have parameters
		}

		// Load the provided parameter file
		// In this implementation we assume it is in the current directory
		File file = new File(config.parameters);
		params.load(file.toURI(), false);
		return params;
	}

	public void clearConfigurations (boolean customOnly) {
		if ( customOnly ) {
			for ( FilterConfiguration fc : configMap.values() ) {
				if ( fc.custom ) {
					configMap.remove(fc.configId);
				}
			}
		}
		else {
			configMap.clear();
		}
	}

	public void addEditor (String editorClass,
		String parametersClass)
	{
		editorMap.put(parametersClass, editorClass);
	}

	public void clearEditors () {
		editorMap.clear();
	}

	public void removeEditor (String editorClass) {
		String found = null;
		for ( String key : editorMap.keySet() ) {
			if ( editorMap.get(key).equals(editorClass) ) {
				found = key;
				break;
			}
		}
		if ( found != null ) {
			editorMap.remove(found);
		}
	}

	/**
	 * Instantiate a filter from a given configuration, trying to re-use an existing one.
	 * @param config the configuration corresponding to the filter to load.
	 * @param existingFilter an optional existing filter we can try to reuse.
	 * @return
	 */
	protected IFilter instantiateFilter (FilterConfiguration config,
		IFilter existingFilter)
	{
		IFilter filter = null;
		if ( existingFilter != null ) {
			if ( config.filterClass.equals(existingFilter.getClass().getName()) ) {
				filter = existingFilter;
			}
		}
		if ( filter == null ) {
			try {
				filter = (IFilter)Class.forName(config.filterClass).newInstance();
			}
			catch ( InstantiationException e ) {
				throw new OkapiFilterCreationException(
					String.format("Cannot instantiate the filter configuration '%s'", config.configId), e);
			}
			catch ( IllegalAccessException e ) {
				throw new OkapiFilterCreationException(
					String.format("Cannot instantiate the filter configuration '%s'", config.configId), e);
			}
			catch ( ClassNotFoundException e ) {
				throw new OkapiFilterCreationException(
					String.format("Cannot instantiate the filter configuration '%s'", config.configId), e);
			}
		}
		return filter;
	}

}
