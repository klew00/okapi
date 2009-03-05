/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.ho.yaml.Yaml;

public class YamlConfigurationReader  {	
	private boolean preserveWhitespace;	
	@SuppressWarnings("unchecked")
	private Map config;
	
	public boolean isPreserveWhitespace() {
		return preserveWhitespace;
	}

	public void setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
	}

	@SuppressWarnings("unchecked")
	public YamlConfigurationReader(URL configurationPathAsResource) {		
		try {
			config = (Map)Yaml.load(new InputStreamReader(configurationPathAsResource.openStream()));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {			
			throw new RuntimeException(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public YamlConfigurationReader(File configurationFile) {				
		try {
			config = (Map)Yaml.load(new FileReader(configurationFile));
		} catch (FileNotFoundException e) {
			// TODO Create custom exception
			throw new RuntimeException(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public YamlConfigurationReader(String configurationScript) {
		config = (Map)Yaml.load(configurationScript);			
	}
	
	@Override
	public String toString() {	
		return Yaml.dump(config);
	}
	
	@SuppressWarnings("unchecked")
	public Map getRule(String ruleName) {
		return (Map)config.get(ruleName);
	}
	
	@SuppressWarnings("unchecked")
	public Object getProperty(String property) {
		return config.get(property);
	}
	
	@SuppressWarnings("unchecked")
	public void addProperty(String property, boolean value) {
		config.put(property, value);
	}
	
	@SuppressWarnings("unchecked")
	public void addProperty(String property, String value) {
		config.put(property, value);
	}
	
	@SuppressWarnings("unchecked")
	public void addRule(String ruleName, Map rule) {
		config.putAll(rule);
	}
	
	public void clearRules() {
		config.clear();
	}
}
