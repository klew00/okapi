/*===========================================================================
  Copyright (C) 2008-2013 by the Okapi Framework contributors
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Properties;

import net.sf.okapi.common.ui.rwt.RwtNotImplementedException;

import org.eclipse.rwt.RWT;

/**
 * Stores user settings in a session store (persisted in browser cookies).
 */
public class UserConfiguration extends Properties {
	
	private static final long serialVersionUID = 6498362881893256039L;

	public UserConfiguration() {
		super(new Properties());
	}

	@SuppressWarnings("unchecked")
	public void load(String appName) {
		for (Object attrName : Collections.list(RWT.getSettingStore().getAttributeNames())) {
			// Populate own hashmap with all attributes from the settings store
			super.put(attrName, RWT.getSettingStore().getAttribute((String) attrName));
		}		
	}
	
	@Override
	public synchronized void load(InputStream inStream) throws IOException {
		throw new RwtNotImplementedException(this, ".load(InputStream inStream)");
	}
	
	@Override
	public synchronized void load(Reader reader) throws IOException {
		throw new RwtNotImplementedException(this, ".load(Reader reader)");
	}
	
	public void save(String appName,
		String version) {
		for (Object attrName : super.keySet()) {
			try {
				RWT.getSettingStore().setAttribute(attrName.toString(), super.get(attrName).toString());
			} catch (Exception e) {
				// Silently ignore
			}
		}
	}
	
	@Override
	public Object setProperty (String key,
		String value)
	{
		if ( value == null ) return remove(key);
		else return super.setProperty(key, value);
	}
	
	/**
	 * Gets a boolean property value.
	 * @param key The name of the property.
	 * @return True if the property exists and is set to "true", false if it
	 * does not exists or if it is set to "false".
	 */
	public boolean getBoolean (String key) {
		return "true".equals(getProperty(key, "false"));
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
		return "true".equals(getProperty(key, (defaultValue ? "true" : "false")));
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
		return super.setProperty(key, (value ? "true" : "false"));
	}

	/**
	 * Gets an integer property.
	 * @param key The name of the property.
	 * @return The integer value of the property if it exists, 0 if it
	 * does not exists.
	 */
	public int getInteger (String key) {
		return Integer.valueOf(getProperty(key, "0"));
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
		return super.setProperty(key, String.valueOf(value));
	}	
}
