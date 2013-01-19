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
 * Implements {@link IEncoder} for properties-type format.
 */
public class PropertiesEncoder implements IEncoder {
	
	private CharsetEncoder chsEnc;
	private boolean escapeExtendedChars;
	private String lineBreak;
	
	/**
	 * Creates a new PropertiesEncoder object, with US-ASCII as the encoding.
	 */
	public PropertiesEncoder () {
		escapeExtendedChars = false;
		chsEnc = Charset.forName("us-ascii").newEncoder();
	}
	
	@Override
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		// lineBreak: They are converted to \n in this format
		chsEnc = Charset.forName(encoding).newEncoder();
		this.lineBreak = lineBreak;
		// Get the output options
		if ( params != null ) {
			escapeExtendedChars = params.getBoolean("escapeExtendedChars");
		}
	}

	@Override
	public String encode (String text,
			EncoderContext context)
	{
		StringBuilder escaped = new StringBuilder();
		char ch;
		for ( int i=0; i<text.length(); i++ ) {
			ch = text.charAt(i);
			
			if ( ch > 127 ) { // Extended chars
				if ( Character.isHighSurrogate(ch) ) {
					int cp = text.codePointAt(i++);
					String tmp = new String(Character.toChars(cp));
					if ( escapeExtendedChars
						|| (( chsEnc != null ) && !chsEnc.canEncode(tmp) )) {
						escaped.append(String.format("\\u%04x\\u%04x",
							(int)tmp.charAt(0), (int)tmp.charAt(1)));
					}
					else {
						escaped.append(tmp);
					}
				}
				else { // Should be able to fold to char, supplementary case will be treated
					if ( escapeExtendedChars
						|| (( chsEnc != null ) && !chsEnc.canEncode(ch) )) {
						escaped.append(String.format("\\u%04x", (int)ch));
					}
					else { // No encoder or char is supported
						escaped.append(String.valueOf(ch));
					}
				}
			}
			else { // ASCII chars
				switch ( ch ) {
				case '\n':
					escaped.append("\\n");
					break;
				case '\t':
					escaped.append("\\t");
					break;
				case ':':
				case '=':
					if ( i == 0 ) escaped.append('\\');
					// Fall thru
				default:
					escaped.append(ch);
					break;
				}
			}
		}
		return escaped.toString();
	}

	@Override
	public String encode (char value,
			EncoderContext context)
	{
		if ( value > 127 ) {
			if ( escapeExtendedChars || !chsEnc.canEncode(value) ) {
				return String.format("\\u%04x", (int)value);
			}
			else {
				return String.valueOf(value);
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

	@Override
	public String encode (int value,
			EncoderContext context)
	{
		if ( value > 127 ) {
			if ( Character.isSupplementaryCodePoint(value) ) {
				String tmp = new String(Character.toChars(value));
				if ( escapeExtendedChars || !chsEnc.canEncode(tmp) ) { 
					return String.format("\\u%04x\\u%04x",
						(int)tmp.charAt(0), (int)tmp.charAt(1));
				}
				else {
					return tmp;
				}
			}
			else { // Extended not supplemental
				if ( escapeExtendedChars 
					|| (( chsEnc != null ) && !chsEnc.canEncode((char)value) )) {
						return String.format("\\u%04x", value);
				}
				else {
					return String.valueOf((char)value);
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

	@Override
	public String toNative (String propertyName,
		String value)
	{
		// PROP_ENCODING: Not applicable
		// PROP_LANGUGE: Not applicable
		
		// No changes for the other values
		return value;
	}

	@Override
	public String getLineBreak () {
		return lineBreak;
	}

	@Override
	public CharsetEncoder getCharsetEncoder () {
		return chsEnc;
	}

}
