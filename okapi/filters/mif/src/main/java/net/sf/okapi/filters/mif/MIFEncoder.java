/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.encoder.EncoderContext;
import net.sf.okapi.common.encoder.IEncoder;

/**
 * Implements {@link IEncoder} for Adobe FrameMaker MIF format.
 */
public class MIFEncoder implements IEncoder {

	private CharsetEncoder chsEnc;
	private CharBuffer tmpBuf = CharBuffer.allocate(1);
	private StringBuilder outBuf = new StringBuilder();
	private ByteBuffer encBuf;

	@Override
	public String encode (String text,
		EncoderContext context)
	{
		StringBuilder escaped = new StringBuilder();
		char ch;
		for ( int i=0; i<text.length(); i++ ) {
			ch = text.charAt(i);
			switch ( text.charAt(i) ) {
			case '\t':
				escaped.append(tryCharStatment(ch));
				break;
			case '>':
				escaped.append("\\>");
				break;
			case '\'':
				escaped.append("\\q");
				break;
			case '`':
				escaped.append("\\Q");
				break;
			case '\\':
				escaped.append("\\\\");
				break;
			default:
				if ( ch > 127 ) {
					String res = tryCharStatment(ch);
					if ( res != null ) {
						escaped.append(res);
						continue;
					}
					// Normal extended characters
					if ( chsEnc == null ) {
						// UTF-8/16, nothing special
						escaped.append(String.valueOf(ch));
						continue;
					}
					// Else: we should escape
					tmpBuf.put(0, ch);
					tmpBuf.position(0);
					try {
						encBuf = chsEnc.encode(tmpBuf);
					}
					catch ( CharacterCodingException e ) {
						return "?"; // Unknown
					}
					if ( encBuf.limit() > 1 ) {
						for (int j=0; j<encBuf.limit(); j++) {
							escaped.append(String.format("\\x%x ", encBuf.get(j)));
						}
					}
					else {
						escaped.append(String.format("\\x%x ", encBuf.get(0)));
					}
				}
				else {
					escaped.append(ch);
				}
				break;
			}
		}
		return escaped.toString();
	}

	@Override
	public String encode (char value,
		EncoderContext context)
	{
		switch ( value ) {
		case '\t':
			return tryCharStatment(value);
		case '>':
			return "\\>";
		case '\'':
			return "\\q";
		case '`':
			return "\\Q";
		case '\\':
			return "\\\\";
		default:
			if ( value > 127 ) {
				String res = tryCharStatment(value);
				if ( res != null ) return res;
				// Normal extended characters
				if ( chsEnc == null ) {
					// UTF-8/16, nothing special
					return String.valueOf(value);
				}
				// Else: we should escape
				tmpBuf.put(0, value);
				tmpBuf.position(0);
				try {
					encBuf = chsEnc.encode(tmpBuf);
				}
				catch ( CharacterCodingException e ) {
					return "?"; // Unknown
				}
				if ( encBuf.limit() > 1 ) {
					outBuf.setLength(0);
					for (int j=0; j<encBuf.limit(); j++) {
						outBuf.append(String.format("\\x%x ", encBuf.get(j)));
					}
					return outBuf.toString();
				}
				else {
					return String.format("\\x%x ", encBuf.get(0));
				}
			}
			else {
				return String.valueOf(value);
			}
		}
	}

	@Override
	public String encode (int value,
		EncoderContext context)
	{
		switch ( value ) {
		case '\t':
			return tryCharStatment(value);
		case '>':
			return "\\>";
		case '\'':
			return "\\q";
		case '`':
			return "\\Q";
		case '\\':
			return "\\\\";
		default:
			if ( value > 127 ) {
				String res = tryCharStatment(value);
				if ( res != null ) return res;
				// Normal extended characters
				if ( chsEnc == null ) {
					// UTF-8/16, nothing special
					return String.valueOf((char)value);
				}
				// Else: we should escape
				tmpBuf.put(0, (char)value);
				tmpBuf.position(0);
				try {
					encBuf = chsEnc.encode(tmpBuf);
				}
				catch ( CharacterCodingException e ) {
					return "?"; // Unknown
				}
				if ( encBuf.limit() > 1 ) {
					outBuf.setLength(0);
					for (int j=0; j<encBuf.limit(); j++) {
						outBuf.append(String.format("\\x%x ", encBuf.get(j)));
					}
					return outBuf.toString();
				}
				else {
					return String.format("\\x%x ", encBuf.get(0));
				}
			}
			else { // ASCII
				return String.valueOf((char)value);
			}
		}
	}

	@Override
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		if ( encoding == null ) return;
		if ( "utf-8".equalsIgnoreCase(encoding) || "utf-16".equalsIgnoreCase(encoding) ) {
			chsEnc = null;
		}
		else {
			if ( encoding.equals(MIFFilter.FRAMEROMAN) ){
				chsEnc = new FrameRomanCharsetProvider().charsetForName(encoding).newEncoder();
			}
			else {
				chsEnc = Charset.forName(encoding).newEncoder();
			}
		}
	}

	@Override
	public String toNative (String propertyName,
		String value)
	{
//		if ( Property.ENCODING.equals(propertyName) ) {
//			if ( "shift-jis".equals(value) ) return "\u65E5\u672C\u8A9E";
//			//TODO: CJK etc...
//		}

		// PROP_LANGUGE: Not applicable

		// No changes for the other values
		return value;
	}

	@Override
	public String getLineBreak () {
		return "\n";
	}

	@Override
	public CharsetEncoder getCharsetEncoder () {
		return chsEnc;
	}

	/**
	 * Maps the value to an escaped character.
	 * @param value the value to convert
	 * @return the mapped string, or null if there is no mapping
	 */
	protected String tryCharStatment (int value) {
		String token = "";
		switch ( value ) {
		case '\t': token = "Tab"; break;
		case '\u00a0': token = "HardSpace"; break;
		case '\u2010': token = "SoftHyphen"; break;
		case '\u2011': token = "HardHyphen"; break;
		case '\u00ad': token = "DiscHyphen"; break;
		case '\u200d': token = "NoHyphen"; break;
		case '\u00a2': token = "Cent"; break;
		case '\u00a3': token = "Pound"; break;
		case '\u00a5': token = "Yen"; break;
		case '\u2013': token = "EnDash"; break;
		case '\u2014': token = "EmDash"; break;
		case '\u2020': token = "Dagger"; break;
		case '\u2021': token = "DoubleDagger"; break;
		case '\u2022': token = "Bullet"; break;
		case '\n': token = "HardReturn"; break;
		case '\u2007': token = "NumberSpace"; break;
		case '\u2009': token = "ThinSpace"; break;
		case '\u2002': token = "EnSpace"; break;
		case '\u2003': token = "EmSpace"; break;
		default:
			return null;
		}
		return "'><Char " + token + "><String `";
	}

}
