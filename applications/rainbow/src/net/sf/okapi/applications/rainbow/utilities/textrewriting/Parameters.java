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
	protected boolean   segment;
	protected String    sourceSrxPath;
	protected String    targetSrxPath;
	
	
	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString (String data) {
		reset();
		super.fromString(data);
		type = getInteger("type", type);
		addPrefix = getBoolean("addPrefix", addPrefix);
		prefix = getString("prefix", prefix);
		addSuffix = getBoolean("addSuffix", addSuffix);
		suffix = getString("suffix", suffix);
		applyToExistingTarget = getBoolean("applyToExistingTarget", applyToExistingTarget);
		addName = getBoolean("addName", addName);
		addID = getBoolean("addID", addID);
		segment = getBoolean("segment", segment);
		sourceSrxPath = getString("sourceSrxPath", sourceSrxPath);
		targetSrxPath = getString("targetSrxPath", targetSrxPath);
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
		segment = false;
		sourceSrxPath = "";
		targetSrxPath = "";
	}

	@Override
	public String toString() {
		setInteger("type", type);
		setBoolean("addPrefix", addPrefix);
		setString("prefix", prefix);
		setBoolean("addSuffix", addSuffix);
		setString("suffix", suffix);
		setBoolean("applyToExistingTarget", applyToExistingTarget);
		setBoolean("addName", addName);
		setBoolean("addID", addID);
		setBoolean("segment", segment);
		setString("sourceSrxPath", sourceSrxPath);
		setString("targetSrxPath", targetSrxPath);
		return super.toString();
	}
	
}
