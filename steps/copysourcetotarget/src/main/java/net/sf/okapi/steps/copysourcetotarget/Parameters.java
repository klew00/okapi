/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.copysourcetotarget;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.LocaleId;

public class Parameters extends BaseParameters {

	public LocaleId targetLocale;
	
	public Parameters () {
		reset();
	}

	public void reset () {
		targetLocale = null;
	}

	public void fromString (String data) {
		reset();
		// Read the file content as a set of fields
		buffer.fromString(data);
		targetLocale = LocaleId.fromString(buffer.getString("targetLocale"));		
	}

	public String toString() {
		buffer.reset();
		buffer.setString("targetLocale", targetLocale.toString());				
		return buffer.toString();
	}
}
