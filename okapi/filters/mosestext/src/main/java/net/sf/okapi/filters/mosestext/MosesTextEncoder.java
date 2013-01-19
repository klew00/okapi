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

package net.sf.okapi.filters.mosestext;

import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.encoder.EncoderContext;
import net.sf.okapi.common.encoder.IEncoder;

public class MosesTextEncoder implements IEncoder {

	private static final String LINEBREAK = "<lb/>";
	
	@Override
	public String encode (String text,
			EncoderContext context)
	{
		if ( text == null ) return "";
		StringBuffer sbTmp = new StringBuffer(text.length());
		char ch;
		for ( int i=0; i<text.length(); i++ ) {
			ch = text.charAt(i);
			switch ( ch ) {
			case '<':
				sbTmp.append("&lt;");
				continue;
			case '&':
				sbTmp.append("&amp;");
				continue;
			case '\r': // In XML this is a literal not a line-break
				sbTmp.append("&#13;");
				break;
			case '\n':
				sbTmp.append(LINEBREAK);
				break;
			default:
				if ( ch > 127 ) { // Extended chars
					if ( Character.isHighSurrogate(ch) ) {
						int cp = text.codePointAt(i++);
						String tmp = new String(Character.toChars(cp));
						sbTmp.append(tmp);
					}
					else {
						sbTmp.append(ch);
					}
				}
				else { // ASCII chars
					sbTmp.append(ch);
				}
			}
		}
		return sbTmp.toString();
	}

	@Override
	public String encode (int value,
			EncoderContext context)
	{
		switch ( value ) {
		case '<':
			return "&lt;";
		case '&':
			return "&amp;";
		case '\r': // In XML this is a literal not a line-break
			return "&#13;";
		case '\n':
			return LINEBREAK;
		default:
			if ( Character.isSupplementaryCodePoint(value) ) {
				return new String(Character.toChars(value)).replace("\n", LINEBREAK);
			}
			return String.valueOf((char)value).replace("\n", LINEBREAK); 
		}
	}

	@Override
	public String encode (char value,
			EncoderContext context)
	{
		switch ( value ) {
		case '<':
			return "&lt;";
		case '&':
			return "&amp;";
		case '\r': // In XML this is a literal not a line-break
			return "&#13;";
		case '\n':
			return LINEBREAK;
		default:
			return String.valueOf(value);
		}
	}

	@Override
	public CharsetEncoder getCharsetEncoder () {
		return null;
	}

	@Override
	public String getLineBreak () {
		return LINEBREAK;
	}

	@Override
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		// Line-break is LINEBREAK
		// Encoding is always UTF-8
	}

	@Override
	public String toNative (String propertyName,
		String value)
	{
		return value;
	}

}
