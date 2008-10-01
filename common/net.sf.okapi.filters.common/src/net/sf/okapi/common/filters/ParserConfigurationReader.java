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

package net.sf.okapi.common.filters;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ParserConfigurationReader  {	
	private boolean preserveWhitespace;
	private ConfigSlurper configSlurper;
	private ConfigObject config;
	
	public boolean isPreserveWhitespace() {
		return preserveWhitespace;
	}

	public void setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
	}

	public ParserConfigurationReader(String configurationPathAsResource) {
		configSlurper = new ConfigSlurper();
		URL url = ParserConfigurationReader.class.getResource(configurationPathAsResource);
		config = configSlurper.parse(url);		
	}
	
	public ParserConfigurationReader(File configurationFile) {
		configSlurper = new ConfigSlurper();		
		try {
			config = configSlurper.parse(configurationFile.toURL());
		} catch (MalformedURLException e) {
			// TODO Create custom exception
			throw new RuntimeException(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public Map getRule(String ruleName) {
		return (Map)config.get(ruleName);
	}
	
	public void addProperty(String property, boolean value) {
		config.setProperty(property, value);
	}
	
	public void addProperty(String property, String value) {
		config.setProperty(property, value);
	}
	
	@SuppressWarnings("unchecked")
	public void addRule(String ruleName, Map rule) {
		config.putAll(rule);
	}
	
	public void clearRules() {
		config.clear();
	}
}
