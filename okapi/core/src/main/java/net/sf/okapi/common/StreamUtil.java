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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

}
