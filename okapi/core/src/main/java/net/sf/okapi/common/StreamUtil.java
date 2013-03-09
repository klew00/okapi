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
===========================================================================*/

package net.sf.okapi.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import net.sf.okapi.common.exceptions.OkapiFileNotFoundException;
import net.sf.okapi.common.exceptions.OkapiIOException;

/**
 * Implements a helper method to read an InputStream into an array of bytes.
 * Adapted from http://forums.sun.com/thread.jspa?threadID=606890
 * @author adrian.ajb
 */
public class StreamUtil {

	/**
	 * Reads an InputStream into an array of bytes.
	 * @param in the input stream to read.
	 * @return the array of bytes read.
	 * @throws IOException if an error occurs.
	 */
	public static byte[] inputStreamToBytes (InputStream in)
		throws IOException
	{
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[1024];
			int len;

			while((len = in.read(buffer)) >= 0) {
				out.write(buffer, 0, len);
			}
		}
		finally {
			if ( out != null ) {
				out.close();
			}
		}

		return out.toByteArray();
	}

	private static final int IO_BUFFER_SIZE = 8 * 1024;
	
	public static void copy(InputStream in, OutputStream out) {		
		byte[] b = new byte[IO_BUFFER_SIZE];  
		int read;  
		try {
			while ((read = in.read(b)) != -1) {  
			out.write(b, 0, read);  
			}
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	} 
	
	public static void copy(File in, OutputStream out) {
		try {
			copy(new FileInputStream(in), out);
		} catch (FileNotFoundException e) {
			throw new OkapiFileNotFoundException(e);
		}
	}
	
	public static void copy(InputStream in, File out) {
		try {
			copy(in, new FileOutputStream(out));
		} catch (FileNotFoundException e) {
			throw new OkapiFileNotFoundException(e);
		}
	}
	
	public static void copy(File in, File out) {
		try {
			copy(new FileInputStream(in), new FileOutputStream(out));
		} catch (FileNotFoundException e) {
			throw new OkapiFileNotFoundException(e);
		}
	}
	
	public static String streamAsString(InputStream in, String encoding) {
		BufferedReader reader;
		StringBuilder tmp = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, encoding));
			tmp = new StringBuilder();
			char[] buf = new char[2048];
			int count = 0;
			while (( count = reader.read(buf)) != -1 ) {
				tmp.append(buf, 0, count);
			}		
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		
        return Util.normalizeNewlines(tmp.toString());
    }
	
	public static String streamAsString(InputStream in) {
        return streamAsString(in, "UTF-8");
    }
	
	public static InputStream stringAsStream(String str) {
		return new ByteArrayInputStream(str.getBytes());
	}
	
	public static InputStream stringAsStream(String str, String encoding) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(str.getBytes(encoding));
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		return is;
	}
}
