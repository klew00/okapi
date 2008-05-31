/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.xliff;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

	protected boolean                useStateValues;
	protected boolean                extractOnlyMatchingValues;
	protected String                 stateValues;
	protected boolean                extractNoState;
	protected boolean                fallbackToID;

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
	}

	public String toString ()
	{
		// Store the parameters in fields
		FieldsString Tmp = new FieldsString();
		Tmp.add("useStateValues", useStateValues);
		Tmp.add("extractOnlyMatchingValues", extractOnlyMatchingValues);
		Tmp.add("stateValues", stateValues);
		Tmp.add("extractNoState", extractNoState);
		Tmp.add("fallbackToID", fallbackToID);
		return Tmp.toString();
	}
	
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString Tmp = new FieldsString(data);

		// Parse the fields
		useStateValues = Tmp.get("useStateValues", useStateValues);
		extractOnlyMatchingValues = Tmp.get("extractOnlyMatchingValues", extractOnlyMatchingValues);
		stateValues = Tmp.get("stateValues", stateValues);
		extractNoState = Tmp.get("extractNoState", extractNoState);
		fallbackToID = Tmp.get("fallbackToID", fallbackToID);
	}
}
