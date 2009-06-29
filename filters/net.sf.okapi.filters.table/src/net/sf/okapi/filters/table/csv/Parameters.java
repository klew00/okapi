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

package net.sf.okapi.filters.table.csv;

/**
 * CSV Filter parameters
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev  
 */

public class Parameters extends net.sf.okapi.filters.table.base.Parameters {

	/**
	 * Symbol or a string separating fields in a row. <p>
	 * Default: , (comma)
	 */
	public String fieldDelimiter = ",";
	
	/** 
	 * Symbol or a string before and after field value to allow special characters inside the field. 
	 * For instance, this field will not be broken into parts: "Field, containing comma, \", "" and \n".
	 * The qualifiers are not included in translation units.<p>  
	 * Default: " (quotation mark)
	 */ 
	public String textQualifier = "\"";
	
	/**
	 * True if qualifiers should be dropped, and shouldn't go into the text units
	 */
	public boolean removeQualifiers = true; 

	public Parameters() {
		
		super();		
		
		reset();
		toString(); // fill the list
	}

	public void reset() {
		
		super.reset();
		
		// All parameters are set to defaults here
		fieldDelimiter = ",";
		textQualifier = "\"";
		removeQualifiers = true;
	}

	public void fromString(String data) {
		
		reset();
		
		super.fromString(data);
		
		buffer.fromString(data);
		
		// All parameters are retrieved here		
		fieldDelimiter = buffer.getString("fieldDelimiter", "").trim(); 
		textQualifier = buffer.getString("textQualifier", "").trim();
		removeQualifiers = buffer.getBoolean("removeQualifiers", true);
	}
	
	@Override
	public String toString () {
		
		buffer.reset();
		
		super.toString(); // Will write to the same buffer
		
		// All parameters are set here						
		buffer.setString("fieldDelimiter", fieldDelimiter);
		buffer.setString("textQualifier", textQualifier);
		buffer.setBoolean("removeQualifiers", removeQualifiers);
		
		return buffer.toString();
	}

}
