package net.sf.okapi.common.filters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.sf.okapi.common.exceptions.OkapiFilterCreationException;
import net.sf.okapi.common.exceptions.OkapiIOException;

public class FilterHarvester {

	static public void harvestFilterConfigurations (String jarPath,
		IFilterConfigurationMapper mapper)
	{
		class JarFileLoader extends URLClassLoader {
		    public JarFileLoader (URL[] urls) {
		        super (urls);
		    }
		    public void addFile (String path) throws MalformedURLException {
		        String urlPath = "jar:file://" + path + "!/";
		        addURL (new URL (urlPath));
		    }
		}

		try {
			JarFile jar;
			jar = new JarFile(jarPath);
			Manifest manifest;
			manifest = jar.getManifest();
			if ( manifest == null ) return;
			
			// Try to get the Okapi filter entry in the manifest
			Attributes attrs = manifest.getMainAttributes();
			String filterClass = attrs.getValue("Okapi-FilterClass");
			if ( filterClass == null ) return;

			URL urls [] = {};
            JarFileLoader cl = new JarFileLoader(urls);
            cl.addFile(jarPath);
            cl.loadClass(filterClass);
			
			// Instantiate the filter to get the available configurations
			IFilter filter = (IFilter)Class.forName(filterClass).newInstance();
			Map<String, FilterConfiguration> map = filter.getConfigurations();
			
			// Add the configurations to the mapper
			for ( String mimeType : map.keySet() ) {
				mapper.addConfiguration(map.get(mimeType), mimeType);
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error reading the JAR file.", e);
		}
		catch ( InstantiationException e ) {
			throw new OkapiFilterCreationException("Error creating the filter object.", e);
		}
		catch ( IllegalAccessException e ) {
			throw new OkapiFilterCreationException("Error creating the filter object.", e);
		}
		catch (ClassNotFoundException e) {
			throw new OkapiFilterCreationException("Error creating the filter object.", e);
		}
	}

}
