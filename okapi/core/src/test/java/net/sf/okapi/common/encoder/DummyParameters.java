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

package net.sf.okapi.common.encoder;

import java.net.URI;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ParametersDescription;

public class DummyParameters implements IParameters {

	private boolean escapeExtendedChars;
	private boolean escapeGt;
	private boolean escapeNbsp;

	public void setBoolean (String name,
		boolean value)
	{
		if ( name.equals("escapeExtendedChars") )
			escapeExtendedChars = value;
		if ( name.equals(XMLEncoder.ESCAPEGT) )
			escapeGt = value;
		if ( name.equals(XMLEncoder.ESCAPENBSP) )
			escapeNbsp = value;
	}
	
	public void fromString (String data) {
		// Not needed for tests
	}

	public boolean getBoolean (String name) {
		if ( name.equals("escapeExtendedChars") )
			return escapeExtendedChars;
		if ( name.equals(XMLEncoder.ESCAPEGT) )
			return escapeGt;
		if ( name.equals(XMLEncoder.ESCAPENBSP) )
			return escapeNbsp;
		return false;
	}

	public String getPath () {
		// Not needed for tests
		return null;
	}
	
	public void setPath (String filePath) {
		// Not needed for tests
	}

	public String getString (String name) {
		// Not needed for tests
		return null;
	}

	public void load (URI inputURI, boolean ignoreErrors) {
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
	
	public ParametersDescription getParametersDescription () {
		return null;
	}

	@Override
	public void setInteger (String name, int value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setString (String name, String value) {
		// TODO Auto-generated method stub
	}

}
