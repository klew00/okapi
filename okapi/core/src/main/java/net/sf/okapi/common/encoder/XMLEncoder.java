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

import net.sf.okapi.common.IParameters;

/**
 * Implements IEncoder for XML format.
 */
public class XMLEncoder implements IEncoder {

	private CharsetEncoder chsEnc;
	private String lineBreak;
	private boolean escapeGt = false;
	private boolean escapeNbsp = false;
	private boolean escapeLineBreak = false;
	private int quoteMode = 1;
	
	/**
	 * Sets the options for this encoder. This encoder supports the following
	 * parameters:
	 * <ul><li>escapeGt=true to converts '>' characters to to <code>&amp;gt;</code>.</li>
	 * <li>escapeNbsp=true to converts non-breaking space to <code>&amp;#x00a0;</code>.</li>
	 * <li>escapeLineBreak=true to converts line-breaks to <code>&amp;#10;</code>.</li>
	 * </ul>
	 * @param params the parameters object with all the configuration information 
	 * specific to this encoder.
	 * @param encoding the name of the character set encoding to use.
	 * @param lineBreak the type of line break to use.
	 */
	@Override
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		this.lineBreak = lineBreak;
		// Use an encoder only if the output is not UTF-8/16
		// since those support all characters
		if ( "utf-8".equalsIgnoreCase(encoding) || "utf-16".equalsIgnoreCase(encoding) ) {
			chsEnc = null;
		}
		else {
			chsEnc = Charset.forName(encoding).newEncoder();
		}
		
		if ( params != null ) {
			escapeGt = params.getBoolean("escapeGt");
			escapeNbsp = params.getBoolean("escapeNbsp");
			escapeLineBreak = params.getBoolean("escapeLineBreak");
			if ( params.getBoolean("quoteModeDefined") ) {
				quoteMode = params.getInteger("quoteMode");
			}
		}
	}

	@Override
	public String encode (String text, 
		int context)
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
			case '>':
				if ( escapeGt ) sbTmp.append("&gt;");
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
					sbTmp.append("&apos;");
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
				if ( escapeLineBreak ) {
					sbTmp.append("&#10;");
				}
				else {
					sbTmp.append(lineBreak);
				}
				break;
			case '\u00A0':
				if ( escapeNbsp ) {
					sbTmp.append("&#x00a0;");
					break;
				}
				// Else: fall through
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
			}
		}
		return sbTmp.toString();
	}

	@Override
	public String encode (char value,
		int context)
	{
		switch ( value ) {
		case '<':
			return "&lt;";
		case '\"':
			return "&quot;";
		case '\'':
			return "&apos;";
		case '&':
			return "&amp;";
		case '>':
			if ( escapeGt ) return "&gt;";
			else return ">";
		case '\n':
			if ( escapeLineBreak ) return "&#10;";
			else return lineBreak;
		case '\u00A0':
			if ( escapeNbsp ) {
				return "&#x00a0;";
			}
			// Else: fall through
		default:
			if ( value > 127 ) { // Extended chars
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
		int context)
	{
		switch ( value ) {
		case '<':
			return "&lt;";
		case '\"':
			return "&quot;";
		case '\'':
			return "&apos;";
		case '&':
			return "&amp;";
		case '>':
			if ( escapeGt ) return "&gt;";
			else return ">";
		case '\n':
			if ( escapeLineBreak ) return "&#10;";
			else return lineBreak;
		case '\u00A0':
			if ( escapeNbsp ) {
				return "&#x00a0;";
			}
			// Else: fall through
		default:
			if ( value > 127 ) { // Extended chars
				if ( Character.isSupplementaryCodePoint(value) ) {
					String tmp = new String(Character.toChars(value));
					if (( chsEnc != null ) && !chsEnc.canEncode(tmp) ) {
						return String.format("&#x%x;", value);
					}
					return tmp;
				}
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
		// PROP_ENCODING: Same value in native
		// PROP_LANGUGE: Same value in native
		return value;
	}

	@Override
	public String getLineBreak () {
		return lineBreak;
	}

}
