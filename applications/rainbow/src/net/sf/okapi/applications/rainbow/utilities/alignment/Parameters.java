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

package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	protected String    tmxPath;
	protected boolean   segment;
	protected String    srxPath;
	protected boolean   singleInput;   
	protected boolean   allowEmptyTarget;   
	

	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		tmxPath = tmp.get("tmxPath", tmxPath);
		segment = tmp.get("segment", segment);
		srxPath = tmp.get("srxPath", srxPath);
		singleInput = tmp.get("singleInput", singleInput);
		allowEmptyTarget = tmp.get("allowEmptyTarget", allowEmptyTarget);
	}

	@Override
	public void reset () {
		tmxPath = "output.tmx";
		segment = false;
		srxPath = "default.srx";
		singleInput = true;
		allowEmptyTarget = false;
	}

	@Override
	public String toString () {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("tmxPath", tmxPath);
		tmp.add("srxPath", srxPath);
		tmp.add("segment", segment);
		tmp.add("singleInput", singleInput);
		tmp.add("allowEmptyTarget", allowEmptyTarget);
		return tmp.toString();
	}
}
