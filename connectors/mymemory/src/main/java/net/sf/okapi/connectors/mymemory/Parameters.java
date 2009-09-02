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

package net.sf.okapi.connectors.mymemory;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	private String key;
	private int useMT;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public String getKey () {
		return key;
	}

	public void setKey (String key) {
		this.key = key;
	}

	public int getUseMT() {
		return useMT;
	}

	public void setUseMT (int useMT) {
		this.useMT = useMT;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		key = buffer.getString("key", key);
		useMT = buffer.getInteger("useMT", useMT);
	}

	public void reset () {
		key = "mmDemo123";
		useMT = 1;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString("key", key);
		buffer.setInteger("useMT", useMT);
		return buffer.toString();
	}

}
