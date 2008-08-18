/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.xliff;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

	protected boolean   useStateValues;
	protected boolean   extractOnlyMatchingValues;
	protected String    stateValues;
	protected boolean   extractNoState;
	protected boolean   fallbackToID;
	protected boolean   escapeGT;
	

	public Parameters () {
		reset();
	}
	
	public void reset () {
		super.reset();
		useStateValues = true;
		stateValues = "new|needs-translation";
		extractOnlyMatchingValues = true;
		extractNoState = true;
		fallbackToID = true;
		escapeGT = false;
	}

	public String toString ()
	{
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("useStateValues", useStateValues);
		tmp.add("extractOnlyMatchingValues", extractOnlyMatchingValues);
		tmp.add("stateValues", stateValues);
		tmp.add("extractNoState", extractNoState);
		tmp.add("fallbackToID", fallbackToID);
		tmp.add("escapeGT", escapeGT);
		return tmp.toString();
	}
	
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);

		// Parse the fields
		useStateValues = tmp.get("useStateValues", useStateValues);
		extractOnlyMatchingValues = tmp.get("extractOnlyMatchingValues", extractOnlyMatchingValues);
		stateValues = tmp.get("stateValues", stateValues);
		extractNoState = tmp.get("extractNoState", extractNoState);
		fallbackToID = tmp.get("fallbackToID", fallbackToID);
		escapeGT = tmp.get("escapeGT", escapeGT);
	}
}
