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

package net.sf.okapi.applications.rainbow.lib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.okapi.common.DefaultFilenameFilter;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiFilterCreationException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;

public class FilterConfigMapper extends FilterConfigurationMapper {

	private String paramsFolder;
	private ArrayList<FilterInfo> filters;
	private static final Logger LOGGER = Logger.getLogger(FilterConfigurationMapper.class.getName());
	
	/**
	 * Splits a configuration identifier into a filter and 
	 * @param configId the configuration identifier to split.
	 * @return an array of two strings: 0=filter (e.g. "okf_xml", 1=parameter info (or null).
	 */
	static public String[] splitFilterFromConfiguration (String configId) {
		String[] res = new String[2];
		// Get the filter
		int n  = configId.indexOf(FilterSettingsMarkers.PARAMETERSSEP);
		if ( n == -1 ) {
			// Try '-' then
			n = configId.indexOf('-');
			if ( n == -1 ) {
				// Try '_'
				n = configId.indexOf('_');
				if ( n == -1 ) {
					res[0] = configId;
					return res; // The filter is the configID (default case)
				}
				else { // Check for "okf_" case
					if ( configId.substring(0, n).equals("okf") ) {
						n = configId.indexOf('_', n+1);
						if ( n == -1 ) {
							res[0] = configId;
							return res; // The filter is the configID (default case) 
						}
					}
				}
			}
		}
		res[0] = configId.substring(0, n);
		res[1] = configId.substring(n+1);
		return res;
	}
	
