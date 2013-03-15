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

package net.sf.okapi.filters.plaintext.spliced;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.Util;

/**
 * Spliced Lines Filter parameters
 * @version 0.1, 09.06.2009
 */

public class Parameters extends net.sf.okapi.filters.plaintext.base.Parameters {
	
	/**
	 * Char at the end of a line, signifying the line is continued on the next line (normally "\" or "_"). Can be a custom string too.<p>
	 * Default: \ (backslash)
	 */
	public String splicer; 
	
	/**
	 * If in-line codes should be created for the dropped splicers and linebreaks of spliced lines
	 * Default: true (create in-line codes)
	 */
	public boolean createPlaceholders;
	
//----------------------------------------------------------------------------------------------------------------------------	
	
	@Override
	protected void parameters_load(ParametersString buffer) {

		super.parameters_load(buffer);
		
		String st = buffer.getString("splicer", "\\");
		if (Util.isEmpty(st))
			splicer = "";
		else
			splicer = st.trim(); 
				
		createPlaceholders = buffer.getBoolean("createPlaceholders", false);
	}

	@Override
	protected void parameters_reset() {

		super.parameters_reset();
		
		splicer = "\\";
		createPlaceholders = true;
	}

	@Override
	protected void parameters_save(ParametersString buffer) {

		super.parameters_save(buffer);
		
		buffer.setString("splicer", splicer);
		buffer.setBoolean("createPlaceholders", createPlaceholders);
	}

}
