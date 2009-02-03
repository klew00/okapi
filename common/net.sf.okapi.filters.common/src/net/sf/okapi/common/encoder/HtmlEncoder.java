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
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IEncoder;

/**
 * Implements IEncoder for HTML format.
 */
public class HtmlEncoder implements IEncoder {
	
	/** Normalized version of the HTML content */
	public static final String NORMALIZED_LANGUAGE = "language";
	
	/** Normalized version of the HTML charset */
	public static final String NORMALIZED_ENCODING = "encoding";
	
	/** HTML content attribute */
	public static final String CONTENT = "content";
	
	/** HTML charset identifier */
	public static final String CHARSET = "charset";

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IEncoder#setOptions(net.sf.okapi.common.IParameters, java.lang.String)
	 */
	public void setOptions (IParameters params,
		String encoding)
	{
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IEncoder#encode(java.lang.String, int)
	 */
	public String encode (String text, int context) {
		return Util.escapeToXML(text, 1, false);
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IEncoder#encode(char, int)
	 */
	public String encode (char value, int context) {
		switch ( value ) {
		case '<':
			return "&lt";
		case '\"':
			return "&quot;";
		case '\'':
			return "&apos;";
		case '&':
			return "&amp;";
		default:
			return String.valueOf(value);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IEncoder#toNative(java.lang.String, java.lang.String)
	 */
	public String toNative (String propertyName,
		String value)
	{
		return value;
	}
}
