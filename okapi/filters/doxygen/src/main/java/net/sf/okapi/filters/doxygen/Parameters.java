/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.doxygen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.okapi.common.BaseParameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.yaml.snakeyaml.Yaml;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Parameters extends BaseParameters
{
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	public static final String DOXYGEN_PARAMETERS = "doxygenConfiguration.yml";
	
	private Yaml yaml;
	private Map<String, Object> config;
	private Map<String, Object> doxygenCommands;
	private Map<String, Object> htmlCommands;
	private IdentityHashMap<Pattern, Object> customCommands;
	private boolean preserveWhitespace = false;

	public Parameters ()
	{	
		yaml = new Yaml();
		reset();
	}

	public void reset()
	{
		config = (Map) yaml.load(DoxygenFilter.class.getResourceAsStream(DOXYGEN_PARAMETERS));
		initialize();
	}
	
	private void initialize() {
		
		doxygenCommands = new HashMap<String, Object>();
		Object doxygen = config.get("doxygen_commands");
		if (doxygen != null) doxygenCommands = (Map<String, Object>) doxygen;
		
		htmlCommands = new HashMap<String, Object>();
		Object html = config.get("html_commands");
		if (html != null) htmlCommands = (Map<String, Object>) html;
		
		Object whitespace = config.get("preserve_whitespace");
		if (whitespace != null) preserveWhitespace = ((Boolean) whitespace).booleanValue();
		
		customCommands = new IdentityHashMap<Pattern, Object>();
		Object custom = config.get("custom_commands");
		if (custom != null)
			for (HashMap<String, Object> c : ((ArrayList<HashMap<String, Object>>) custom))
				try {
					String regex = (String) c.get("pattern");
					customCommands.put(Pattern.compile(regex), c);
				} catch (PatternSyntaxException ex) {
					LOGGER.warn("Regex pattern was invalid: " + ex.getPattern());
				} catch (NullPointerException ex) {
					LOGGER.warn("User-supplied custom regex for the Doxygen filter was null. "
							+ "Make sure to enclose it in double-quotes in the config file.");
				}
	}

	public boolean isDoxygenCommand(String cmd)
	{
		if (cmd.equals("@{") || cmd.equals("@}")) return true;
		
		if (!cmd.startsWith("\\") && !cmd.startsWith("@") && !cmd.startsWith("<")) return false;
		
		if (cmd.startsWith("<")) return htmlCommands.containsKey(clean(cmd));
		
		return doxygenCommands.containsKey(clean(cmd));
	}
	
	private String clean(String cmd)
	{
		if (cmd.equals("@{") || cmd.equals("@}")) return cmd;
		return cmd.replaceAll("[\\\\@<>/]|[\\[\\(\\{].*|\\s.*", "");
	}

	@Override
	public String toString()
	{
		return yaml.dump(config);
	}
	
	public void fromString(String data)
	{
		config = (Map) yaml.load(data);
		initialize();
	}
	
	public DoxygenCommand commandInfo(String rawCommand, Pattern pattern)
	{
		if (customCommands.containsKey(pattern)) {
			Map<String, Object> data = null;
			data = (Map<String, Object>) customCommands.get(pattern);
			return new DoxygenCommand(data, rawCommand, rawCommand, this);
		}
		
		if (!isDoxygenCommand(rawCommand)) return null;
		
		String cmdName = clean(rawCommand);
		
		Map<String, Object> data = null;
		
		if (rawCommand.startsWith("<")) data = (Map<String, Object>) htmlCommands.get(cmdName);
		else data = (Map<String, Object>) doxygenCommands.get(cmdName);
		
		return new DoxygenCommand(data, cmdName, rawCommand, this);
	}
	
	public boolean isPreserveWhitespace()
	{
		return preserveWhitespace;
	}

	public IdentityHashMap<Pattern, Object> getCustomCommandPatterns() {
		return customCommands;
	}
}
