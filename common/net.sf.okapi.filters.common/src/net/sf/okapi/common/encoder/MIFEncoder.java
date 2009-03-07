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

import net.sf.okapi.common.IParameters;

/**
 * Implements IEncoder for Adobe FrameMaker MIF format.
 */
public class MIFEncoder implements IEncoder {

	public String encode (String text,
		int context)
	{
		StringBuilder escaped = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			if ( text.codePointAt(i) > 127 ) {
				escaped.append(String.format("\\u%04X", text.codePointAt(i))); 
				//TODO: Do we need legacy \xHH using MIF encoding?
			}
			else {
				switch ( text.charAt(i) ) {
				case '\t':
					escaped.append("\\t");
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
		switch ( value ) {
		case '\t':
			return "\\t";
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
				return String.format("\\u%04X", value);
				//TODO: Do we need legacy \xHH using MIF encoding?
			}
			else {
				return String.valueOf(value);
			}
		}
	}

	public void setOptions (IParameters params,
		String encoding)
	{
		// Nothing to do
	}

	public String toNative (String propertyName,
		String value)
	{
		if ( PROP_ENCODING.equals(propertyName) ) {
			if ( "shift-jis".equals(value) ) return "\u65E5\u672C\u8A9E";
			//TODO: CJK etc...
		}

		// PROP_LANGUGE: Not applicable

		// No changes for the other values
		return value;
	}

}
