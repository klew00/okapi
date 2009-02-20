/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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
import java.io.PushbackInputStream;

/**
 * Helper class to detect and skip BOM in InputStream, including for UTF-8.
 */
//TODO: Maybe find a better way to deal with UTF-8 BOM: this is pretty ugly...
public class BOMAwareInputStream extends InputStream {

	private static final int BUFFER_SIZE = 4;
	
	private PushbackInputStream initStream;
	private InputStream input;

	private String defaultEncoding;
	private String detectedEncoding;
	private int bomSize;
	private boolean hasUTF8BOM;

	public BOMAwareInputStream (InputStream input,
		String defaultEncoding)
	{
		this.input = input;
		this.defaultEncoding = defaultEncoding;
		bomSize = 0;
		hasUTF8BOM = false;
	}
	
	public String detectEncoding ()
		throws IOException
	{
		initStream = new PushbackInputStream(input, BUFFER_SIZE);
		byte bom[] = new byte[BUFFER_SIZE];
		int n = initStream.read(bom, 0, bom.length);
		int unread;
		if (( bom[0] == (byte)0xEF )
			&& ( bom[1] == (byte)0xBB )
			&& ( bom[2] == (byte)0xBF )) {
			detectedEncoding = "UTF-8";
			bomSize = 3;
			hasUTF8BOM = true;
			unread = n-3;
		}
		else if (( bom[0] == (byte)0xFE )
			&& ( bom[1] == (byte)0xFF )) {
			detectedEncoding = "UTF-16BE";
			bomSize = 2;
			unread = n-2;
		}
		else if (( bom[0] == (byte)0xFF )
			&& ( bom[1] == (byte)0xFE )
			&& ( bom[2] == (byte)0x00 )
			&& ( bom[3] == (byte)0x00 )) {
			detectedEncoding = "UTF-32LE";
			bomSize = 4;
			unread = n-4;
		}
		else if (( bom[0] == (byte)0xFF )
			&& ( bom[1] == (byte)0xFE )) {
			detectedEncoding = "UTF-16LE";
			bomSize = 2;
			unread = n-2;
		}
		else if (( bom[0] == (byte)0x00 )
			&& ( bom[1] == (byte)0x00 )
			&& ( bom[2] == (byte)0xFE )
			&& ( bom[3] == (byte)0xFF )) {
			detectedEncoding = "UTF-32BE";
			bomSize = 4;
			unread = n-4;
		}
		else { // No BOM
			detectedEncoding = defaultEncoding;
			bomSize = 0;
			unread = n;
		}

		// Push-back bytes as needed
		if ( unread > 0 ) {
			initStream.unread(bom, (n-unread), unread);
		}

		return detectedEncoding;
	}

	public String getDetectedEncoding () {
		return detectedEncoding;
	}
	
	public int getBOMSize () {
		return bomSize;
	}
	
	public boolean hasUTF8BOM () {
		return hasUTF8BOM;
	}
	
	@Override
	public int read()
		throws IOException
	{
		return initStream.read();
	}

}