	/**
	 * Loads the list of accessible filters and the list filters parameters editors.
	 * The lists are stored in an XML file of the following format:
	 * <okapiFilters>
	 *  <filter id="okf_regex"
	 *   inputFilterClass="net.sf.okapi.filters.regex.RegexFilter"
	 *  >Regular Expressions</filter>
	 *  <parametersEditor
	 *   parametersClass="net.sf.okapi.filters.regex.Parameters"
	 *   editorClass="net.sf.okapi.filters.ui.regex.Editor"
	 *  />
	 * </okapiFilters>
	 * @param p_sPath Full path of the list file to load.
	 */
	public void loadList (String p_sPath) {
		try {
			// Try to open the file
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document doc = Fact.newDocumentBuilder().parse(new File(p_sPath));
			
			// Clear all the data
			clearFilters();
			clearEditors();
			clearConfigurations(false);
			FilterInfo info;
			String filterId;

			NodeList list = doc.getElementsByTagName("filter");
			for ( int i=0; i<list.getLength(); i++ ) {
				Node node = list.item(i).getAttributes().getNamedItem("id");
				if ( node == null ) throw new RuntimeException("An attribute 'id' is missing.");
				filterId = Util.getTextContent(node);
				info = new FilterInfo();
				
				node = list.item(i).getAttributes().getNamedItem("inputFilterClass");
				if ( node == null ) throw new RuntimeException("An attribute 'inputFilterClass' is missing.");
				info.filterClass = Util.getTextContent(node);
				
				node = list.item(i).getAttributes().getNamedItem("name");
				if ( node != null ) info.name = Util.getTextContent(node);
				else info.name = filterId;

				info.description = Util.getTextContent(list.item(i));

				filters.add(info);
				// Add the default configurations
				try {
					addConfigurations(info.filterClass);
				}
				catch ( OkapiFilterCreationException e ) {
					LOGGER.warning(e.getMessage());
				}
			}
			
			list = doc.getElementsByTagName("parametersEditor");
			for ( int i=0; i<list.getLength(); i++ ) {
				Node nodeP = list.item(i).getAttributes().getNamedItem("parametersClass");
				if ( nodeP == null ) throw new RuntimeException("An attribute 'parametersClass' is missing.");
				Node nodeE = list.item(i).getAttributes().getNamedItem("editorClass");
				if ( nodeE == null ) throw new RuntimeException("An attribute 'editorClass' is missing.");

				addEditor(Util.getTextContent(nodeE),  Util.getTextContent(nodeP));
			}
			
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
		catch ( SAXException e ) {
			throw new RuntimeException(e);
		}
	}

	private void clearFilters () {
		filters = new ArrayList<FilterInfo>();
	}

	public void setParametersFolder (String newFolder) {
		paramsFolder = newFolder;
		if ( !paramsFolder.endsWith(File.separator) ) {
			paramsFolder += File.separator;
		}
	}
	
	/**
	 * Updates the custom configurations for this mapper. This should
	 * be called if the parameters folder has changed.
	 */
	public void updateCustomConfigurations () {
		File dir = new File(paramsFolder);
		String aRes[] = dir.list(new DefaultFilenameFilter(FilterSettingsMarkers.PARAMETERS_FILEEXT));
		clearConfigurations(true); // Only custom configurations
		if ( aRes == null ) return;
		
		FilterConfiguration fc;
		IFilter filter = null;
		for ( int i=0; i<aRes.length; i++ ) {
			fc = new FilterConfiguration();
			fc.custom = true;
			fc.configId = Util.getFilename(aRes[i], false);
			
			// Get the filter
			String[] res = splitFilterFromConfiguration(fc.configId);
			if ( res == null ) { // Cannot found the filter in the ID
				//TODO: Maybe a warning?
				continue;
			}
			// Create the filter (this assumes the base-name is the default config ID)
			filter = createFilter(res[0], filter);
			if ( filter == null ) continue;
			
			// Set the data
			fc.parametersLocation = fc.configId + FilterSettingsMarkers.PARAMETERS_FILEEXT;
			fc.filterClass = filter.getClass().getName();
			fc.mimeType = filter.getMimeType();
			fc.description = "Configuration for "+fc.configId; // Temporary
			fc.name = fc.configId; // Temporary
			addConfiguration(fc);
		}
	}

	public ArrayList<FilterInfo> getFilters () {
		return filters;
	}

	public FilterInfo getFilterInfo (String filterClass) {
		for ( FilterInfo info : filters ) {
			if ( info.filterClass.equals(filterClass) ) return info;
		}
		return null;
	}

	@Override
	public void deleteCustomParameters (FilterConfiguration config) {
		// In this implementation the file is stored in a given directory
		File file = new File(paramsFolder + config.parametersLocation);
		file.delete();
	}

	@Override
	public void saveCustomParameters (FilterConfiguration config,
		IParameters params)
	{
		// In this implementation the file is stored in a given directory
		File file = new File(paramsFolder + config.parametersLocation);
		params.save(file.getAbsolutePath());
	}

	@Override
	public FilterConfiguration createCustomConfiguration (FilterConfiguration baseConfig) {
		// Create the new configuration and set its members as a copy of the base
		FilterConfiguration newConfig = new FilterConfiguration();
		String[] res = splitFilterFromConfiguration(baseConfig.configId);
		if ( res == null ) { // Cannot create the configuration because of ID 
			return null;
		}
		
		newConfig.custom = true;
		if ( res[1] == null ) {
			newConfig.configId = String.format("%s%ccopy-of-default",
				res[0], FilterSettingsMarkers.PARAMETERSSEP);
		}
		else {
			newConfig.configId = String.format("%s%ccopy-of-%s",
				res[0], FilterSettingsMarkers.PARAMETERSSEP, res[1]);
		}
		newConfig.name = String.format(newConfig.configId);
		newConfig.description = "";
		newConfig.filterClass = baseConfig.filterClass;
		newConfig.mimeType = baseConfig.mimeType;
		newConfig.parametersLocation = newConfig.configId + FilterSettingsMarkers.PARAMETERS_FILEEXT;
		
		// Instantiate a filter and set the new parameters based on the base ones
		IFilter filter = instantiateFilter(baseConfig, null);
		IParameters baseParams = getParameters(baseConfig, filter);
		IParameters newParams = filter.getParameters();
		newParams.fromString(baseParams.toString());
		// Make sure to reset the path, the save function should set it
		newParams.setPath(null);
		return newConfig;
	}

	@Override
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
		// In this implementation the file is stored in a given directory
		File file = new File(paramsFolder + config.parametersLocation);
		params.load(file.toURI(), false);
		return params;
	}

}
