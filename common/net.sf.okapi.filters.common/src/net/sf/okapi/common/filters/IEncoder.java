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

package net.sf.okapi.common.filters;

import net.sf.okapi.common.IParameters;

/**
 * Provides common methods to encode/escape text to a specific format.
 * <p>Important: Each class implementing this interface must have a nullary constructor, so the object 
 * can be instantiated using the Class.fromName() methods by the EncoderManager.
 */
public interface IEncoder {

	public static final String PROP_ENCODING = "encoding";
	public static final String PROP_LANGUAGE = "language";
	
	/**
	 * Sets the options for this encoder.
	 * @param params The parameters object with all the configuration information 
	 * specific to this encoder.
	 * @param encoding The name of the charset encoding to use.
	 */
	public void setOptions (IParameters params, String encoding);
	
	/**
	 * Encodes a given text with this encoder.
	 * @param text The text to encode.
	 * @param context The context of the text: 0=text, 1=skeleton, 2=inline.
	 * @return The encoded text.
	 */
	public String encode (String text, int context);
	
	/**
	 * Encodes a given character with this encoding.
	 * @param value The character to encode.
	 * @param context The context of the character: 0=text, 1=skeleton, 2=inline.
	 * @return The encoded character 9as a string since it can be now made up of
	 * more than one character).
	 */
	public String encode (char value, int context);
	
	/**
	 * Converts any property values from its standard representation to
	 * the native representation for this encoder.
	 * @param propertyName Name of the property.
	 * @param value Standard value to convert.
	 * @return Native representation of the given value.
	 */
	public String toNative (String propertyName,
		String value);

}
