/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Library.Base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public abstract class BaseParameters implements IParameters {

	protected String    path;

	/* Do not define BaseParameters() constructor 
	 * calling reset(), as some derived classes may need to 
	 * create object before calling reset
	 */
	// public BaseParameters()
	
	public abstract void reset ();
	
	public abstract String toString ();
	
	public abstract void fromString (String p_sData);
	
	public String getPath () {
		return path;
	}
	public String getParameter (String p_sName) {
		//TODO: Find a faster/better way to implement getOption()
		FieldsString FS = new FieldsString(toString());
		return FS.get(p_sName, null);
	}

	public void setParameter (String p_sName,
		String p_sValue)
	{
		//TODO: Find a faster/better way to implement setOption()
		FieldsString FS = new FieldsString(toString());
		FS.set(p_sName, p_sValue);
		fromString(FS.toString());
	}

	public void load (String filePath,
		boolean p_bIgnoreErrors)
		throws Exception
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
			char[] aBuf = new char[10];
			@SuppressWarnings("unused")
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
		catch ( Exception E ) {
			if ( !p_bIgnoreErrors ) throw E;
		}
	}

	public void save (String newPath,
		String multiFilesPrefix)
		throws Exception
	{
		// multiFilesPrefix is not used in this default implementation
		Writer SW = null;
		try {
			// Save the fields on file
			SW = new OutputStreamWriter(
				new BufferedOutputStream(new FileOutputStream(newPath)),
				"UTF-8");
			SW.write(toString());
			path = newPath;
		}
		catch ( Exception E ) {
			throw E;
		}
		finally {
			if ( SW != null ) SW.close();
		}
	}
}
