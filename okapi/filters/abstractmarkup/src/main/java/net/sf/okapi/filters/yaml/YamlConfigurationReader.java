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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.yaml.snakeyaml.Yaml;

public class YamlConfigurationReader  {	
	private static final String REGEX_META_CHARS_REGEX = "[\\(\\[\\{\\^\\$\\|\\]\\}\\)\\?\\*\\+]+";
	private static final Pattern REGEX_META_CHARS_PATTERN = Pattern.compile(REGEX_META_CHARS_REGEX);
	
	private boolean preserveWhitespace;
	private Yaml yaml;
	@SuppressWarnings("unchecked")
	private Map config;
	private Map<String, Object> regexRules;
	private Map<String, Pattern> compiledRegexRules;
	
	public boolean isPreserveWhitespace() {
		return preserveWhitespace;
	}

	public void setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
	}

	/**
	 * Default Tagged Configuration
	 */
	@SuppressWarnings("unchecked")
	public YamlConfigurationReader() {
		yaml = new Yaml();
		config = (Map)yaml.load("collapse_whitespace: false\nassumeWellformed: true");
		regexRules = new HashMap<String, Object>();
		findRegexRules();
		compileRegexRules();
	}
	
	@SuppressWarnings("unchecked")
	public YamlConfigurationReader(URL configurationPathAsResource) {		
		try {
			yaml = new Yaml();
			config = (Map)yaml.load(new InputStreamReader(configurationPathAsResource.openStream()));
			regexRules = new HashMap<String, Object>();
			findRegexRules();
			compileRegexRules();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {			
			throw new RuntimeException(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public YamlConfigurationReader(File configurationFile) {				
		try {
			yaml = new Yaml();
			config = (Map)yaml.load(new FileReader(configurationFile));
			regexRules = new HashMap<String, Object>();
			findRegexRules();
			compileRegexRules();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public YamlConfigurationReader(String configurationScript) {
		yaml = new Yaml();
		config = (Map)yaml.load(configurationScript);
		regexRules = new HashMap<String, Object>();
		findRegexRules();
		compileRegexRules();
	}
	
	@Override
	public String toString() {	
		return yaml.dump(config);
	}
	
	@SuppressWarnings("unchecked")
	public Map getRule(String ruleName) {		
		return (Map)config.get(ruleName);
	}
	
	@SuppressWarnings("unchecked")
	public Map getCompiledRegexRules() {		
		return compiledRegexRules;
	}
	
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
		regexRules.clear();
		compiledRegexRules.clear();
	}
		
	private void findRegexRules() {		
		for (Object r : config.keySet()) {			
			try {
				Matcher m = REGEX_META_CHARS_PATTERN.matcher((String)r);
				if (m.find()) {
					regexRules.put((String)r, config.get(r));
				}
			} catch (PatternSyntaxException e) {
				throw new IllegalConditionalAttributeException(e);
			}
		}		
	}
	
	private void compileRegexRules() {
		if (regexRules.isEmpty()) {
			return;
		}
		
		compiledRegexRules = new HashMap<String, Pattern>();
		for (String r : regexRules.keySet()) {
			Pattern compiledRegex = Pattern.compile(r);
			compiledRegexRules.put(r, compiledRegex);			
		}
	}
}
