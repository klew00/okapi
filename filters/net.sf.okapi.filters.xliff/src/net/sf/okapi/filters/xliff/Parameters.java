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
============================================================================*/

package net.sf.okapi.filters.xliff;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	protected boolean useStateValues;
	protected boolean extractOnlyMatchingValues;
	protected String stateValues;
	protected boolean extractNoState;
	protected boolean fallbackToID;
	protected boolean escapeGT;

	public Parameters () {
		reset();
		toString(); // fill the list
	}
	
	public void reset () {
		useStateValues = true;
		stateValues = "new|needs-translation";
		extractOnlyMatchingValues = true;
		extractNoState = true;
		fallbackToID = true;
		escapeGT = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		useStateValues = buffer.getBoolean("useStateValues", useStateValues);
		extractOnlyMatchingValues = buffer.getBoolean("extractOnlyMatchingValues", extractOnlyMatchingValues);
		stateValues = buffer.getString("stateValues", stateValues);
		extractNoState = buffer.getBoolean("extractNoState", extractNoState);
		fallbackToID = buffer.getBoolean("fallbackToID", fallbackToID);
		escapeGT = buffer.getBoolean("escapeGT", escapeGT);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("useStateValues", useStateValues);
		buffer.setBoolean("extractOnlyMatchingValues", extractOnlyMatchingValues);
		buffer.setString("stateValues", stateValues);
		buffer.setBoolean("extractNoState", extractNoState);
		buffer.setBoolean("fallbackToID", fallbackToID);
		buffer.setBoolean("escapeGT", escapeGT);
		return buffer.toString();
	}
	
}
