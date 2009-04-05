/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.packages.xliff;

import net.sf.okapi.common.BaseParameters;

public class Options extends BaseParameters {

	public boolean gMode;
	public boolean includeNoTranslate;
	public String message;
	
	public Options () {
		reset();
	}
	
	public void reset() {
		gMode = false;
		includeNoTranslate = true;
		message = "";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		gMode = buffer.getBoolean("gMode", gMode);
		includeNoTranslate = buffer.getBoolean("includeNoTranslate", includeNoTranslate);
		message = buffer.getString("message", message);
	}

	public String toString () {
		buffer.reset();
		buffer.setParameter("gMode", gMode);
		buffer.setBoolean("includeNoTranslate", includeNoTranslate);
		buffer.setParameter("message", message);
		return buffer.toString();
	}
	
}
