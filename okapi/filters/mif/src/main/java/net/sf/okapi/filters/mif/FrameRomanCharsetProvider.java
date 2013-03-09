/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.mif;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.Iterator;

public class FrameRomanCharsetProvider extends CharsetProvider {

	private static final String NAME = "x-FrameRoman";
	
	private final String[] aliases = {"FrameRoman", "MIFRoman"};

	// Zero-argument constructor
    public FrameRomanCharsetProvider() {
    }

    @Override
	public Charset charsetForName (String name) {
		// Check the main name
		if ( name.equalsIgnoreCase(NAME) ) {
			return new FrameRomanCharset(NAME, aliases);
		}
		// Check our aliases
		for ( String aliasName : aliases) {
			if ( name.equalsIgnoreCase(aliasName)) {
				return new FrameRomanCharset(NAME, aliases);
			}
		}
		// Else: Unknown name
		return null;
	}

	@Override
	public Iterator<Charset> charsets () {
		// Create a list with the lone encoding this provider supports
		ArrayList<Charset> list = new ArrayList<Charset>();
		list.add(Charset.forName(NAME));
		// Return the iterator
		return list.iterator();
	}

}
