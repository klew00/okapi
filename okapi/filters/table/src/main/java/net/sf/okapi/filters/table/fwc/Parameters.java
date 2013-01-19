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

import net.sf.okapi.common.ParametersString;

/**
 * Fixed-Width Columns Filter parameters
 * 
 * @version 0.1, 09.06.2009 
 */

public class Parameters extends net.sf.okapi.filters.table.base.Parameters {

	/** 
	 * Specifies start positions of fixed-width table columns. The positions are x-coordinates, like the position of a char in a string.
	 * The difference is that columnStartPositions are 1-based.  
	 * Can be represented by one of the following string types:
	 *<li>"1" - position (1-based) where the column starts
	 *<li>"1,2,5" - comma-delimited list (1-based) of starting positions of the table columns<p>
	 * Default: Empty
	 */
	public String columnStartPositions;
	
	/** 
	 * Specifies end positions of fixed-width table columns. The positions are x-coordinates, like the position of a char in a string.
	 * The difference is that columnEndPositions are 1-based.  
	 * Can be represented by one of the following string types:
	 *<li>"1" - position (1-based) where the column starts
	 *<li>"1,2,5" - comma-delimited list (1-based) of starting positions of the table columns<p>
	 * Default: Empty
	 */
	public String columnEndPositions;
			
//----------------------------------------------------------------------------------------------------------------------------	
	
	@Override
	protected void parameters_load(ParametersString buffer) {

		super.parameters_load(buffer);
		
		columnStartPositions = buffer.getString("columnStartPositions", "").trim(); // null is impossible, default is ""
		columnEndPositions = buffer.getString("columnEndPositions", "").trim(); // null is impossible, default is ""
	}

	@Override
	protected void parameters_reset() {
		
		super.parameters_reset();
		
		columnStartPositions = "";
		columnEndPositions = "";
		trimMode = TRIM_ALL;  // To get rid of white-space padding in-between columns
	}

	@Override
	protected void parameters_save(ParametersString buffer) {

		super.parameters_save(buffer);
		
		buffer.setString("columnStartPositions", columnStartPositions);
		buffer.setString("columnEndPositions", columnEndPositions);
	}


}
 