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

package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	public static final int  TYPE_KEEPORIGINAL   = 0;
	public static final int  TYPE_XNREPLACE      = 1;
	
	protected int       type;
	protected boolean   addPrefix;
	protected String    prefix;
	protected boolean   addSuffix;
	protected String    suffix;
	protected boolean   applyToExistingTarget;
	protected boolean   addName;
	protected boolean   addID;
	
	
	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		type = tmp.get("type", type);
		addPrefix = tmp.get("addPrefix", addPrefix);
		prefix = tmp.get("prefix", prefix);
		addSuffix = tmp.get("addSuffix", addSuffix);
		suffix = tmp.get("suffix", suffix);
		applyToExistingTarget = tmp.get("applyToExistingTarget", applyToExistingTarget);
		addName = tmp.get("addName", addName);
		addID = tmp.get("addID", addID);
	}

	@Override
	public void reset() {
		type = 0;
		addPrefix = false;
		prefix = "[[";
		addSuffix = false;
		suffix = "]]";
		applyToExistingTarget = false;
		addName = false;
		addID = false;
	}

	@Override
	public String toString() {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("type", type);
		tmp.add("addPrefix", addPrefix);
		tmp.add("prefix", prefix);
		tmp.add("addSuffix", addSuffix);
		tmp.add("suffix", suffix);
		tmp.add("applyToExistingTarget", applyToExistingTarget);
		tmp.add("addName", addName);
		tmp.add("addID", addID);
		return tmp.toString();
	}
	
}
