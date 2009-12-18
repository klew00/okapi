/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Simple storage and retrieval class for text content (segmented or not). 
 */
class SimpleStore {

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
	
	public void write (TextContainer tc) {
		try {
			if ( tc.isSegmented() ) {
				for ( Segment seg : tc.getSegments() ) {
					dos.writeUTF(seg.text.getCodedText());
					dos.writeUTF(Code.codesToString(seg.text.getCodes()));
				}
			}
			else {
				TextFragment tf = tc.getContent();
				dos.writeUTF(tf.getCodedText());
				dos.writeUTF(Code.codesToString(tf.getCodes()));
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error while writing.", e);
		}
	}

	/**
	 * Reads the next text container in the store.
	 * @return the next text container in the store, or null if the end is reached.
	 */
	public TextFragment readNext () {
		try {
			String codedText = dis.readUTF();
			String tmp = dis.readUTF();
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
	
}
