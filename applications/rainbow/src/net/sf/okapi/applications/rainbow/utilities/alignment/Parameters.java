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

public class Parameters extends BaseParameters {
	
	public String       tmxPath;
	public String       tmPath;
	public boolean      segment;
	public String       sourceSrxPath;
	public String       targetSrxPath;
	public boolean      useTradosWorkarounds;
	public boolean      checkSingleSegUnit;
	public boolean      useAutoCorrection;
	public boolean      createTM;
	

	public Parameters () {
		reset();
	}
	
	public void reset () {
		tmxPath = "";
		tmPath = "";
		segment = false;
		sourceSrxPath = "";
		targetSrxPath = "";
		useTradosWorkarounds = true;
		checkSingleSegUnit = true;
		useAutoCorrection = true;
		createTM = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		tmxPath = buffer.getString("tmxPath", tmxPath);
		tmPath = buffer.getString("simpletmPath", tmPath);
		segment = buffer.getBoolean("segment", segment);
		sourceSrxPath = buffer.getString("sourceSrxPath", sourceSrxPath);
		targetSrxPath = buffer.getString("targetSrxPath", targetSrxPath);
		useTradosWorkarounds = buffer.getBoolean("useTradosWorkarounds", useTradosWorkarounds);
		checkSingleSegUnit = buffer.getBoolean("checkSingleSegUnit", checkSingleSegUnit);
		useAutoCorrection = buffer.getBoolean("useAutoCorrection", useAutoCorrection);
		createTM = buffer.getBoolean("createTM", createTM);
	}

	public String toString () {
		buffer.reset();
		buffer.setString("tmxPath", tmxPath);
		buffer.setString("simpletmPath", tmPath);
		buffer.setString("sourceSrxPath", sourceSrxPath);
		buffer.setString("targetSrxPath", targetSrxPath);
		buffer.setBoolean("segment", segment);
		buffer.setBoolean("useTradosWorkarounds", useTradosWorkarounds);
		buffer.setBoolean("checkSingleSegUnit", checkSingleSegUnit);
		buffer.setBoolean("useAutoCorrection", useAutoCorrection);
		buffer.setBoolean("createTM", createTM);
		return buffer.toString();
	}
}
