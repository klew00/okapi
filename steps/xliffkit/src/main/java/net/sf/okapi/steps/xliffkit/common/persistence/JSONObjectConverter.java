/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.common.persistence;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONObjectConverter {

	private static ObjectMapper converter = new ObjectMapper();
	
	/**
	 * Converts a given object to an expected type.
	 * The given object is serialized as is, and then deserialized as 
	 * an expected class instance. This helps if the object was initially deserialized incorrectly.
	 * @param obj the given object to be converted.
	 * @param expectedClass new class of the given object.
	 * @return the converted object.
	 */
	public static <T extends IPersistenceBean> T convert(Object object, Class<T> expectedClass) {		
		return converter.convertValue(object, expectedClass);
	}
}
