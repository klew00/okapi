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

package net.sf.okapi.common.encoder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IEncoder;

/**
 * Implements IEncoder for XML format.
 */
public class XMLEncoder implements IEncoder {

	private CharsetEncoder chsEnc;
	
	public void setOptions (IParameters params,
		String encoding)
	{
		// Use an encoder only if the output is not UTF-8/16
		// since those support all characters
		if ( "utf-8".equals(encoding) || "utf-16".equals(encoding) ) {
			chsEnc = null;
		}
		else {
			chsEnc = Charset.forName(encoding).newEncoder();
		}
	}

	public String encode (String text, int context) {
		return Util.escapeToXML(text, 1, false, chsEnc);
	}

	public String encode (char value, int context) {
		switch ( value ) {
		case '<':
			return "&lt";
		case '\"':
			return "&quot;";
		case '\'':
			return "&apos;";
		case '&':
			return "&amp;";
		default:
			//TODO: Escape unsupported chars
			return String.valueOf(value);
		}
	}

	public String toNative (String propertyName,
		String value)
	{
		// PROP_ENCODING: Same value in native
		// PROP_LANGUGE: Same value in native

		// No changes for the other values
		return value;
	}

}
