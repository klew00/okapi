/*===========================================================================
Copyright (C) 2008 by the Okapi Framework contributors
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

package net.sf.okapi.common.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Simple class to store user-specific of an application in the user folder.
 */
public class UserConfiguration extends Properties {
	
	private static final long serialVersionUID = 1L;
	
	public UserConfiguration () {
		super();
		defaults = new Properties();
	}

	/**
	 * 
	 * @param appName
	 */
	public void load (String appName) {
		try {
			InputStream input = new FileInputStream(
				new File(System.getProperty("user.home") + File.separator + "."+appName));
			load(input);
		}
		catch ( IOException e ) {
			// Don't care about not reading this file
		}
	}
	
	public void save (String appName,
		String version)
	{
		try {
			OutputStream output = new FileOutputStream(
				new File(System.getProperty("user.home") + File.separator + "."+appName));
			store(output, appName + " - " + version);
		}
		catch ( IOException e ) {
			// Don't care about not writing this file
		}
	}
	
	@Override
	public Object setProperty (String key,
		String value)
	{
		if ( value == null ) return remove(key);
		else return super.setProperty(key, value);
	}
	
	public boolean getBoolean (String key) {
		return "true".equals(getProperty(key, "false"));
	}
	
	public Object setProperty (String key,
		boolean value)
	{
		return super.setProperty(key, (value ? "true" : "false"));
	}
}
