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

package net.sf.okapi.filters.wiki;

import java.util.Map;

import net.sf.okapi.common.BaseParameters;

import org.yaml.snakeyaml.Yaml;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Parameters extends BaseParameters
{
	public static final String WIKI_PARAMETERS = "wikiConfiguration.yml";
	
	private Yaml yaml;
	private Map<String, Object> config;
	private boolean preserveWhitespace = false;

	public Parameters ()
	{	
		yaml = new Yaml();
		reset();
	}

	public void reset()
	{
		config = (Map) yaml.load(WikiFilter.class.getResourceAsStream(WIKI_PARAMETERS));
		initialize();
	}
	
	private void initialize() {
		
		Object whitespace = config.get("preserve_whitespace");
		if (whitespace != null) preserveWhitespace = ((Boolean) whitespace).booleanValue();
		
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
	
	public boolean isPreserveWhitespace()
	{
		return preserveWhitespace;
	}

}
