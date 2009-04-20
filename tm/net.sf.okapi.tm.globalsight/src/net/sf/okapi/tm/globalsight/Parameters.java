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

package net.sf.okapi.tm.globalsight;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	public String username;
	public String password;
	public String serverURL;
	public String tmProfile;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		username = buffer.getString("username", username);
		password = buffer.getString("password", password);
		serverURL = buffer.getString("serverURL", serverURL);
		tmProfile = buffer.getString("tmProfile", tmProfile);
	}

	public void reset () {
		username = "";
		password = "";
		serverURL = "";
		tmProfile = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString("username", username);
		buffer.setString("password", password);
		buffer.setString("serverURL", serverURL);
		buffer.setString("tmProfile", tmProfile);
		return buffer.toString();
	}

}
