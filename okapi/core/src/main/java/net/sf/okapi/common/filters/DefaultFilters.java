package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Helper object to load a default list of filters and their editor information.
 * The list is in the DefaultFilters.properties file.
 */
public class DefaultFilters {

	private static final String BUNDLE_NAME = "net.sf.okapi.common.filters.DefaultFilters";

	/**
	 * Add the default mappings provided in the DefaultFilters.properties file.
	 * @param fcMapper the mapper where to add the mapping.
	 * @param reset true to clear all filters, editors and dec descriptions in the mapper
	 * before setting the new ones.
	 * @param addConfigurations true to add the filters configurations, false to add 
	 * only the parameters editors and UI descriptions.
	 */
	public static void setMappings (IFilterConfigurationMapper fcMapper,
		boolean reset,
		boolean addConfigurations)
	{
		// Create the bundle and load it
		ResourceBundle res = ResourceBundle.getBundle(BUNDLE_NAME);
		Enumeration<String> keys = res.getKeys();
		ArrayList<String> list = Collections.list(keys);
		
		if ( reset ) {
			fcMapper.clearConfigurations(false);
			fcMapper.clearDescriptionProviders();
			fcMapper.clearEditors();
		}
		
		// Go through the keys
		for ( String key : list ) {
			// Skip non-filterClass entries
			if ( !key.startsWith("filterClass") ) continue;
			
			int n = key.indexOf('_');
			String suffix = key.substring(n);
			String value = res.getString(key);
				
			// Add the configurations for the filter
			if ( addConfigurations ) {
				fcMapper.addConfigurations(value);
			}

			String key2 = "parametersClass"+suffix;
			if ( list.contains(key2) ) {
				String paramsClass = res.getString(key2);
				// Add editor if available
				String key3 = "parametersEditorClass"+suffix;
				if ( list.contains(key3) ) {
					value = res.getString(key3);
					fcMapper.addEditor(value, paramsClass);
				}
				else { // Add editor descriptor if available
					key3 = "editorDescriptionProvider"+suffix;
					if ( list.contains(key3) ) {
						value = res.getString(key3);
						fcMapper.addDescriptionProvider(value, paramsClass);
					}
				}
				
			}
		} // End of for
		
	}

}
