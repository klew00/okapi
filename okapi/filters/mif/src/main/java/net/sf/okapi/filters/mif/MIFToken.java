/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.mif;

import net.sf.okapi.common.resource.Code;

class MIFToken {

	public static final int TYPE_NULL = 0;
	public static final int TYPE_STRING = 1;
	public static final int TYPE_CODE = 2;
	
	private int type;
	private String stringValue;
	private Code codeValue;
	private boolean isLast;

	public MIFToken () {
		type = TYPE_NULL;
	}
	
	public MIFToken (String value) {
		type = TYPE_STRING;
		stringValue = value;
	}
	
	public MIFToken (Code value) {
		type = TYPE_CODE;
		codeValue = value;
	}
	
	@Override
	public String toString () {
		if ( type == TYPE_STRING ) return stringValue;
		else return "";
	}
	
	public void setString (String value) {
		type = TYPE_STRING;
		stringValue = value;
	}
	
	public String getString () {
		return stringValue;
	}
	
	public Code getCode () {
		return codeValue;
	}
	
	public void setLast (boolean value) {
		isLast = value;
	}
	
	public boolean isLast () {
		return isLast;
	}
	
	public void setType (int value) {
		type = value;
	}
	
	public int getType () {
		return type;
	}
	
}
