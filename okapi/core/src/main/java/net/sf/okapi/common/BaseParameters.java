/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

/**
 * Base class for properties-like parameters that implement IParameters.
 * See the {@link ParametersString} documentation for details on the storage format.
 */
public abstract class BaseParameters implements IParameters {

	/**
	 * Current path of the parameter file.
	 */
	protected String path;
	
	/**
	 * Buffer where the parameters are stored during conversion.
	 */
	protected ParametersString buffer;
	
	/**
	 * Creates a new BaseParameters object with a null path and an empty buffer.
	 */
	public BaseParameters () {
		path = null;
		buffer = new ParametersString();
	}

	@Override
	public String getPath () {
		return path;
	}

	@Override
	public void setPath (String filePath) {
		path = filePath;
	}
	
	@Override
	public void load (URI inputURI,
		boolean ignoreErrors)
	{
		char[] aBuf;
		try {
			// Reset the parameters to their defaults
			reset();
			// Open the file. use a URL so we can do openStream() and load
			// predefined files from JARs.
			URL url = inputURI.toURL();
			Reader SR = new InputStreamReader(
				new BufferedInputStream(url.openStream()), "UTF-8");

			// Read the file in one string
			StringBuilder sbTmp = new StringBuilder(1024);
			aBuf = new char[1024];
			int nCount;
			while ((nCount = SR.read(aBuf)) > -1) {
				sbTmp.append(aBuf, 0, nCount);	
			}
			SR.close();
			SR = null;

			// Parse it
			String tmp = sbTmp.toString().replace("\r\n", "\n");
			fromString(tmp.replace("\r", "\n"));
			path = inputURI.getPath();
		}
		catch ( IOException e ) {
			if ( !ignoreErrors ) throw new RuntimeException(e);
		}
		finally {
			aBuf = null;
		}
	}

	@Override
	public void save (String newPath) {
		Writer SW = null;
		try {
			// Save the fields on file
			SW = new OutputStreamWriter(
				new BufferedOutputStream(new FileOutputStream(newPath)),
				"UTF-8");
			SW.write(toString());
			path = newPath;
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			if ( SW != null )
				try { SW.close(); } catch ( IOException e ) {};
		}
	}

	@Override
	public boolean getBoolean (String name) {
		// Make sure the buffer is up-to-date and check the type of storage
		// We do this to avoid setting YAML-based parameters
		if ( !toString().startsWith("#v") ) return false; 
		return buffer.getBoolean(name);
	}
	
	@Override
	public void setBoolean (String name,
		boolean value)
	{
		// Make sure the buffer is up-to-date and check the type of storage
		// We do this to avoid setting YAML-based parameters
		if ( !toString().startsWith("#v") ) return; 

		// Previous we were doing:
		// buffer.setBoolean(name, value);
		// fromString(buffer.toString()); // Update the variables from the buffer
		// But this calls fromString() which resets all variables, including the runtime ones
		// like regex compiled rules, etc.

		// Instead, now we try to use the bean method
		try {
			// Get the bean method for updating the parameter
			BeanInfo info;
			info = Introspector.getBeanInfo(getClass());
			for ( PropertyDescriptor pd : info.getPropertyDescriptors() ) {
				if ( pd.getName().equals(name) ) {
					Method writeMethod = pd.getWriteMethod();
					// Update the object directly
					writeMethod.invoke(this, value);
					// Update the buffer list too (just in case)
					buffer.setBoolean(name, value);
					return; // Done
				}
			}
		}
		catch ( Throwable e ) {
			throw new RuntimeException(String.format("Error with setInteger() on '%s':\n" + e.getMessage(), name));
		}
	}
	
	@Override
	public String getString (String name) {
		// Make sure the buffer is up-to-date and check the type of storage
		// We do this to avoid setting YAML-based parameters
		if ( !toString().startsWith("#v") ) return null; 
		return buffer.getString(name);
	}
	
	@Override
	public void setString (String name,
		String value)
	{
		// Make sure the buffer is up-to-date and check the type of storage
		// We do this to avoid setting YAML-based parameters
		if ( !toString().startsWith("#v") ) return; 

		// Previous we were doing:
		// buffer.setInteger(name, value);
		// buffer.setString(name, value);
		// fromString(buffer.toString()); // Update the variables from the buffer
		// like regex compiled rules, etc.

		// Instead, now we try to use the bean method
		try {
			// Get the bean method for updating the parameter
			BeanInfo info;
			info = Introspector.getBeanInfo(getClass());
			for ( PropertyDescriptor pd : info.getPropertyDescriptors() ) {
				if ( pd.getName().equals(name) ) {
					Method writeMethod = pd.getWriteMethod();
					// Update the object directly
					writeMethod.invoke(this, value);
					// Update the buffer list too (just in case)
					buffer.setString(name, value);
					return; // Done
				}
			}
		}
		catch ( Throwable e ) {
			throw new RuntimeException(String.format("Error with setInteger() on '%s':\n" + e.getMessage(), name));
		}
	}

	@Override
	public int getInteger (String name) {
		// Make sure the buffer is up-to-date and check the type of storage
		// We do this to avoid setting YAML-based parameters
		if ( !toString().startsWith("#v") ) return 0; 
		return buffer.getInteger(name);
	}

	@Override
	public void setInteger (String name,
		int value)
	{
		// Make sure the buffer is up-to-date and check the type of storage
		// We do this to avoid setting YAML-based parameters
		if ( !toString().startsWith("#v") ) return; 

		// Previous we were doing:
		// buffer.setInteger(name, value);
		// fromString(buffer.toString()); // Update the variables from the buffer
		// But this calls fromString() which resets all variables, including the runtime ones
		// like regex compiled rules, etc.

		// Instead, now we try to use the bean method
		try {
			// Get the bean method for updating the parameter
			BeanInfo info;
			info = Introspector.getBeanInfo(getClass());
			for ( PropertyDescriptor pd : info.getPropertyDescriptors() ) {
				if ( pd.getName().equals(name) ) {
					Method writeMethod = pd.getWriteMethod();
					// Update the object directly
					writeMethod.invoke(this, value);
					// Update the buffer list too (just in case)
					buffer.setInteger(name, value);
					return; // Done
				}
			}
		}
		catch ( Throwable e ) {
			throw new RuntimeException(String.format("Error with setInteger() on '%s':\n" + e.getMessage(), name));
		}
	}

	@Override
	public ParametersDescription getParametersDescription () {
		return null;
	}

}
