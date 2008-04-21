/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Borneo.Actions;

import net.sf.okapi.Library.Base.IParameters;

public abstract class BaseAction implements IAction {

	int                 m_nDocTotal;
	int                 m_nCurrentDoc;

	public static final String    ID_EXTRACTSOURCE         = "extractsource";
	public static final String    ID_UPDATESOURCE          = "updatesource";
	public static final String    ID_UPDATETARGET          = "updatetarget";
	public static final String    ID_GENERATETARGET        = "generatetarget";
	public static final String    ID_EXPORTPACKAGE         = "exportpackage";
	public static final String    ID_IMPORTPACKAGE         = "importpackage";
	public static final String    ID_IMPORTTRANSLATION     = "importtranslation";
	

	public abstract boolean execute (int[] p_aDKeys,
		String[] p_aTargets);

	public abstract String getID ();

	public abstract String getName ();

	public IParameters getOptions () {
		return null;
	}

	public boolean hasOptions () {
		return false;
	}

	public boolean hasResultToOpen () {
		return false;
	}

	public boolean isFileSetLevel () {
		return false;
	}

	public boolean isManual () {
		return false;
	}

	public boolean isOptional () {
		return false;
	}

	public boolean needsTarget () {
		return false;
	}

	public void openResult () {
		// Do nothing by default
	}

	public void setOptions (IParameters p_Value) {
		// Do nothing by default
	}

	public abstract boolean start ();

	public abstract void stop ();
}
