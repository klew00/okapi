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

package net.sf.okapi.filters.vignette;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Simple temporary binary storage and retrieval class for Vignette block. 
 */
class TemporaryStore {

	private static int MAXBLOCKLEN = 65000;
	
	DataOutputStream dos = null;
	DataInputStream dis = null;

	public void close () {
		try {
			if ( dis != null ) {
				dis.close();
				dis = null;
			}
			if ( dos != null ) {
				dos.close();
				dos = null;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error closing.", e);
		}
	}
	
	public void create (File file) {
		try {
			close();
			dos = new DataOutputStream(new FileOutputStream(file));
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error creating.", e);
		}
	}
	
	public void openForRead (File file) {
		try {
			close();
			dis = new DataInputStream(new FileInputStream(file));
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error opening.", e);
		}
	}

	private void writeLongString (String data)
		throws IOException
	{
		int r = (data.length() % MAXBLOCKLEN);
		int n = (data.length() / MAXBLOCKLEN);
		int count = n + ((r > 0) ? 1 : 0);
		
		dos.writeInt(count); // Number of blocks
		int pos = 0;

		// Write the full blocks
		for ( int i=0; i<n; i++ ) {
			dos.writeUTF(data.substring(pos, pos+MAXBLOCKLEN));
			pos += MAXBLOCKLEN;
		}
		// Write the remaining text
		if ( r > 0 ) {
			dos.writeUTF(data.substring(pos));
		}
	}
	
	private String readLongString ()
		throws IOException
	{
		StringBuilder tmp = new StringBuilder();
		int count = dis.readInt();
		for ( int i=0; i<count; i++ ) {
			tmp.append(dis.readUTF());
		}
		return tmp.toString();
	}
	
	public void writeBlock (String contentId,
		String data)
	{
		try {
			dos.writeUTF(contentId);
			//dos.writeUTF(data);
			writeLongString(data);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error while writing.", e);
		}
	}

	// Return String[0] = sourceId, String[1] = data 
	public String[] readNext () {
		try {
			String[] res = new String[2];
			res[0] = dis.readUTF();
			//res[1] = dis.readUTF();
			res[1] = readLongString();
			return res;
		}
		catch ( EOFException e ) {
			return null;
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error while reading.", e);
		}
	}
	
}
