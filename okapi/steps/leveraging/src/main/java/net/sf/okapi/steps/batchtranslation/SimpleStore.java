/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.batchtranslation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Simple storage and retrieval class for text content (segmented or not). 
 */
class SimpleStore {

	/**
	 * maximum number of characters per writing block.
	 * This is less than MAXINT because the maximum is really in UTF-8 bytes and
	 * we can have 1 char = several bytes in many cases, so there is soom room built-in.
	 */
	private static int MAXBLOCKLEN = 40000;

	private DataOutputStream dos = null;
	private DataInputStream dis = null;

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
	
	/**
	 * Writes a text fragment.
	 * @param tf the text fragment to write out.
	 */
	public void write (TextFragment tf) {
		try {
			writeLongString(tf.getCodedText());
			writeLongString(Code.codesToString(tf.getCodes()));
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error while writing.", e);
		}
	}

	/**
	 * Reads the next text fragment in the store.
	 * @return the next text fragment in the store, or null if the end is reached.
	 */
	public TextFragment readNext () {
		try {
			String codedText = readLongString();
			String tmp = readLongString();
			TextFragment tf = new TextFragment(codedText, Code.stringToCodes(tmp));
			return tf;
		}
		catch ( EOFException e ) { // Normal end
			return null;
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error while reading.", e);
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
		int count = dis.readInt();
		if ( count == 0 ) return "";
		if ( count == 1 ) return dis.readUTF();
		// Else: read the multiple blocks
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<count; i++ ) {
			tmp.append(dis.readUTF());
		}
		return tmp.toString();
	}
		
}
