/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

import net.sf.okapi.common.HTMLCharacterEntities;
import net.sf.okapi.common.IParameters;

/**
 * Implements {@link IEncoder} for HTML format.
 */
public class HtmlEncoder implements IEncoder {
			
	/** HTML content attribute */
	public static final String CONTENT = "content";
	
	/** HTML charset identifier */
	public static final String CHARSET = "charset";

	private CharsetEncoder chsEnc;
	private String lineBreak;
	private int quoteMode = 1;
	private String charsToCER = null;
	private HTMLCharacterEntities entities;

	@Override
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		this.lineBreak = lineBreak;
		// Use an encoder only if the output is not UTF-8/16
		// since those support all characters
		if ( "utf-8".equals(encoding) || "utf-16".equals(encoding) ) {
			chsEnc = null;
		}
		else {
			chsEnc = Charset.forName(encoding).newEncoder();
		}

		// Get options from the filter's configuration
		if ( params != null ) {
			// Retrieve the options
			charsToCER = params.getString("escapeCharacters");
		}
	}

	@Override
	public String encode (String text,
			EncoderContext context)
	{
		if ( text == null ) return "";
		boolean escapeGT = false;
		
		StringBuffer sbTmp = new StringBuffer(text.length());
		char ch;
		for ( int i=0; i<text.length(); i++ ) {
			ch = text.charAt(i);
			switch ( ch ) {
			case '<':
				sbTmp.append("&lt;");
				continue;
			case '>':
				if ( escapeGT ) sbTmp.append("&gt;");
				else {
					if (( i > 0 ) && ( text.charAt(i-1) == ']' )) sbTmp.append("&gt;");
					else sbTmp.append('>');
				}
				continue;
			case '&':
				sbTmp.append("&amp;");
				continue;
			case '"':
				if ( quoteMode > 0 ) sbTmp.append("&quot;");
				else sbTmp.append('"');
				continue;
			case '\'':
				switch ( quoteMode ) {
				case 1:
					sbTmp.append("&#39;");
					break;
				case 2:
					sbTmp.append("&#39;");
					break;
				default:
					sbTmp.append(ch);
					break;
				}
				continue;
			case '\n':
				sbTmp.append(lineBreak);
				break;
			default:
				if ( ch > 127 ) { // Extended chars
					if ( Character.isHighSurrogate(ch) ) {
						int cp = text.codePointAt(i++);
						String tmp = new String(Character.toChars(cp));
						if (( chsEnc != null ) && !chsEnc.canEncode(tmp) ) {
							sbTmp.append(String.format("&#x%x;", cp));
						}
						else {
							sbTmp.append(tmp);
						}
					}
					else { // Should be able to fold to char, supplementary case will be treated
						String cer = checkCER(ch);
						if ( cer != null ) {
							sbTmp.append(cer);
							continue;
						}
						// Else: fall back to normal character process
						if (( chsEnc != null ) && !chsEnc.canEncode(ch) ) {
							sbTmp.append(String.format("&#x%04x;", (int)ch));
						}
						else { // No encoder or char is supported
							sbTmp.append(String.valueOf(ch));
						}
					}
				}
				else { // ASCII chars
					sbTmp.append(ch);
				}
				continue;
			}
		}
		return sbTmp.toString();
	}

	/**
	 * Checks if the character needs/can be represented as a CER.
	 * @param ch the character to process.
	 * @return the string representing the CER or null if the character needs to be processed as usual.
	 */
	private String checkCER (char ch) {
		if (( charsToCER != null ) && ( charsToCER.indexOf(ch) > -1 )) {
			if ( entities == null ) {
				entities = new HTMLCharacterEntities();
				entities.ensureInitialization(false);
			}
			String name = entities.getName(ch);
			if ( name == null ) return null;
			else return "&"+name+";";
		}
		return null;
	}
	
	@Override
	public String encode (char value,
			EncoderContext context)
	{
		switch ( value ) {
		case '<':
			return "&lt;";
		case '\"':
			if ( quoteMode > 0 ) return "&quot;";
			else  return "\"";
		case '\'':
			if ( quoteMode > 0 ) return "&#39;";
			else return "\'";
		case '&':
			return "&amp;";
		case '\n':
			return lineBreak;
		default:
			if ( value > 127 ) { // Extended chars
				String cer = checkCER(value);
				if ( cer != null ) {
					return cer;
				}
				// Else: fall back to normal character process
				if (( chsEnc != null ) && ( !chsEnc.canEncode(value) )) {
					return String.format("&#x%04x;", (int)value);
				}
				else { // No encoder or char is supported
					return String.valueOf(value);
				}
			}
			else { // ASCII chars
				return String.valueOf(value);
			}
		}
	}

	@Override
	public String encode (int value,
			EncoderContext context)
	{
		switch ( value ) {
		case '<':
			return "&lt;";
		case '\"':
			if ( quoteMode > 0 ) return "&quot;";
			else  return "\"";
		case '\'':
			if ( quoteMode > 0 ) return "&#39;";
			else return "\'";
		case '&':
			return "&amp;";
		case '\n':
			return lineBreak;
		default:
			if ( value > 127 ) { // Extended chars
				if ( Character.isSupplementaryCodePoint(value) ) {
					String tmp = new String(Character.toChars(value));
					if (( chsEnc != null ) && !chsEnc.canEncode(tmp) ) {
						return String.format("&#x%x;", value);
					}
					return tmp;
				}
				String cer = checkCER((char)value);
				if ( cer != null ) {
					return cer;
				}
				// Else: fall back to normal character process
				// Should be able to fold to char, supplementary case will be treated
				if (( chsEnc != null ) && !chsEnc.canEncode((char)value) ) {
					return String.format("&#x%04x;", value);
				}
				else { // No encoder or char is supported
					return String.valueOf((char)value);
				}
			}
			else { // ASCII chars
				return String.valueOf((char)value);
			}
		}
	}

	@Override
	public String toNative (String propertyName,
		String value)
	{
		return value;
	}

	@Override
	public String getLineBreak () {
		return this.lineBreak;
	}

	@Override
	public CharsetEncoder getCharsetEncoder () {
		return chsEnc;
	}

}
