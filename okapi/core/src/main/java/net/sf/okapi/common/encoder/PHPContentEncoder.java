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

package net.sf.okapi.common.encoder;

import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.IParameters;

/**
 * Implements {@link IEncoder} for JSON format.
 */
public class PHPContentEncoder implements IEncoder {
	
//	private CharsetEncoder chsEnc;
	
	/**
	 * Creates a new PHPContentEncoder object, with US-ASCII as the encoding.
	 */
	public PHPContentEncoder () {
//		chsEnc = Charset.forName("us-ascii").newEncoder();
	}
	
	@Override
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
//		chsEnc = Charset.forName(encoding).newEncoder();
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
					escaped.append(tmp);
				}
				else { // Should be able to fold to char, supplementary case will be treated
					escaped.append(String.valueOf(ch));
				}
			}
			else { // ASCII chars
				switch ( ch ) {
//				case '\n':
//					escaped.append("\\n");
//					break;
//				case '\t':
//					escaped.append("\\t");
//					break;
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
			return String.valueOf(value);
		}
		else {
			switch ( value ) {
//			case '\n':
//				return "\\n";
//			case '\t':
//				return "\\t";
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
				return tmp;
			}
			else { // Extended not supplemental
				return String.valueOf((char)value);
			}
		}			
		else { // Non-extended
			switch ( (char)value ) {
//			case '\n':
//				return "\\n";
//			case '\t':
//				return "\\t";
			default:
				return String.valueOf((char)value);
			}
		}
	}

	@Override
	public String toNative (String propertyName,
		String value)
	{
		// No changes
		return value;
	}

	@Override
	public String getLineBreak () {
		return "\n";
	}

	@Override
	public CharsetEncoder getCharsetEncoder () {
		return null;
	}

}
