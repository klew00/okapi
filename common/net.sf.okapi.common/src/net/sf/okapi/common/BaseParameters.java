/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public abstract class BaseParameters extends ParametersString implements IParameters {

	protected String              path;

	public BaseParameters () {
		path = null;
	}
	
	public String getPath () {
		return path;
	}
	
	public void setParameter (String name,
		String value)
	{
		setString(name, value);
	}

	public void setParameter (String name,
		boolean value)
	{
		setBoolean(name, value);
	}

	public void setParameter (String name,
		int value)
	{
		setInteger(name, value);
	}

	public String getParameter (String name,
		String defaultValue)
	{
		return getString(name, defaultValue);
	}
		
	public boolean getParameter (String name,
		boolean defaultValue)
	{
		return getBoolean(name, defaultValue);
	}
		
	public int getParameter (String name,
		int defaultValue)
	{
		return getInteger(name, defaultValue);
	}
	
	public String getParameter (String name) {
		return getString(name);
	}

	public int getInt (String name,
		int defaultValue)
	{
		return getInteger(name, defaultValue);
	}
	
	public int getInt (String name) {
		return getInteger(name);
	}

	public void load (String filePath,
		boolean p_bIgnoreErrors)
	{
		try {
			// Reset the parameters to their defaults
			reset();
			// Open the file
			Reader SR = new InputStreamReader(
				new BufferedInputStream(new FileInputStream(filePath)),
				"UTF-8");

			// Read the file in one string
			StringBuilder sbTmp = new StringBuilder(1024);
			char[] aBuf = new char[100];
			int nCount;
			while ((nCount = SR.read(aBuf)) > -1) {
				sbTmp.append(aBuf, 0, nCount);	
			}
			SR.close();
			SR = null;

			// Parse it
			fromString(sbTmp.toString());
			path = filePath;
		}
		catch ( IOException e ) {
			if ( !p_bIgnoreErrors ) throw new RuntimeException(e);
		}
	}

	public void save (String newPath,
		String multiFilesPrefix)
	{
		Writer SW = null;
		try {
			// Set the output filename
			if ( multiFilesPrefix != null ) {
				String fname = Util.getFilename(newPath, true);
				String dir = Util.getDirectoryName(newPath);
				newPath = dir + File.separator + multiFilesPrefix + fname;
			}
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
}
