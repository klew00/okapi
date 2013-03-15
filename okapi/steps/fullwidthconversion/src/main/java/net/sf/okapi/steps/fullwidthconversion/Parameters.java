/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.fullwidthconversion;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	public boolean toHalfWidth;
	public boolean asciiOnly;
	public boolean includeSLA;
	public boolean includeLLS;
	
	public Parameters () {
		reset();
	}
	
	public void reset() {
		toHalfWidth = true;
		asciiOnly = false;
		includeSLA = false;
		includeLLS = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		toHalfWidth = buffer.getBoolean("toHalfWidth", toHalfWidth);
		asciiOnly = buffer.getBoolean("asciiOnly", asciiOnly);
		includeSLA = buffer.getBoolean("includeSLA", includeSLA);
		includeLLS = buffer.getBoolean("includeLLS", includeLLS);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean("toHalfWidth", toHalfWidth);
		buffer.setBoolean("asciiOnly", asciiOnly);
		buffer.setBoolean("includeSLA", includeSLA);
		buffer.setBoolean("includeLLS", includeLLS);
		return buffer.toString();
	}
	
}
