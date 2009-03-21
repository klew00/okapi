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

import net.sf.okapi.common.IParameters;

/**
 * Implements IEncoder for PO file format.
 */
public class POEncoder implements IEncoder {
	
//	private String lineBreak;
	
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		//this.lineBreak = lineBreak;
	}

	public String encode (String text,
		int context)
	{
		return text;
	}

	public String encode (char value,
		int context)
	{
		return String.valueOf(value);
	}

	public String encode (int value,
		int context)
	{
		if ( Character.isSupplementaryCodePoint(value) ) {
			return new String(Character.toChars(value));
		}
		return String.valueOf((char)value); 
	}

	public String toNative (String propertyName,
		String value)
	{
		// PROP_LANGUAGE: Not applicable
		// PROP_ENCODING: No change
		
		// No changes for the other values
		if ( propertyName.equals("approved") ) {
			if (( value != null ) && ( value.equals("no") )) {
				return "fuzzy";
			}
			else { // Don't set the fuzzy flag
				return "";
			}
		}
		return value;
	}

}
