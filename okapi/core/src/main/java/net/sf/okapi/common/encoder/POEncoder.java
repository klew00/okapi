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
import net.sf.okapi.common.resource.Property;

/**
 * Implements {@link IEncoder} for PO file format.
 */
public class POEncoder implements IEncoder {
	
	@Override
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		//this.lineBreak = lineBreak;
	}

	@Override
	public String encode (String text,
			EncoderContext context)
	{
		return text;
	}

	@Override
	public String encode (char value,
			EncoderContext context)
	{
		return String.valueOf(value);
	}

	@Override
	public String encode (int value,
			EncoderContext context)
	{
		if ( Character.isSupplementaryCodePoint(value) ) {
			return new String(Character.toChars(value));
		}
		return String.valueOf((char)value); 
	}

	@Override
	public String toNative (String propertyName,
		String value)
	{
		if ( propertyName.equals(Property.APPROVED) ) {
			if (( value != null ) && ( value.equals("no") )) {
				return "fuzzy";
			}
			else { // Don't set the fuzzy flag
				return "";
			}
		}

		// No changes for the other values
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
