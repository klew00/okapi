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

package net.sf.okapi.common.encoder.tests;

import net.sf.okapi.common.IParameters;

public class DummyParameters implements IParameters {

	private boolean escapeExtendedChars;
	private boolean escapeGt;
	private boolean escapeNbsp;

	public void setBoolean (String name,
		boolean value)
	{
		if ( name.equals("escapeExtendedChars") )
			escapeExtendedChars = value;
		if ( name.equals("escapeGt") )
			escapeGt = value;
		if ( name.equals("escapeNbsp") )
			escapeNbsp = value;
	}
	
	public void fromString (String data) {
		// Not needed for tests
	}

	public boolean getBoolean (String name) {
		if ( name.equals("escapeExtendedChars") )
			return escapeExtendedChars;
		if ( name.equals("escapeGt") )
			return escapeGt;
		if ( name.equals("escapeNbsp") )
			return escapeNbsp;
		return false;
	}

	public String getPath () {
		// Not needed for tests
		return null;
	}

	public String getString (String name) {
		// Not needed for tests
		return null;
	}

	public void load (String filePath, boolean ignoreErrors) {
		// Not needed for tests
	}

	public void reset () {
		// Not needed for tests
	}

	public void save (String filePath) {
		// Not needed for tests
	}

	public int getInteger(String name) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
