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

/**
 * Encodes <, >, and &, but not " or '
 */

import net.sf.okapi.common.IParameters;

public class OpenXMLEncoder implements IEncoder {

	private String lineBreak;
	
	/**
	 * Sets the options for this encoder. This encoder supports the following
	 * parameters:
	 * <ul><li>escapeGt=true to converts '>' characters to to <code>&amp;gt;</code>.</li>
	 * <li>escapeNbsp=true to converts non-breaking space to <code>&amp;nbsp;</code>.</li>
	 * </ul>
	 * @param params the parameters object with all the configuration information 
	 * specific to this encoder.
	 * @param encoding the name of the charset encoding to use.
	 * @param lineBreak the type of line break to use.
	 */
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		this.lineBreak = lineBreak;
		// Use an encoder only if the output is not UTF-8/16
		// since those support all characters
	}

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
				sbTmp.append("&gt;");
				continue;
			case '&':
				sbTmp.append("&amp;");
				continue;
			case '\n':
				sbTmp.append(lineBreak);
				break;
			default:
				if ( ch > 127 ) // Extended chars
				{
					if ( Character.isHighSurrogate(ch) )
					{
						int cp = text.codePointAt(i++);
						String tmp = new String(Character.toChars(cp));
							sbTmp.append(tmp);
					}
				}
				else // Should be able to fold to char, supplementary case will be treated
					sbTmp.append(String.valueOf(ch));
				break;
			}
		}
		return sbTmp.toString();
	}

	public String encode (char value,
		int context)
	{
		switch ( value ) {
		case '<':
			return "&lt;";
		case '&':
			return "&amp;";
		case '>':
			return "&gt;";
		case '\n':
			return lineBreak;
		default:
			return String.valueOf(value);
		}
	}

	public String encode (int value,
		int context)
	{
		switch ( value ) {
		case '<':
			return "&lt;";
		case '&':
			return "&amp;";
		case '>':
			return "&lt;";
		case '\n':
			return lineBreak;
		default:
			if ( value > 127 ) { // Extended chars
				if ( Character.isSupplementaryCodePoint(value) ) {
					String tmp = new String(Character.toChars(value));
					return tmp;
				}
				return String.valueOf((char)value);
			}
			else { // ASCII chars
				return String.valueOf((char)value);
			}
		}
	}

	public String toNative (String propertyName,
		String value)
	{
		// PROP_ENCODING: Same value in native
		// PROP_LANGUGE: Same value in native
		return value;
	}

}
