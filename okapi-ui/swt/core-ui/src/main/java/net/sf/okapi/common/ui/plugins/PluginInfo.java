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

package net.sf.okapi.common.ui.plugins;

public class PluginInfo {

	private String name;
	private String provider;
	private String description;
	private String helpURL;

	/**
	 * Creates a new PluginInfo object.
	 * @param name the name of the plugin.
	 * @param provider the provider name (can be null).
	 * @param description a description of what the plugin does (can be null).
	 * @param helpURL a URL to an help page (can be null).
	 */
	public PluginInfo (String name,
		String provider,
		String description,
		String helpURL)
	{
		this.name = name;
		this.provider = provider;
		this.description = description;
		this.helpURL = helpURL;
	}
	
	public String getName () {
		return name;
	}

	public String getProvider () {
		return provider;
	}

	public String getDescription () {
		return description;
	}

	public String getHelpURL () {
		return helpURL;
	}

}
