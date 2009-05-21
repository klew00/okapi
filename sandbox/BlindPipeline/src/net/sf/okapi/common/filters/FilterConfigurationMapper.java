package net.sf.okapi.common.filters;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.exceptions.OkapiFilterCreationException;

public class FilterConfigurationMapper implements IFilterConfigurationMapper {

	class MimeConfig {
		String mimeType;
		String configId;
		MimeConfig (String mimeType, String configId) {
			this.mimeType = mimeType;
			this.configId = configId;
		}
	}
	
	private LinkedHashMap<String, FilterConfiguration> configMap;
	private List<MimeConfig> mimeList;
	private LinkedHashMap<String, String> editorMap;
	
	public FilterConfigurationMapper () {
		configMap = new LinkedHashMap<String, FilterConfiguration>();
		mimeList = new ArrayList<MimeConfig>();
		editorMap = new LinkedHashMap<String, String>();
		
		// Temporary hard-coded list for test
		FilterConfiguration cfg = new FilterConfiguration();
		cfg.configId = "okapi.xml";
		cfg.name = "Generic XML";
		cfg.description = "Default XML documents.";
		cfg.filterClass = "net.sf.okapi.filters.xml.XMLFilter";
		cfg.parameters = null;
		addConfiguration(cfg, MimeTypeMapper.XML_MIME_TYPE);
		
		cfg = new FilterConfiguration();
		cfg.configId = "okapi.properties";
		cfg.name = "Properties";
		cfg.description = "Default properties files.";
		cfg.filterClass = "net.sf.okapi.filters.properties.PropertiesFilter";
		cfg.parameters = null;
		addConfiguration(cfg, MimeTypeMapper.PROPERTIES_MIME_TYPE);
	}
	
	public void addConfiguration (FilterConfiguration config, String mimeType) {
		configMap.put(config.configId, config);
		mimeList.add(new MimeConfig(mimeType, config.configId));
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
				params.load(url.getPath(), false);
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
		for ( MimeConfig mc : mimeList ) {
			if ( mc.mimeType.equals(mimeType) ) {
				list.add(configMap.get(mc.configId));
			}
		}
		return list;
	}

	public FilterConfiguration getDefaultConfiguration (String mimeType) {
		for ( int i=0; i<mimeList.size(); i++ ) {
			if ( mimeList.get(i).mimeType.equals(mimeType) ) {
				return configMap.get(mimeList.get(i).configId);
			}
		}
		return null;
	}

	public void removeConfiguration (String configId) {
		configMap.remove(configId);
		int found = -1;
		for ( int i=0; i<mimeList.size(); i++ ) {
			if ( mimeList.get(i).configId.equals(configId) ) {
				found = i;
				break;
			}
		}
		if ( found > -1 ) mimeList.remove(found);
	}

	public IParameters getCustomParameters (FilterConfiguration config) {
		return getCustomParameters(config, null);
	}

	/* This default implementation of IFilterConfiguration gets the custom data
	 * from the current directory at the time the method is called.  
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
		params.load(file.getAbsolutePath(), false);
		return params;
	}

	public void clearConfigurations() {
		configMap.clear();
		mimeList.clear();
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

	private IFilter instantiateFilter (FilterConfiguration config,
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
