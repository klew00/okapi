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
	
	protected String    tmxPath;
	protected boolean   segment;
	protected String    sourceSrxPath;
	protected String    targetSrxPath;
	protected boolean   useTradosWorkarounds;
	protected boolean   checkSingleSegUnit;
	

	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString (String data) {
		reset();
		super.fromString(data);
		tmxPath = getParameter("tmxPath", tmxPath);
		segment = getParameter("segment", segment);
		sourceSrxPath = getParameter("sourceSrxPath", sourceSrxPath);
		targetSrxPath = getParameter("targetSrxPath", targetSrxPath);
		useTradosWorkarounds = getParameter("useTradosWorkarounds", useTradosWorkarounds);
		checkSingleSegUnit = getParameter("checkSingleSegUnit", checkSingleSegUnit);
	}

	@Override
	public void reset () {
		super.reset();
		tmxPath = "output.tmx";
		segment = false;
		sourceSrxPath = "";
		targetSrxPath = "";
		useTradosWorkarounds = true;
		checkSingleSegUnit = true;
	}

	@Override
	public String toString () {
		setParameter("tmxPath", tmxPath);
		setParameter("sourceSrxPath", sourceSrxPath);
		setParameter("targetSrxPath", targetSrxPath);
		setParameter("segment", segment);
		setParameter("useTradosWorkarounds", useTradosWorkarounds);
		setParameter("checkSingleSegUnit", checkSingleSegUnit);
		return super.toString();
	}
}
