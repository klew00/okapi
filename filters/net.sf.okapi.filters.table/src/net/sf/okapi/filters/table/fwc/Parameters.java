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

package net.sf.okapi.filters.table.fwc;

/**
 * Fixed-Width Columns Filter parameters
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev   
 */

public class Parameters extends net.sf.okapi.filters.table.base.Parameters {

	/** 
	 * Specifies positions of fixed-width table columns. The positions are x-coordinates, like the position of a char in a string.
	 * The difference is that columnStartPositions are 1-based.  
	 * Can be represented by one of the following string types:
	 *<li>"1" - position (1-based) where the column starts
	 *<li>"1,2,5" - comma-delimited list (1-based) of starting positions of the table columns
	 */
	public String columnWidths = "";
			
//----------------------------------------------------------------------------------------------------------------------------	
	
	public Parameters() {
		
		super();		
		
		reset();
		toString(); // fill the list
	}

	public void reset() {
		
		super.reset();
		
		// All parameters are set to defaults here
		columnWidths = "";
		trimMode = TRIM_ALL;  // To get rid of white-space padding in-between columns
	}

	public void fromString(String data) {
		
		reset();
		
		super.fromString(data);
		
		buffer.fromString(data);
		
		// All parameters are retrieved here		
		columnWidths = buffer.getString("columnWidths", "").trim(); // null is impossible, default is ""
	}
	
	@Override
	public String toString () {
		
		buffer.reset();
		
		super.toString(); // Will write to the same buffer
		
		// All parameters are set here						
		buffer.setString("columnWidths", columnWidths);
		
		return buffer.toString();
	}

}
 