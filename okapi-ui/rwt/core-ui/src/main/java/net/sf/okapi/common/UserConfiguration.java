/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.common;

import java.util.Properties;

/**
 * Blocking any global config by now.
 * TODO Consider saving session data in browser cookies.
 */
public class UserConfiguration extends Properties {
	
	private static final long serialVersionUID = 1L;
	
	public UserConfiguration () {
	}

	/**
	 * 
	 * @param appName
	 */
	public void load (String appName) {
	}
	
	public void save (String appName,
		String version) {
	}
	
	public String getProperty(String key) {
		return "";
	}
	
	public String getProperty(String key, String defaultValue) {
		return "";
	}
		
	@Override
	public Object setProperty (String key,
		String value)
	{
		return "";
	}
	
	/**
	 * Gets a boolean property value.
	 * @param key The name of the property.
	 * @return True if the property exists and is set to "true", false if it
	 * does not exists or if it is set to "false".
	 */
	public boolean getBoolean (String key) {
		return false;
	}

	/**
	 * Gets a boolean property value possibly set to a default value.
	 * @param key the name of the property.
	 * @param defaultValue the default value.
	 * @return the value of the property if it exists, 
	 * or the default value if it does not exist.
	 */
	public boolean getBoolean (String key,
		boolean defaultValue)
	{
		return false;
	}
	
	/**
	 * Sets a boolean property.
	 * @param key The name of the property.
	 * @param value The new value for the property.
	 * @return The previous value of the property, or null if the
	 * property did not exists yet.
	 */
	public Object setProperty (String key,
		boolean value)
	{
		return false;
	}

	/**
	 * Gets an integer property.
	 * @param key The name of the property.
	 * @return The integer value of the property if it exists, 0 if it
	 * does not exists.
	 */
	public int getInteger (String key) {
		return 0;
	}
	
	/**
	 * Sets an integer property.
	 * @param key The name of the property.
	 * @param value The new value for the property.
	 * @return The previous value of the property, or null if the
	 * property did not exist.
	 */
	public Object setProperty (String key,
		int value)
	{
		return 0;
	}
	
}
