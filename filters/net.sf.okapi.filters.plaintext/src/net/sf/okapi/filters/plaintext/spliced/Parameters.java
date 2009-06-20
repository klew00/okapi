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

/**
 * Spliced Lines Filter parameters
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev   
 */

public class Parameters extends net.sf.okapi.filters.plaintext.base.Parameters {
	
	/**
	 * Char at the end of a line, signifying the line is continued on the next line (normally "\" or "_"). Can be a custom string too.<p>
	 * Default: \ (backslash)
	 */
	public String splicer = "\\"; 
	
	/**
	 * If in-line codes should be created for the dropped splicers and linebreaks of spliced lines
	 * Default: true (create in-line codes)
	 */
	public boolean createPlaceholders = true;
	
//----------------------------------------------------------------------------------------------------------------------------	
	
	public Parameters() {
		
		super();		
		
		reset();
		toString(); // fill the list
	}

	public void reset() {
		
		super.reset();
		
		// All parameters are set to defaults here
		splicer = "\\";
		createPlaceholders = true;
	}

	public void fromString(String data) {
		
		reset();
		
		super.fromString(data);
		
		buffer.fromString(data);
		
		// All parameters are retrieved here		
		splicer = buffer.getString("splicer", "\\");
		createPlaceholders = buffer.getBoolean("createPlaceholders", false);
	}
	
	@Override
	public String toString () {
		
		buffer.reset();
		
		super.toString(); // Will write to the same buffer
		
		// All parameters are set here
		buffer.setString("splicer", splicer);
		buffer.setBoolean("createPlaceholders", createPlaceholders);
		
		return buffer.toString();
	}
	
}
