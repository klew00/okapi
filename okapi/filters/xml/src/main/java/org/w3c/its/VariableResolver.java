/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package org.w3c.its;

import java.util.Hashtable;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

/**
 * Resolver for XPath string variables.
 */
public class VariableResolver implements XPathVariableResolver {

	private Hashtable<QName, String> table;
	
	/**
	 * Resolves the variable for a given name.
	 * @aram qName the name of the variable.
	 * @return the value for the given name, or null if no value for that name exists.
	 */
	@Override
	public Object resolveVariable (QName qName) {
		if ( table == null ) return null;
		return table.get(qName);
	}

	/**
	 * Adds a variable and its value to this object. If the variable already exists, it is overwritten.
	 * @param qName the name of the variable.
	 * @param value the value.
	 */
	public void add (QName qName,
		String value)
	{
		if ( table == null ) table = new Hashtable<QName, String>();
		table.put(qName, value);
	}

}
