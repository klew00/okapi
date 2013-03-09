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

package net.sf.okapi.common;

import net.sf.okapi.common.BaseParameters;

public class DummyParameters extends BaseParameters {

	public boolean paramBool1;
	public int paramInt1;
	public String paramStr1;
	public String reference1;
	public String password1;

	public DummyParameters () {
		reset();
		toString(); // fill the list
	}
	
	public void reset () {
		paramBool1 = true;
		paramInt1 = 123;
		paramStr1 = "test";
		reference1 = "reference1";
		password1 = "password";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		paramBool1 = buffer.getBoolean("paramBool1", paramBool1);
		paramInt1 = buffer.getInteger("paramInt1", paramInt1);
		paramStr1 = buffer.getString("paramStr1", paramStr1);
		password1 = buffer.getEncodedString("password1", password1);
	}
	
	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("paramBool1", paramBool1);
		buffer.setInteger("paramInt1", paramInt1);
		buffer.setString("paramStr1", paramStr1);
		buffer.setEncodedString("password1", password1);
		return buffer.toString();
	}

	@ReferenceParameter
	public String getReference1 () {
		return reference1;
	}
	
	public void setReference1 (String reference1) {
		this.reference1 = reference1;
	}
	
	public void setParamStr1 (String value) {
		paramStr1 = value;
	}
	
	public void setParamBool1 (boolean value) {
		paramBool1 = value;
	}
	
	public void setParamInt1 (int value) {
		paramInt1 = value;
	}
	
	public String getPassword1 () {
		return password1;
	}
	
	public void setPassword1 (String password1) {
		this.password1 = password1;
	}
}
