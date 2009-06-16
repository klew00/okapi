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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;

public class FilterConfigMapper extends FilterConfigurationMapper {

	private String paramsFolder;
	
	/**
	 * Loads the list of accessible filters.
	 * The list is stored in an XML file of the following format:
	 * <okapiFilters>
	 *  <filter id="okf_regex"
	 *   inputFilterClass="net.sf.okapi.filters.regex.Filter"
	 *   editorClass="net.sf.okapi.filters.ui.regex.Editor"
	 *  >Regular Expressions</filter>
	 * </okapiFilters>
	 * @param p_sPath Full path of the list file to load.
	 */
	public void loadList (String p_sPath) {
		try {
			// Try to open the file
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document doc = Fact.newDocumentBuilder().parse(new File(p_sPath));
			NodeList list = doc.getElementsByTagName("filter");
			
			// Clear all the data 
			clearEditors();
			clearConfigurations(false);
			
			FilterAccessItem item;
			for ( int i=0; i<list.getLength(); i++ ) {
				Node node = list.item(i).getAttributes().getNamedItem("id");
				if ( node == null ) throw new RuntimeException("The attribute 'id' is missing.");
				item = new FilterAccessItem();
				item.id = Util.getTextContent(node);

				node = list.item(i).getAttributes().getNamedItem("inputFilterClass");
				if ( node == null ) throw new RuntimeException("The attribute 'inputFilterClass' is missing.");
				item.inputFilterClass = Util.getTextContent(node);
				
				node = list.item(i).getAttributes().getNamedItem("editorClass");
				if ( node != null ) item.editorClass = Util.getTextContent(node);

				node = list.item(i).getAttributes().getNamedItem("parametersClass");
				if ( node != null ) item.parametersClass = Util.getTextContent(node);

				node = list.item(i).getAttributes().getNamedItem("name");
				if ( node != null ) item.name = Util.getTextContent(node);
				else item.name = item.id;

				item.description = Util.getTextContent(list.item(i));

				// Add the default configurations
				addConfigurations(item.inputFilterClass);
				// Add the editor, if possible
				if (( item.editorClass != null ) && ( item.parametersClass != null )) {
					addEditor(item.editorClass, item.parametersClass);
				}
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
			String baseConfigId = fc.configId;
			int n = baseConfigId.indexOf(FilterSettingsMarkers.PARAMETERSSEP);
			if ( n == -1 ) continue;
			baseConfigId = baseConfigId.substring(0, n);
			filter = this.createFilter(baseConfigId, filter);
			if ( filter == null ) continue;
			
			// Set the data
			fc.parameters = fc.configId + FilterSettingsMarkers.PARAMETERS_FILEEXT;
			fc.filterClass = filter.getClass().getName();
			fc.mimeType = filter.getMimeType();
			fc.description = "Configuration for "+fc.configId; // Temporary
			fc.name = fc.configId; // Temporary
			addConfiguration(fc);
		}
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
		// In this implementation we assume it is in the current directory
		File file = new File(paramsFolder + config.parameters);
		params.load(file.toURI(), false);
		return params;
	}

}
