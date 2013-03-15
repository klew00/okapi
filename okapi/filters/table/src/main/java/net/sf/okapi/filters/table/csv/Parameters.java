/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.table.csv;

import net.sf.okapi.common.ParametersString;

/**
 * CSV Filter parameters
 * 
 * @version 0.1, 09.06.2009  
 */

public class Parameters extends net.sf.okapi.filters.table.base.Parameters {

	final public static int ESCAPING_MODE_DUPLICATION = 1;
	final public static int ESCAPING_MODE_BACKSLASH = 2;
	
	/**
	 * Character separating fields in a row. <p>
	 * Default: , (comma)
	 */
	public String fieldDelimiter;
	
	/** 
	 * Character before and after field value to allow field delimiters inside the field. 
	 * For instance, this field will not be broken into parts: "Field, containing comma, \", "" and \n".
	 * The qualifiers are not included in translation units.<p>  
	 * Default: " (quotation mark)
	 */ 
	public String textQualifier;
	
	/**
	 * True if qualifiers should be dropped, and shouldn't go into the text units
	 */
	public boolean removeQualifiers = true;
	
	/**
	 * The way qualifiers in the original text are escaped.
	 */
	public int escapingMode = ESCAPING_MODE_DUPLICATION;

	@Override
	protected void parameters_load(ParametersString buffer) {

		super.parameters_load(buffer);
		
		fieldDelimiter = buffer.getString("fieldDelimiter", ""); 
		textQualifier = buffer.getString("textQualifier", "");
		removeQualifiers = buffer.getBoolean("removeQualifiers", true);
		escapingMode = buffer.getInteger("escapingMode", ESCAPING_MODE_DUPLICATION);
	}

	@Override
	protected void parameters_reset() {

		super.parameters_reset();
		
		fieldDelimiter = ",";
		textQualifier = "\"";
		removeQualifiers = true;
		escapingMode = ESCAPING_MODE_DUPLICATION;
	}

	@Override
	protected void parameters_save(ParametersString buffer) {

		super.parameters_save(buffer);
		
		buffer.setString("fieldDelimiter", fieldDelimiter);
		buffer.setString("textQualifier", textQualifier);
		buffer.setBoolean("removeQualifiers", removeQualifiers);
		buffer.setInteger("escapingMode", escapingMode);
	}
	
}
