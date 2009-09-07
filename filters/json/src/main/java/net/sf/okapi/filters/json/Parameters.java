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

package net.sf.okapi.filters.json;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	private boolean extractStandalone;
	private boolean extractAllPairs;
	private String exceptions;

	public Parameters () {
		reset();
		toString(); // fill the list
	}
	
	public boolean getExtractStandalone () {
		return extractStandalone;
	}

	public void setExtractStandalone (boolean extractStandalone) {
		this.extractStandalone = extractStandalone;
	}

	public boolean getExtractAllPairs () {
		return extractAllPairs;
	}

	public void setExtractAllPairs (boolean extractAllPairs) {
		this.extractAllPairs = extractAllPairs;
	}

	public String getExceptions () {
		return exceptions;
	}

	public void setExceptions (String exceptions) {
		this.exceptions = exceptions;
	}

	public void reset () {
		extractStandalone = false;
		extractAllPairs = true;
		exceptions = "";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		extractStandalone = buffer.getBoolean("extractIsolatedStrings", extractStandalone);
		extractAllPairs = buffer.getBoolean("extractAllPairs", extractAllPairs);
		exceptions = buffer.getString("exceptions", exceptions);
	}
	
	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("extractIsolatedStrings", extractStandalone);
		buffer.setBoolean("extractAllPairs", extractAllPairs);
		buffer.setString("exceptions", exceptions);
		return buffer.toString();
	}

}
