package net.sf.okapi.common.filters;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.MimeTypeMapper;

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
	
	public FilterConfigurationMapper () {
		configMap = new LinkedHashMap<String, FilterConfiguration>();
		mimeList = new ArrayList<MimeConfig>();
		
		// Temporary hard-coded list for test
		FilterConfiguration cfg = new FilterConfiguration();
		cfg.configId = "okapi.xml";
		cfg.name = "Generic XML";
		cfg.description = "Default XML documents.";
		cfg.filterClass = "net.sf.okapi.filters.xml.XMLFilter";
		cfg.parameters = null;
		addMapping(cfg, MimeTypeMapper.XML_MIME_TYPE);
		
		cfg = new FilterConfiguration();
		cfg.configId = "okapi.properties";
		cfg.name = "Properties";
		cfg.description = "Default properties files.";
		cfg.filterClass = "net.sf.okapi.filters.properties.PropertiesFilter";
		cfg.parameters = null;
		addMapping(cfg, MimeTypeMapper.PROPERTIES_MIME_TYPE);
	}
	
	public void addMapping (FilterConfiguration config, String mimeType) {
		configMap.put(config.configId, config);
		mimeList.add(new MimeConfig(mimeType, config.configId));
	}

	public IFilter createFilter (String configId) {
		FilterConfiguration fc = configMap.get(configId);
		if ( fc == null ) return null;
		IFilter filter = null;
		try {
			filter = (IFilter)Class.forName(fc.filterClass).newInstance();
		}
		catch ( InstantiationException e ) {
			// throw exception
		}
		catch ( IllegalAccessException e ) {
			// throw exception
		}
		catch ( ClassNotFoundException e ) {
			// throw exception
		}
		if ( fc.parameters != null ) {
			IParameters params = filter.getParameters();
			if ( params == null ) {
				throw new RuntimeException("Cannot read filter parameters for "+fc.configId);
			}
			if ( fc.custom ) {
				String path = getCustomParametersPath(fc);
				if ( path == null ) {
					throw new RuntimeException("Cannot find custom parameters file for "+fc.configId);
				}
				params.load(path, false);
			}
			else {
				URL url = filter.getClass().getResource(fc.parameters);
				params.load(url.getPath(), false);
			}
		}
		return filter;
	}

	public IParametersEditor createParametersEditor (String configId) {
		FilterConfiguration fc = configMap.get(configId);
		if ( fc == null ) return null;
		if ( fc.editorClass == null ) return null;
		IParametersEditor editor = null;
		try {
			editor = (IParametersEditor)Class.forName(fc.editorClass).newInstance();
		}
		catch ( InstantiationException e ) {
			// throw exception
		}
		catch ( IllegalAccessException e ) {
			// throw exception
		}
		catch ( ClassNotFoundException e ) {
			// throw exception
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

	public void removeMapping (String configId) {
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

	/* This default implementation of IFilterConfiguration gets the custom data
	 * from the current directory at the time the method is called.  
	 */
	public String getCustomParametersPath (FilterConfiguration config) {
		File file = new File(config.parameters);
		return file.getAbsolutePath();
	}

	public void clear() {
		configMap.clear();
		mimeList.clear();
	}

}
