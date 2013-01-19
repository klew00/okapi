/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.uriconversion;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	public final static int UNESCAPE = 0;
	public int conversionType;
	public boolean updateAll;
	public String escapeList;
	
	public Parameters () {
		reset();
	}

	public void reset () {
		conversionType = UNESCAPE;
		updateAll = false;
		escapeList = "%{}[]()&";
	}

	public void fromString (String data) {
		reset();
		// Read the file content as a set of fields
		buffer.fromString(data);
		conversionType = buffer.getInteger("conversionType", conversionType);
		updateAll = buffer.getBoolean("updateAll", updateAll);
		escapeList = buffer.getString("escapeList", escapeList);
	}
	
	public String toString() {
		buffer.reset();
		buffer.setInteger("conversionType", conversionType);
		buffer.setBoolean("updateAll", updateAll);		
		buffer.setString("escapeList", escapeList);
		return buffer.toString();
	}

}
