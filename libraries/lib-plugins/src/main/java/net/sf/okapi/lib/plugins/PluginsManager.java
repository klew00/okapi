/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.lib.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import net.sf.okapi.common.DefaultFilenameFilter;
import net.sf.okapi.common.NonPluggable;
import net.sf.okapi.common.exceptions.OkapiFilterCreationException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.pipeline.IPipelineStep;

/**
 * Provides a way to discover and list plug-ins for a given location or file.   
 */
public class PluginsManager {

	public static final int PLUGINTYPE_IFILTER = 0;
	public static final int PLUGINTYPE_IPIPELINESTEP = 1;
	
	private ArrayList<URL> urls;
	private List<PluginItem> plugins;
	private URLClassLoader loader;
	
	/**
	 * Initializes this object with a given type of plug-ins.
	 * @param input the directory or file to inspect.
	 */
	public void reset (File input) {
		urls = new ArrayList<URL>();
		plugins = new ArrayList<PluginItem>();
		loader = null;

		// Inspect either all the .jar files if input is a directory
		if ( input.isDirectory() ) {
			File[] files = input.listFiles(new DefaultFilenameFilter(".jar"));
			for ( File file : files ) {
				inspectFile(file);
			}
		}
		else { // Or inspect the given file itself
			inspectFile(input);
		}

		if ( urls.size() > 0 ) {
			URL[] tmp = new URL[urls.size()];
			for ( int i=0; i<urls.size(); i++ ) {
				tmp[i] = urls.get(i);
			}
			loader = new URLClassLoader(tmp);
		}
	}
	
	/**
	 * Gets the list of all available plug-ins of a given type
	 * currently available in this manager.
	 * The method {@link #reset(File)} must be called once before
	 * calling this method.
	 * @param type the tyep of plug-ins to list.
	 * @return the list of available plug-ins for the given type.
	 */
	public List<String> getList (int type) {
		ArrayList<String> list = new ArrayList<String>();
		for ( PluginItem item : plugins ) {
			if ( item.type == type ) list.add(item.className);
		}
		return list;
	}
	
	/**
	 * Creates a IFilter object for the given class.
	 * @param className the class to instantiate, it must be listed
	 * in this manager.
	 * @return the instantiated filter.
	 */
	public IFilter createIFilter (String className) {
		IFilter filter;
		try {
			filter = (IFilter)Class.forName(className, true, loader).newInstance();
			return filter;
		}
		catch ( InstantiationException e ) {
			throw new OkapiFilterCreationException(String.format(
				"Error creating filter for '%s'", className), e);
		}
		catch ( IllegalAccessException e ) {
			throw new OkapiFilterCreationException(String.format(
				"Error creating filter for '%s'", className), e);
		}
		catch ( ClassNotFoundException e ) {
			throw new OkapiFilterCreationException(String.format(
				"Error creating filter for '%s'", className), e);
		}
	}
	
	/**
	 * Creates a IPipelineStep object for the given class.
	 * @param className the class to instantiate, it must be listed
	 * in this manager.
	 * @return the instantiated step.
	 */
	public IPipelineStep createIPipelineStep (String className) {
		IPipelineStep step;
		try {
			step = (IPipelineStep)Class.forName(className, true, loader).newInstance();
			return step;
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException(String.format(
				"Error creating step for '%s'", className), e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(String.format(
				"Error creating step for '%s'", className), e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(String.format(
				"Error creating step for '%s'", className), e);
		}
	}
	
	private void inspectFile (File file) {
		try {
			// Create a temporary class loader
			URL[] tmpUrls = new URL[1]; 
			URL url = file.toURI().toURL();
			tmpUrls[0] = url;
			URLClassLoader loader = URLClassLoader.newInstance(tmpUrls);
		
			// Introspect the classes
			JarInputStream jarFile = new JarInputStream(new FileInputStream(file));
			JarEntry entry;
			while ( true ) {
				if ( (entry = jarFile.getNextJarEntry()) == null ) break;
				String name = entry.getName();
				if ( name.endsWith(".class") ) {
					name = name.substring(0, name.length()-6).replace('/', '.');
					try {
						Class<?> cls = Class.forName(name, false, loader);
						// Skip interfaces
						if ( cls.isInterface() ) continue;
						// Skip abstract
						if ( Modifier.isAbstract(cls.getModifiers()) ) continue;
						// Skip classes that should not be use directly
						if ( cls.getAnnotation(NonPluggable.class) != null ) continue;
						// Check class type
						if ( IFilter.class.isAssignableFrom(cls) ) {
							if ( !urls.contains(url) ) urls.add(url);
							plugins.add(new PluginItem(PLUGINTYPE_IFILTER, name));
						}
						else if ( IPipelineStep.class.isAssignableFrom(cls) ) {
							if ( !urls.contains(url) ) urls.add(url);
							plugins.add(new PluginItem(PLUGINTYPE_IPIPELINESTEP, name));
						}
					}
					catch ( Throwable e ) {
						// If the class cannot be create for some reason, we skip it silently
					}
				}
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException("IO error when inspecting a file for plugins.", e);
		}
	}

}
