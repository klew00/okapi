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

/**
 * Implements IEncoder for properties-type format.
 */
public class PropertiesEncoder implements IEncoder {
	
	private CharsetEncoder chsEnc;
	private boolean escapeExtendedChars;
	
	/**
	 * Creates a new PropertiesEncoder object, with US-ASCII as the encoding, and
	 * escaping all extended characters.
	 */
	public PropertiesEncoder () {
		escapeExtendedChars = false;
		chsEnc = Charset.forName("us-ascii").newEncoder();
	}
	
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		// lineBreak: They are converted to \n in this format
		chsEnc = Charset.forName(encoding).newEncoder();
		
		// Get the output options
		if ( params != null ) {
			escapeExtendedChars = params.getBoolean("escapeExtendedChars");
		}
	}

	public String encode (String text,
		int context)
	{
		StringBuilder escaped = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			if ( text.codePointAt(i) > 127 ) {
				if ( escapeExtendedChars ) {
					escaped.append(String.format("\\u%04x", text.codePointAt(i))); 
				}
				else {
					if ( chsEnc.canEncode(text.charAt(i)) )
						escaped.append(text.charAt(i));
					else
						escaped.append(String.format("\\u%04x", text.codePointAt(i)));
				}
			}
			else {
				switch ( text.charAt(i) ) {
				case '\n':
					escaped.append("\\n");
					break;
				case '\t':
					escaped.append("\\t");
					break;
				default:
					escaped.append(text.charAt(i));
					break;
				}
			}
		}
		return escaped.toString();
	}

	public String encode (char value,
		int context)
	{
		if ( value > 127 ) {
			if ( escapeExtendedChars ) {
				return String.format("\\u%04x", (int)value);
			}
			else {
				if ( chsEnc.canEncode(value) )
					return String.valueOf(value);
				else
					return String.format("\\u%04x", (int)value);
			}
		}
		else {
			switch ( value ) {
			case '\n':
				return "\\n";
			case '\t':
				return "\\t";
			default:
				return String.valueOf(value);
			}
		}
	}

	public String encode (int value,
		int context)
	{
		if ( value > 127 ) {
			if ( Character.isSupplementaryCodePoint(value) ) {
				String tmp = new String(Character.toChars(value));
				if ( escapeExtendedChars ) {
					return String.format("\\u%04x\\u%04x",
						(int)tmp.charAt(0), (int)tmp.charAt(1));
				}
				else {
					if ( !chsEnc.canEncode(tmp) ) {
						return String.format("\\u%04x\\u%04x",
							(int)tmp.charAt(0), (int)tmp.charAt(1));
					}
					else {
						return tmp;
					}
				}
			}
			else { // Extended not supplemental
				if ( escapeExtendedChars ) {
					return String.format("\\u%04x", value);
				}
				else {
					if (( chsEnc != null ) && !chsEnc.canEncode((char)value) ) {
						return String.format("&#x%x;", value);
					}
					else {
						return String.valueOf((char)value);
					}
				}
			}
		}			
		else { // Non-extended
			switch ( (char)value ) {
			case '\n':
				return "\\n";
			case '\t':
				return "\\t";
			default:
				return String.valueOf((char)value);
			}
		}
	}

	public String toNative (String propertyName,
		String value)
	{
		// PROP_ENCODING: Not applicable
		// PROP_LANGUGE: Not applicable
		
		// No changes for the other values
		return value;
	}

}
